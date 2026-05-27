package Model;

import java.util.Date;

public class Vehicle {
    private String vehicleNumber;
    private String vehicleType;
    private Date   entryTime;

    public Vehicle(String vehicleNumber, String vehicleType) {
        this.vehicleNumber = vehicleNumber;
        this.vehicleType   = vehicleType;
        this.entryTime     = new Date();
    }

    public String getVehicleNumber() { return vehicleNumber; }
    public String getVehicleType()   { return vehicleType; }
    public Date   getEntryTime()     { return entryTime; }
}
