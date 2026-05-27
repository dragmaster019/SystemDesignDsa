package Services;

import Model.*;
import DataSet.Maps;

public class vehilceService {

    public boolean isSpaceAvailable() {
        for (ParkingSpot spot : Maps.spotMap.values()) {
            if (spot.isAvailable) return true;
        }
        return false;
    }

    public void showParkingStatus() {
        System.out.println("--- Parking Status ---");
        for (ParkingSpot spot : Maps.spotMap.values()) {
            if (spot.isAvailable) {
                System.out.println("  Spot #" + spot.spotId + " → AVAILABLE");
            } else {
                System.out.println("  Spot #" + spot.spotId + " → OCCUPIED by " + spot.parkedVehicleNumber);
            }
        }
        System.out.println("  Waiting queue: " + Maps.waitingQueue.size());
        System.out.println("----------------------\n");
    }
}
