package Model;

public class User {
    private int userId;
    private String name;
    private String phoneNumber;
    private String shippingAddress;
    private int cartId;

    public User(int userId, String name, String phoneNumber, String shippingAddress) {
        this.userId = userId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.shippingAddress = shippingAddress;
        this.cartId = -1;
    }

    public int getUserId() { return userId; }
    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getShippingAddress() { return shippingAddress; }
    public int getCartId() { return cartId; }

    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    public void setCartId(int cartId) { this.cartId = cartId; }
}
