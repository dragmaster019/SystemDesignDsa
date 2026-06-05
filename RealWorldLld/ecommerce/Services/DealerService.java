package Services;

import DataSet.DataStore;
import Model.Dealer;
import Model.Order;
import Model.Product;
import Model.enums.OrderStatus;
import java.util.*;

public class DealerService {
    private DataStore dataStore = DataStore.getInstance();
    private OrderService orderService;
    private DeliveryService deliveryService;

    public DealerService(OrderService orderService, DeliveryService deliveryService) {
        this.orderService = orderService;
        this.deliveryService = deliveryService;
    }

    public Dealer registerDealer(String companyName) {
        int dealerId = dataStore.nextDealerId();
        Dealer dealer = new Dealer(dealerId, companyName);
        dataStore.getDealers().put(dealerId, dealer);
        dataStore.getDealerOrderQueues().put(dealerId, new ArrayDeque<>());
        System.out.println("[DEALER] Registered: " + companyName + " (ID: " + dealerId + ")");
        return dealer;
    }

    public Product addProduct(int dealerId, String name, String category, double price, int stock) {
        Dealer dealer = dataStore.getDealers().get(dealerId);
        if (dealer == null) { System.out.println("  Dealer not found."); return null; }

        int productId = dataStore.nextProductId();
        Product product = new Product(productId, name, category, price, stock, dealerId);
        dataStore.getProducts().put(productId, product);
        dealer.getProductIds().add(productId);

        // Update TreeMap price index — enables "products under ₹X" range query in O(log n)
        dataStore.getPriceIndex()
                .computeIfAbsent(price, k -> new ArrayList<>())
                .add(productId);

        // Update category HashMap index — O(1) lookup of all products in a category
        dataStore.getCategoryIndex()
                .computeIfAbsent(category, k -> new ArrayList<>())
                .add(productId);

        System.out.println("[DEALER] Added product: \"" + name + "\" | ₹" + price + " | Stock: " + stock + " | Category: " + category);
        return product;
    }

    public void viewPendingOrders(int dealerId) {
        Deque<Integer> queue = dataStore.getDealerOrderQueues().get(dealerId);
        System.out.println("\n--- Pending Orders (Dealer #" + dealerId + ") ---");
        if (queue == null || queue.isEmpty()) {
            System.out.println("  No pending orders.");
        } else {
            queue.forEach(oid -> {
                Order o = dataStore.getOrders().get(oid);
                System.out.println("  Order #" + oid + " | User #" + o.getUserId() + " | Status: " + o.getStatus() + " | Items: " + o.getCartSnapshot().size());
            });
        }
        System.out.println("-----------------------------------------------");
    }

    // ArrayDeque.poll() is O(1) — dealer always processes the oldest order first (FIFO)
    public void acceptNextOrder(int dealerId) {
        Deque<Integer> queue = dataStore.getDealerOrderQueues().get(dealerId);
        if (queue == null || queue.isEmpty()) { System.out.println("  No pending orders for Dealer #" + dealerId); return; }

        int orderId = queue.poll();
        orderService.updateStatus(orderId, OrderStatus.ACCEPTED);
        orderService.updateStatus(orderId, OrderStatus.PACKED);
        System.out.println("[DEALER] Accepted and packed Order #" + orderId);
    }

    public void shipOrder(int dealerId, int orderId) {
        Order order = dataStore.getOrders().get(orderId);
        if (order == null || order.getDealerId() != dealerId) {
            System.out.println("  Order #" + orderId + " not found for Dealer #" + dealerId);
            return;
        }
        orderService.updateStatus(orderId, OrderStatus.SHIPPED);
        deliveryService.createDelivery(orderId);
        System.out.println("[DEALER] Shipped Order #" + orderId);
    }
}
