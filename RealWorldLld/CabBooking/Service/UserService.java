package Service;

import Model.*;
import DataSet.Data;
import java.util.Stack;

public class UserService {

    private SystemService systemService = new SystemService();

    public void bookRide(int userId, String pickup, String drop) {
        User user = Data.userMap.get(userId);
        if (user == null) { System.out.println("User not found."); return; }

        if (user.getActiveRideId() != null) {
            System.out.println("[UserService] " + user.getName() + " already has an active ride #" + user.getActiveRideId());
            return;
        }

        // create ride
        Ride ride = new Ride(Data.rideCounter++, userId, null, pickup, drop);
        Data.rideMap.put(ride.getRideId(), ride);

        // find nearby driver
        Driver driver = systemService.findNearbyDriver(pickup);

        if (driver != null) {
            // assign immediately
            ride.setDriverId(driver.getDriverId());
            ride.setStatus(RideStatus.ASSIGNED);
            driver.setAvailable(false);
            driver.setActiveRideId(ride.getRideId());
            user.setActiveRideId(ride.getRideId());

            System.out.println("[UserService] Ride #" + ride.getRideId() + " booked!"
                    + " | " + pickup + " → " + drop
                    + " | Driver: " + driver.getVehicleNumber()
                    + " (rating: " + driver.getRating() + ")"
                    + " | Status: " + ride.getStatus());
        } else {
            // no driver — add to waiting queue
            Data.pendingRides.add(ride);
            user.setActiveRideId(ride.getRideId());
            System.out.println("[UserService] No driver available at " + pickup
                    + ". Ride #" + ride.getRideId() + " added to queue. Position: " + Data.pendingRides.size());
        }
    }

    public void cancelRide(int userId, int rideId) {
        User user = Data.userMap.get(userId);
        Ride ride = Data.rideMap.get(rideId);

        if (user == null || ride == null) { System.out.println("User or Ride not found."); return; }
        if (ride.getStatus() == RideStatus.COMPLETED) {
            System.out.println("[UserService] Cannot cancel a completed ride.");
            return;
        }

        ride.setStatus(RideStatus.CANCELLED);
        user.setActiveRideId(null);

        // free driver if assigned
        if (ride.getDriverId() != null) {
            Driver driver = Data.driverMap.get(ride.getDriverId());
            if (driver != null) {
                driver.setAvailable(true);
                driver.setActiveRideId(null);
            }
        }

        System.out.println("[UserService] Ride #" + rideId + " cancelled.");

        // process next pending ride
        processQueue();
    }

    public void viewRideHistory(int userId) {
        Stack<Ride> history = Data.rideHistory.get(userId);
        if (history == null || history.isEmpty()) {
            System.out.println("[UserService] No ride history for user #" + userId);
            return;
        }
        System.out.println("[UserService] Ride history for user #" + userId + ":");
        int count = 0;
        // peek top 5 without destroying stack
        Object[] arr = history.toArray();
        for (int i = arr.length - 1; i >= 0 && count < 5; i--, count++) {
            Ride r = (Ride) arr[i];
            System.out.println("  Ride #" + r.getRideId()
                    + " | " + r.getPickupLocation() + " → " + r.getDropLocation()
                    + " | Fare: Rs." + r.getFare()
                    + " | Status: " + r.getStatus());
        }
    }

    private void processQueue() {
        if (Data.pendingRides.isEmpty()) return;
        Ride next   = Data.pendingRides.peek();
        Driver driver = systemService.findNearbyDriver(next.getPickupLocation());
        if (driver == null) return;

        Data.pendingRides.poll();
        next.setDriverId(driver.getDriverId());
        next.setStatus(RideStatus.ASSIGNED);
        driver.setAvailable(false);
        driver.setActiveRideId(next.getRideId());

        User user = Data.userMap.get(next.getUserId());
        if (user != null) user.setActiveRideId(next.getRideId());

        System.out.println("[UserService] Pending ride #" + next.getRideId()
                + " assigned to driver: " + driver.getVehicleNumber());
    }
}
