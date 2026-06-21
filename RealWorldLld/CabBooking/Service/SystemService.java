package Service;

import Model.*;
import DataSet.Data;

public class SystemService {

    // finds highest rated available driver at the pickup location
    public Driver findNearbyDriver(String pickupLocation) {
        Driver best = null;
        for (Driver d : Data.allDrivers) {
            if (d.isAvailable() && d.getCurrentLocation().equalsIgnoreCase(pickupLocation)) {
                if (best == null || d.getRating() > best.getRating()) {
                    best = d;
                }
            }
        }
        return best;
    }

    public double calculateFare(String pickup, String drop) {
        String key      = pickup + "-" + drop;
        Integer distance = Data.distanceMap.get(key);
        if (distance == null) {
            System.out.println("[SystemService] Route not found: " + key);
            return 0;
        }
        return distance * Data.RATE_PER_KM;
    }

    public void updateDriverAvailability(int driverId, boolean available) {
        Driver driver = Data.driverMap.get(driverId);
        if (driver != null) driver.setAvailable(available);
    }

    public void updateRideStatus(int rideId, RideStatus status) {
        Ride ride = Data.rideMap.get(rideId);
        if (ride != null) ride.setStatus(status);
    }
}
