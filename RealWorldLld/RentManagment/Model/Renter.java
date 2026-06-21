public class Renter{

    private int renterId;
    private int budget;
    private RoomType roomType;
    private String location;

    public Renter(int renterId, int budget, RoomType roomType, String location) {
        this.renterId = renterId;
        this.budget = budget;
        this.roomType = roomType;
        this.location = location;
    }

    public int getRenterId() {
        return renterId;
    }

    public int getBudget() {
        return budget;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public String getLocation() {
        return location;
    }
    
}