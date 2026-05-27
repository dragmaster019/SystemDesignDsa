package Model;

public class Item {
    public Integer itemId;
    public String name;
    public Integer weight;
    public Integer senderId;
    public String receiverName;
    public String receiverCity;
    public Integer officeId;
    public ParcelStatus status;
    public Integer deliveryPartnerId;

    public Item(Integer itemId, String name, Integer weight,
                Integer senderId, String receiverName, String receiverCity, Integer officeId) {
        this.itemId = itemId;
        this.name = name;
        this.weight = weight;
        this.senderId = senderId;
        this.receiverName = receiverName;
        this.receiverCity = receiverCity;
        this.officeId = officeId;
        this.status = ParcelStatus.BOOKED;
        this.deliveryPartnerId = null;
    }
}
