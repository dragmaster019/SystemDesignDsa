package RealWorldLld.SwiggyDemo.Model;

import java.util.List;

public class Order {
    public Integer orderId;
    public Integer userId;
    public Integer restaurantId;
    public List<MenuItem> items;
    public OrderStatus status;
    public Integer deliveryPartnerId;

    public Order(Integer orderId, Integer userId, Integer restaurantId, List<MenuItem> items) {
        this.orderId = orderId;
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.items = items;
        this.status = OrderStatus.PLACED;
        this.deliveryPartnerId = null;
    }
}
