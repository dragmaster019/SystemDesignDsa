package Model;

public class User {
    public String name;
    public Long mobileNumber;
    public String city;
    public Integer userId;

    public User(String name, Long mobileNumber, String city, Integer userId) {
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.city = city;
        this.userId = userId;
    }
}
