package Model;

public class User {
    private Integer userId;
    private String  name;
    private String  phoneNumber;
    private Integer activeRideId;   // null = no active ride

    public User(int userId, String name, String phoneNumber) {
        this.userId      = userId;
        this.name        = name;
        this.phoneNumber = phoneNumber;
        this.activeRideId = null;
    }

    public Integer getUserId()     { return userId; }
    public String  getName()       { return name; }
    public String  getPhoneNumber(){ return phoneNumber; }
    public Integer getActiveRideId(){ return activeRideId; }

    public void setActiveRideId(Integer activeRideId) {
        this.activeRideId = activeRideId;
    }
}
