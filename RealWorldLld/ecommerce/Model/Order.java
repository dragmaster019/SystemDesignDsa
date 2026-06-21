package Model;

import Model.enums.OrderStatus;
import java.util.Map;

public class Order {
    private int orderId;
    private int userId;
    private int dealerId;
    private Map<Integer, CartItem> cartSnapshot;
    private OrderStatus status;
    private int deliveryId;
    private long createdAt;

    // Called only through OrderBuilder — never construct Order directly
    public Order(int orderId, int userId, int dealerId, Map<Integer, CartItem> cartSnapshot,
          OrderStatus status, long createdAt) {
        this.orderId = orderId;
        this.userId = userId;
        this.dealerId = dealerId;
        this.cartSnapshot = cartSnapshot;
        this.status = status;
        this.createdAt = createdAt;
        this.deliveryId = -1;
    }

    public int getOrderId() { return orderId; }
    public int getUserId() { return userId; }
    public int getDealerId() { return dealerId; }
    public Map<Integer, CartItem> getCartSnapshot() { return cartSnapshot; }
    public OrderStatus getStatus() { return status; }
    public int getDeliveryId() { return deliveryId; }
    public long getCreatedAt() { return createdAt; }

    public void setStatus(OrderStatus status) { this.status = status; }
    public void setDeliveryId(int deliveryId) { this.deliveryId = deliveryId; }
}
