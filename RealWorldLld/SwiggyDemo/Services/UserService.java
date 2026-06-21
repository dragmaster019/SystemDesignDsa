package RealWorldLld.SwiggyDemo.Services;

import RealWorldLld.SwiggyDemo.DataSet.Maps;
import RealWorldLld.SwiggyDemo.Model.*;
import java.util.List;

public class UserService {

    public Order placeOrder(Integer userId, Integer restaurantId, List<MenuItem> items) {
        if (!Maps.users.containsKey(userId)) {
            System.out.println("User not found.");
            return null;
        }
        if (!Maps.restaurants.containsKey(restaurantId)) {
            System.out.println("Restaurant not found.");
            return null;
        }
        Order order = new Order(Maps.orderCounter++, userId, restaurantId, items);
        Maps.orders.put(order.orderId, order);
        System.out.println("[UserService] Order placed! Order ID: " + order.orderId + " | Status: " + order.status);
        return order;
    }

    public void viewOrderStatus(Integer orderId) {
        Order order = Maps.orders.get(orderId);
        if (order == null) {
            System.out.println("Order not found.");
            return;
        }
        System.out.println("[UserService] Order #" + orderId + " → Status: " + order.status);
    }
}
