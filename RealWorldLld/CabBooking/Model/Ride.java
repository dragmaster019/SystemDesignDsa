package Model;

public class Ride {
    private Integer    rideId;
    private Integer    userId;
    private Integer    driverId;
    private String     pickupLocation;
    private String     dropLocation;
    private double     fare;
    private RideStatus status;

    public Ride(Integer rideId, Integer userId, Integer driverId,
                String pickupLocation, String dropLocation) {
        this.rideId          = rideId;
        this.userId          = userId;
        this.driverId        = driverId;
        this.pickupLocation  = pickupLocation;
        this.dropLocation    = dropLocation;
        this.fare            = 0;
        this.status          = RideStatus.REQUESTED;
    }

    public Integer    getRideId()         { return rideId; }
    public Integer    getUserId()         { return userId; }
    public Integer    getDriverId()       { return driverId; }
    public String     getPickupLocation() { return pickupLocation; }
    public String     getDropLocation()   { return dropLocation; }
    public double     getFare()           { return fare; }
    public RideStatus getStatus()         { return status; }

    public void setDriverId(Integer driverId) { this.driverId = driverId; }
    public void setFare(double fare)          { this.fare = fare; }
    public void setStatus(RideStatus status)  { this.status = status; }
}
