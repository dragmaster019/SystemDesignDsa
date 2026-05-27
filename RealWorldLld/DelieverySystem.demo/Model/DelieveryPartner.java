package Model;

public class DelieveryPartner {
    public String name;
    public String vehicleNumber;
    public Integer partnerId;
    public boolean isAvailable;

    public DelieveryPartner(String name, String vehicleNumber, Integer partnerId) {
        this.name = name;
        this.vehicleNumber = vehicleNumber;
        this.partnerId = partnerId;
        this.isAvailable = true;
    }
}
