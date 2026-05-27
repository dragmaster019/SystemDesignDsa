package Model;

public class ParkingSpot {
    public int     spotId;
    public boolean isAvailable;
    public String  parkedVehicleNumber;

    public ParkingSpot(int spotId) {
        this.spotId               = spotId;
        this.isAvailable          = true;
        this.parkedVehicleNumber  = null;
    }
}
