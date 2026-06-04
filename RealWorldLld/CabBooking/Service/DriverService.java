package Service;

import Model.*;
import DataSet.Data;

public class DriverService {

    private SystemService systemService = new SystemService();

    public void acceptRide(int driverId, int rideId) {
        Driver driver = Data.driverMap.get(driverId);
        Ride   ride   = Data.rideMap.get(rideId);

        if (driver == null || ride == null) { System.out.println("Driver or Ride not found."); return; }
        if (ride.getStatus() != RideStatus.ASSIGNED) {
            System.out.println("[DriverService] Ride #" + rideId + " is not in ASSIGNED state.");
            return;
        }

        System.out.println("[DriverService] Driver " + driver.getVehicleNumber()
                + " accepted Ride #" + rideId
                + " | Heading to: " + ride.getPickupLocation());
    }

    public void rejectRide(int driverId, int rideId) {
        Driver driver = Data.driverMap.get(driverId);
        Ride   ride   = Data.rideMap.get(rideId);

        if (driver == null || ride == null) { System.out.println("Driver or Ride not found."); return; }

        // free this driver, find another
        driver.setAvailable(true);
        driver.setActiveRideId(null);
        ride.setDriverId(null);
        ride.setStatus(RideStatus.REQUESTED);

        System.out.println("[DriverService] Driver " + driver.getVehicleNumber()
                + " rejected Ride #" + rideId + ". Finding another driver...");

        Driver next = systemService.findNearbyDriver(ride.getPickupLocation());
        if (next != null) {
            ride.setDriverId(next.getDriverId());
            ride.setStatus(RideStatus.ASSIGNED);
            next.setAvailable(false);
            next.setActiveRideId(rideId);
            System.out.println("[DriverService] Ride #" + rideId + " reassigned to: " + next.getVehicleNumber());
        } else {
            Data.pendingRides.add(ride);
            System.out.println("[DriverService] No driver found. Ride #" + rideId + " back in queue.");
        }
    }

    public void completeRide(int driverId, int rideId) {
        Driver driver = Data.driverMap.get(driverId);
        Ride   ride   = Data.rideMap.get(rideId);

        if (driver == null || ride == null) { System.out.println("Driver or Ride not found."); return; }

        // calculate fare
        double fare = systemService.calculateFare(ride.getPickupLocation(), ride.getDropLocation());
        ride.setFare(fare);
        ride.setStatus(RideStatus.COMPLETED);

        // update driver — move to drop location, free up
        driver.setCurrentLocation(ride.getDropLocation());
        driver.setAvailable(true);
        driver.setActiveRideId(null);

        // update user
        User user = Data.userMap.get(ride.getUserId());
        if (user != null) user.setActiveRideId(null);

        // push to ride history stack
        Data.rideHistory
            .computeIfAbsent(ride.getUserId(), k -> new java.util.Stack<>())
            .push(ride);

        System.out.println("[DriverService] Ride #" + rideId + " completed!"
                + " | " + ride.getPickupLocation() + " → " + ride.getDropLocation()
                + " | Fare: Rs." + fare
                + " | Driver now at: " + driver.getCurrentLocation());

        // check pending queue — this driver may serve next ride
        if (!Data.pendingRides.isEmpty()) {
            Ride next = Data.pendingRides.peek();
            if (next.getPickupLocation().equalsIgnoreCase(driver.getCurrentLocation())) {
                Data.pendingRides.poll();
                next.setDriverId(driverId);
                next.setStatus(RideStatus.ASSIGNED);
                driver.setAvailable(false);
                driver.setActiveRideId(next.getRideId());
                User nextUser = Data.userMap.get(next.getUserId());
                if (nextUser != null) nextUser.setActiveRideId(next.getRideId());
                System.out.println("[DriverService] Pending ride #" + next.getRideId()
                        + " assigned to " + driver.getVehicleNumber());
            }
        }
    }
}
