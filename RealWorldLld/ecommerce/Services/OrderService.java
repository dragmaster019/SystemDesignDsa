package Services;

import DataSet.DataStore;
import Model.CartItem;
import Model.Order;
import Model.enums.OrderStatus;
import builder.OrderBuilder;
import observer.OrderObserver;
import java.util.*;

// OrderService owns the order lifecycle and notifies all registered observers on every status change
public class OrderService {
    private DataStore dataStore = DataStore.getInstance();
    // Open/Closed: add new observers (email, SMS, push) without touching this class
    private List<OrderObserver> observers = new ArrayList<>();

    public void registerObserver(OrderObserver observer) {
        observers.add(observer);
    }

    public Order createOrder(int userId, int dealerId, Map<Integer, CartItem> cartSnapshot) {
        int orderId = dataStore.nextOrderId();

        // Builder pattern: readable, validated construction of an Order with many fields
        Order order = new OrderBuilder()
                .setOrderId(orderId)
                .setUserId(userId)
                .setDealerId(dealerId)
                .setCartSnapshot(new LinkedHashMap<>(cartSnapshot))
                .setStatus(OrderStatus.PLACED)
                .setCreatedAt(System.currentTimeMillis())
                .build();

        dataStore.getOrders().put(orderId, order);

        // FIFO dealer queue — dealer always processes oldest order first (ArrayDeque.offer: O(1))
        dataStore.getDealerOrderQueues()
                .computeIfAbsent(dealerId, k -> new ArrayDeque<>())
                .offer(orderId);

        // User history: most recent order at front (ArrayDeque.offerFirst: O(1))
        dataStore.getUserOrderHistory()
                .computeIfAbsent(userId, k -> new ArrayDeque<>())
                .offerFirst(orderId);

        notifyObservers(orderId, OrderStatus.PLACED, userId, dealerId);
        return order;
    }

    public void updateStatus(int orderId, OrderStatus newStatus) {
        Order order = dataStore.getOrders().get(orderId);
        if (order == null) { System.out.println("  [ORDER] Not found: #" + orderId); return; }
        order.setStatus(newStatus);
        notifyObservers(orderId, newStatus, order.getUserId(), order.getDealerId());
    }

    public void viewOrderStatus(int orderId) {
        Order order = dataStore.getOrders().get(orderId);
        if (order == null) { System.out.println("  Order #" + orderId + " not found."); return; }
        System.out.println("  Order #" + orderId + " | Status: " + order.getStatus() + " | Dealer: #" + order.getDealerId());
    }

    public void viewOrderHistory(int userId) {
        Deque<Integer> history = dataStore.getUserOrderHistory().get(userId);
        System.out.println("\n--- Order History (User #" + userId + ") ---");
        if (history == null || history.isEmpty()) {
            System.out.println("  No orders yet.");
        } else {
            history.forEach(oid -> {
                Order o = dataStore.getOrders().get(oid);
                double total = o.getCartSnapshot().values().stream()
                        .mapToDouble(i -> i.getPriceAtAddTime() * i.getQuantity()).sum();
                System.out.println("  Order #" + oid + " | Status: " + o.getStatus() + " | ₹" + total);
            });
        }
        System.out.println("------------------------------------------");
    }

    private void notifyObservers(int orderId, OrderStatus status, int userId, int dealerId) {
        for (OrderObserver observer : observers) {
            observer.onOrderStatusChange(orderId, status, userId, dealerId);
        }
    }
}
