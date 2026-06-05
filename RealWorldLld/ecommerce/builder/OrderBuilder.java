package builder;

import Model.CartItem;
import Model.Order;
import Model.enums.OrderStatus;
import java.util.Map;

/*
 * Builder pattern — Order has 6 required fields. Without Builder, the constructor call
 * looks like: new Order(1, 2, 3, items, PLACED, 1717000000L) — impossible to read.
 * Builder lets you construct step-by-step and validates at build() time.
 */
public class OrderBuilder {
    private int orderId;
    private int userId;
    private int dealerId;
    private Map<Integer, CartItem> cartSnapshot;
    private OrderStatus status = OrderStatus.PLACED;
    private long createdAt = System.currentTimeMillis();

    public OrderBuilder setOrderId(int orderId) {
        this.orderId = orderId;
        return this;
    }

    public OrderBuilder setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public OrderBuilder setDealerId(int dealerId) {
        this.dealerId = dealerId;
        return this;
    }

    public OrderBuilder setCartSnapshot(Map<Integer, CartItem> cartSnapshot) {
        this.cartSnapshot = cartSnapshot;
        return this;
    }

    public OrderBuilder setStatus(OrderStatus status) {
        this.status = status;
        return this;
    }

    public OrderBuilder setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Order build() {
        if (userId <= 0) throw new IllegalStateException("Order requires a valid userId");
        if (dealerId <= 0) throw new IllegalStateException("Order requires a valid dealerId");
        if (cartSnapshot == null || cartSnapshot.isEmpty()) throw new IllegalStateException("Order cart cannot be empty");
        return new Order(orderId, userId, dealerId, cartSnapshot, status, createdAt);
    }
}
