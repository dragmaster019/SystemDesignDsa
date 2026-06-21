package RealWorldLld.SwiggyDemo;

import RealWorldLld.SwiggyDemo.DataSet.Maps;
import RealWorldLld.SwiggyDemo.Model.*;
import RealWorldLld.SwiggyDemo.Services.*;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {

        UserService userService           = new UserService();
        ResturantServices restaurantService = new ResturantServices();
        DelieveryPartnerServices deliveryService = new DelieveryPartnerServices();

        System.out.println("========== Swiggy Demo ==========\n");

        // Step 1: User picks items from the menu and places an order
        Resturant restaurant = Maps.restaurants.get(1);
        MenuItem item1 = restaurant.menu.get(0);   // Paneer Butter Masala
        MenuItem item2 = restaurant.menu.get(1);   // Butter Naan

        Order order = userService.placeOrder(1, 1, Arrays.asList(item1, item2));

        // Step 2: User checks status right after placing
        userService.viewOrderStatus(order.orderId);

        System.out.println();

        // Step 3: Restaurant sees pending orders
        restaurantService.viewPendingOrders(1);

        // Step 4: Restaurant assigns a delivery partner
        restaurantService.assignDeliveryPartner(order.orderId);

        System.out.println();

        // Step 5: Delivery partner checks their assigned orders
        deliveryService.viewAssignedOrders(1);

        // Step 6: Delivery partner marks the order as delivered
        deliveryService.updateOrderStatus(order.orderId, OrderStatus.DELIVERED);

        System.out.println();

        // Step 7: User checks final status
        userService.viewOrderStatus(order.orderId);

        System.out.println("\n========== Order Complete ==========");
    }
}
