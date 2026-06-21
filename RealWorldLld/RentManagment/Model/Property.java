public class Property{

    private int propertyId;
    private int ownerId;
    private RoomType roomType;
    private String location;
    private int price;


    public Property(int propertyId, int ownerId, RoomType roomType, String location, int price) {
        this.propertyId = propertyId;
        this.ownerId = ownerId;
        this.roomType = roomType;
        this.location = location;
        this.price = price;
    }

    public int getPropertyId() {
        return propertyId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public String getLocation() {
        return location;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

}