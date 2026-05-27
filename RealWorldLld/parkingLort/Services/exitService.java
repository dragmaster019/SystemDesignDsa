package Services;

import Model.*;
import DataSet.Maps;
import java.util.Date;

public class exitService {

    private static final double BIKE_RATE = 10.0;  // ₹10 per hour
    private static final double CAR_RATE  = 20.0;  // ₹20 per hour

    public void exitVehicle(String vehicleNumber) {
        Ticket ticket = Maps.activeTickets.get(vehicleNumber);
        if (ticket == null) {
            System.out.println("[ExitService] No active ticket for " + vehicleNumber);
            return;
        }

        // calculate time and fee
        ticket.exitTime      = new Date();
        long   durationMs    = ticket.exitTime.getTime() - ticket.entryTime.getTime();
        double hours         = Math.max(1, Math.ceil(durationMs / 3600000.0)); // minimum 1 hour
        double rate          = ticket.vehicleType.equalsIgnoreCase("Bike") ? BIKE_RATE : CAR_RATE;
        ticket.fee           = hours * rate;

        // free the spot
        ParkingSpot spot          = Maps.spotMap.get(ticket.spotId);
        spot.isAvailable          = true;
        spot.parkedVehicleNumber  = null;

        Maps.activeTickets.remove(vehicleNumber);

        System.out.println("[ExitService] " + vehicleNumber
                + " exited | Duration: " + (durationMs / 1000) + "s"
                + " | Fee: Rs." + ticket.fee);

        // assign freed spot to next waiting vehicle
        if (!Maps.waitingQueue.isEmpty()) {
            String[] next = Maps.waitingQueue.poll();
            System.out.println("[ExitService] Spot freed! Assigning to waiting vehicle: " + next[0]);
            new entryService().assignSpot(spot, next[0], next[1]);
        }
    }
}
