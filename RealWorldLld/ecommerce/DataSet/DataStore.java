package DataSet;

import Model.*;
import java.util.*;

/*
 * Singleton — one shared data store for all services.
 * Mirrors a real DB connection: every service talks to the same source of truth.
 *
 * Data structure choices (see ecommerce.md for full rationale):
 *   HashMap         → O(1) get/put for entity lookups by ID
 *   TreeMap         → O(log n) price-sorted index for range queries ("under ₹500")
 *   ArrayDeque      → O(1) FIFO queue for dealer's order processing
 */
public class DataStore {

    private static DataStore instance;

    // --- Primary lookup maps (O(1) get/put) ---
    private Map<Integer, User> users = new HashMap<>();
    private Map<Integer, Dealer> dealers = new HashMap<>();
    private Map<Integer, Product> products = new HashMap<>();
    private Map<Integer, Order> orders = new HashMap<>();
    private Map<Integer, Cart> carts = new HashMap<>();
    private Map<Integer, Delivery> deliveries = new HashMap<>();

    // --- Query indexes ---
    // TreeMap: keys sorted by price → subMap() gives range queries in O(log n + k)
    private TreeMap<Double, List<Integer>> priceIndex = new TreeMap<>();
    // HashMap: O(1) lookup of all product IDs in a category
    private Map<String, List<Integer>> categoryIndex = new HashMap<>();

    // --- Per-dealer FIFO order queue (ArrayDeque: O(1) offer/poll, no node overhead) ---
    private Map<Integer, Deque<Integer>> dealerOrderQueues = new HashMap<>();

    // --- Per-user order history (most recent first — offerFirst is O(1)) ---
    private Map<Integer, Deque<Integer>> userOrderHistory = new HashMap<>();

    // --- Auto-increment ID counters ---
    private int userCounter = 1;
    private int dealerCounter = 1;
    private int productCounter = 1;
    private int orderCounter = 1;
    private int cartCounter = 1;
    private int deliveryCounter = 1;

    private DataStore() {}

    public static DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    // --- Getters for all collections ---
    public Map<Integer, User> getUsers() { return users; }
    public Map<Integer, Dealer> getDealers() { return dealers; }
    public Map<Integer, Product> getProducts() { return products; }
    public Map<Integer, Order> getOrders() { return orders; }
    public Map<Integer, Cart> getCarts() { return carts; }
    public Map<Integer, Delivery> getDeliveries() { return deliveries; }
    public TreeMap<Double, List<Integer>> getPriceIndex() { return priceIndex; }
    public Map<String, List<Integer>> getCategoryIndex() { return categoryIndex; }
    public Map<Integer, Deque<Integer>> getDealerOrderQueues() { return dealerOrderQueues; }
    public Map<Integer, Deque<Integer>> getUserOrderHistory() { return userOrderHistory; }

    // --- ID generators ---
    public int nextUserId() { return userCounter++; }
    public int nextDealerId() { return dealerCounter++; }
    public int nextProductId() { return productCounter++; }
    public int nextOrderId() { return orderCounter++; }
    public int nextCartId() { return cartCounter++; }
    public int nextDeliveryId() { return deliveryCounter++; }
}
