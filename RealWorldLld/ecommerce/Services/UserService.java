package Services;

import DataSet.DataStore;
import Model.*;
import java.util.*;

/*
 * Facade pattern — UserService is the single entry point for all user-facing actions.
 * A user placing an order triggers: cart validation → stock deduction → order creation
 * → observer notifications → cart clear. The caller just calls placeOrder(userId).
 */
public class UserService {
    private DataStore dataStore = DataStore.getInstance();
    private CartService cartService = new CartService();
    private OrderService orderService;

    public UserService(OrderService orderService) {
        this.orderService = orderService;
    }

    public User registerUser(String name, String phoneNumber, String shippingAddress) {
        int userId = dataStore.nextUserId();
        User user = new User(userId, name, phoneNumber, shippingAddress);
        dataStore.getUsers().put(userId, user);
        System.out.println("[USER] Registered: " + name + " (ID: " + userId + ")");
        return user;
    }

    // TreeMap range query: headMap(maxPrice, inclusive) → all keys ≤ maxPrice in O(log n + k)
    public void browseByMaxPrice(double maxPrice) {
        System.out.println("\n--- Products under ₹" + maxPrice + " ---");
        boolean found = false;
        for (Map.Entry<Double, List<Integer>> entry : dataStore.getPriceIndex().headMap(maxPrice, true).entrySet()) {
            for (int pid : entry.getValue()) {
                Product p = dataStore.getProducts().get(pid);
                if (p != null && p.isAvailable()) {
                    System.out.println("  #" + p.getProductId() + " " + p.getName() + " | ₹" + p.getPrice() + " | Stock: " + p.getStockCount());
                    found = true;
                }
            }
        }
        if (!found) System.out.println("  No products found.");
        System.out.println("-------------------------------");
    }

    // Category index: O(1) HashMap lookup
    public void browseByCategory(String category) {
        List<Integer> productIds = dataStore.getCategoryIndex().get(category);
        System.out.println("\n--- Products in [" + category + "] ---");
        if (productIds == null || productIds.isEmpty()) {
            System.out.println("  No products found.");
        } else {
            productIds.forEach(pid -> {
                Product p = dataStore.getProducts().get(pid);
                if (p != null && p.isAvailable()) {
                    System.out.println("  #" + p.getProductId() + " " + p.getName() + " | ₹" + p.getPrice() + " | Stock: " + p.getStockCount());
                }
            });
        }
        System.out.println("-------------------------------");
    }

    public void addToCart(int userId, int productId, int quantity) {
        cartService.addToCart(userId, productId, quantity);
    }

    public void removeFromCart(int userId, int productId) {
        cartService.removeFromCart(userId, productId);
    }

    public void viewCart(int userId) {
        cartService.viewCart(userId);
    }

    public void applyDiscount(int userId, double discountPercent) {
        cartService.applyDiscount(userId, discountPercent);
    }

    // --- Facade: orchestrates CartService + OrderService in the correct order ---
    public Order placeOrder(int userId) {
        User user = dataStore.getUsers().get(userId);
        if (user == null) { System.out.println("  User not found."); return null; }
        if (!cartService.validateCart(userId)) return null;

        Cart cart = cartService.getOrCreateCart(userId);

        // Determine dealer from first cart item (single-dealer cart for simplicity)
        int firstProductId = cart.getItems().keySet().iterator().next();
        int dealerId = dataStore.getProducts().get(firstProductId).getDealerId();

        // Deduct stock — do this before creating the order to prevent overselling
        cart.getItems().forEach((pid, item) -> {
            Product p = dataStore.getProducts().get(pid);
            p.setStockCount(p.getStockCount() - item.getQuantity());
        });

        Order order = orderService.createOrder(userId, dealerId, cart.getItems());

        // Clear cart after order is confirmed
        cart.getItems().clear();

        System.out.println("[USER] Order placed! Order #" + order.getOrderId() + " | Ship to: " + user.getShippingAddress());
        return order;
    }

    public void viewOrderHistory(int userId) {
        orderService.viewOrderHistory(userId);
    }

    public void viewOrderStatus(int orderId) {
        orderService.viewOrderStatus(orderId);
    }
}
