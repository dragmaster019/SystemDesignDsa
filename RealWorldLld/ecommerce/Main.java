import Services.*;
import Model.enums.DeliveryStatus;
import Model.*;
import observer.*;

public class Main {
    public static void main(String[] args) {

        // =========================================================
        // WIRING — build the service graph (dependency injection)
        // OrderService is shared: UserService, DealerService, DeliveryService all need it
        // =========================================================
        OrderService orderService = new OrderService();
        DeliveryService deliveryService = new DeliveryService(orderService);
        UserService userService = new UserService(orderService);
        DealerService dealerService = new DealerService(orderService, deliveryService);

        // Register observers — add more here without changing OrderService (Open/Closed)
        orderService.registerObserver(new UserOrderObserver());
        orderService.registerObserver(new DealerOrderObserver());

        System.out.println("============================================================");
        System.out.println("       E-COMMERCE SYSTEM — FULL FLOW DEMO");
        System.out.println("============================================================\n");

        // =========================================================
        // STEP 1: Register actors
        // =========================================================
        System.out.println(">>> STEP 1: REGISTRATION");
        User alice = userService.registerUser("Alice", "9876543210", "12, MG Road, Bengaluru - 560001");
        User bob   = userService.registerUser("Bob",   "9123456780", "45, Park Street, Kolkata - 700016");
        Dealer techShop = dealerService.registerDealer("TechShop India");

        // =========================================================
        // STEP 2: Dealer stocks products
        // =========================================================
        System.out.println("\n>>> STEP 2: DEALER ADDS PRODUCTS");
        dealerService.addProduct(techShop.getDealerId(), "iPhone 15",        "Electronics", 79999.0, 10);
        dealerService.addProduct(techShop.getDealerId(), "Wireless Earbuds", "Electronics",  2499.0, 50);
        dealerService.addProduct(techShop.getDealerId(), "Phone Case",       "Accessories",   499.0, 200);
        dealerService.addProduct(techShop.getDealerId(), "USB-C Hub",        "Accessories",  1999.0, 30);

        // =========================================================
        // STEP 3: User browses
        // =========================================================
        System.out.println("\n>>> STEP 3: USER BROWSES");
        userService.browseByCategory("Electronics");
        userService.browseByMaxPrice(2500.0);

        // =========================================================
        // STEP 4: Alice adds to cart
        // =========================================================
        System.out.println("\n>>> STEP 4: ADD TO CART");
        userService.addToCart(alice.getUserId(), 2, 1);   // Wireless Earbuds x1
        userService.addToCart(alice.getUserId(), 3, 2);   // Phone Case x2
        userService.viewCart(alice.getUserId());

        // =========================================================
        // STEP 5: Apply a 10% coupon discount (Strategy swap at runtime)
        // =========================================================
        System.out.println("\n>>> STEP 5: APPLY DISCOUNT COUPON");
        userService.applyDiscount(alice.getUserId(), 10.0);
        userService.viewCart(alice.getUserId());

        // =========================================================
        // STEP 6: Place order — one call, Facade hides 5 sub-operations
        // =========================================================
        System.out.println("\n>>> STEP 6: PLACE ORDER (Facade)");
        Order order = userService.placeOrder(alice.getUserId());
        if (order == null) { System.out.println("Order failed."); return; }

        // =========================================================
        // STEP 7: Dealer side — view FIFO queue, accept, pack, ship
        // =========================================================
        System.out.println("\n>>> STEP 7: DEALER PROCESSES ORDER");
        dealerService.viewPendingOrders(techShop.getDealerId());
        dealerService.acceptNextOrder(techShop.getDealerId());
        dealerService.shipOrder(techShop.getDealerId(), order.getOrderId());

        // =========================================================
        // STEP 8: Delivery partner updates status
        // =========================================================
        System.out.println("\n>>> STEP 8: DELIVERY PARTNER UPDATES");
        int deliveryId = order.getDeliveryId();
        deliveryService.updateDeliveryStatus(deliveryId, DeliveryStatus.PICKED_UP);
        deliveryService.updateDeliveryStatus(deliveryId, DeliveryStatus.OUT_FOR_DELIVERY);
        deliveryService.updateDeliveryStatus(deliveryId, DeliveryStatus.DELIVERED);

        // =========================================================
        // STEP 9: Final status check
        // =========================================================
        System.out.println("\n>>> STEP 9: FINAL STATUS");
        userService.viewOrderStatus(order.getOrderId());
        userService.viewOrderHistory(alice.getUserId());
        deliveryService.viewDeliveryStatus(deliveryId);

        // =========================================================
        // BONUS: Bob places a separate order — independent lifecycle
        // =========================================================
        System.out.println("\n>>> BONUS: BOB PLACES AN ORDER");
        userService.addToCart(bob.getUserId(), 4, 1);  // USB-C Hub x1
        userService.viewCart(bob.getUserId());
        Order bobOrder = userService.placeOrder(bob.getUserId());
        userService.viewOrderHistory(bob.getUserId());

        System.out.println("\n============================================================");
        System.out.println("                    DEMO COMPLETE");
        System.out.println("============================================================");
    }
}
