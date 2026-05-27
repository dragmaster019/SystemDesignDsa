package Services;

import Model.*;
import DataSet.Maps;

public class entryService {

    public void parkVehicle(String vehicleNumber, String vehicleType) {
        ParkingSpot spot = getAvailableSpot();

        if (spot == null) {
            Maps.waitingQueue.add(new String[]{vehicleNumber, vehicleType});
            System.out.println("[EntryService] No space! " + vehicleNumber
                    + " added to waiting queue. Position: " + Maps.waitingQueue.size());
            return;
        }

        assignSpot(spot, vehicleNumber, vehicleType);
    }

    public void assignSpot(ParkingSpot spot, String vehicleNumber, String vehicleType) {
        spot.isAvailable         = false;
        spot.parkedVehicleNumber = vehicleNumber;

        Ticket ticket = new Ticket(Maps.ticketCounter++, vehicleNumber, vehicleType, spot.spotId);
        Maps.activeTickets.put(vehicleNumber, ticket);

        System.out.println("[EntryService] " + vehicleType + " " + vehicleNumber
                + " parked at Spot #" + spot.spotId
                + " | Entry: " + ticket.entryTime);
    }

    private ParkingSpot getAvailableSpot() {
        for (ParkingSpot spot : Maps.spotMap.values()) {
            if (spot.isAvailable) return spot;
        }
        return null;
    }
}
