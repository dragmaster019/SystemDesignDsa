package Model;

import java.util.Date;

public class Ticket {
    public int    ticketId;
    public String vehicleNumber;
    public String vehicleType;
    public int    spotId;
    public Date   entryTime;
    public Date   exitTime;
    public double fee;

    public Ticket(int ticketId, String vehicleNumber, String vehicleType, int spotId) {
        this.ticketId      = ticketId;
        this.vehicleNumber = vehicleNumber;
        this.vehicleType   = vehicleType;
        this.spotId        = spotId;
        this.entryTime     = new Date();
        this.exitTime      = null;
        this.fee           = 0;
    }
}
