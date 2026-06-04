package Model;

public class Driver {
    private Integer driverId;
    private String  vehicleNumber;
    private String  currentLocation;
    private boolean isAvailable;
    private double  rating;
    private Integer activeRideId;   // null = no active ride

    public Driver(Integer driverId, String vehicleNumber, String currentLocation, double rating) {
        this.driverId         = driverId;
        this.vehicleNumber    = vehicleNumber;
        this.currentLocation  = currentLocation;
        this.isAvailable      = true;
        this.rating           = rating;
        this.activeRideId     = null;
    }

    public Integer getDriverId()        { return driverId; }
    public String  getVehicleNumber()   { return vehicleNumber; }
    public String  getCurrentLocation() { return currentLocation; }
    public boolean isAvailable()        { return isAvailable; }
    public double  getRating()          { return rating; }
    public Integer getActiveRideId()    { return activeRideId; }

    public void setAvailable(boolean available)       { this.isAvailable = available; }
    public void setCurrentLocation(String location)   { this.currentLocation = location; }
    public void setActiveRideId(Integer activeRideId) { this.activeRideId = activeRideId; }
}
