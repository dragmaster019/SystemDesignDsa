package RealWorldLld.SwiggyDemo.Services;

import RealWorldLld.SwiggyDemo.DataSet.Maps;
import RealWorldLld.SwiggyDemo.Model.*;
import java.util.*;

public class ResturantServices {

    public List<Order> viewPendingOrders(Integer restaurantId) {
        List<Order> pending = new ArrayList<>();
        for (Order order : Maps.orders.values()) {
            if (order.restaurantId.equals(restaurantId) && order.status == OrderStatus.PLACED) {
                pending.add(order);
            }
        }
        System.out.println("[RestaurantService] Pending orders for restaurant #" + restaurantId + ": " + pending.size());
        return pending;
    }

    public void assignDeliveryPartner(Integer orderId) {
        Order order = Maps.orders.get(orderId);
        if (order == null) {
            System.out.println("Order not found.");
            return;
        }

        DelieveryPartner available = null;
        for (DelieveryPartner p : Maps.partners.values()) {
            if (p.isAvailable) {
                available = p;
                break;
            }
        }

        if (available == null) {
            System.out.println("[RestaurantService] No delivery partner available right now.");
            return;
        }

        available.isAvailable = false;
        order.deliveryPartnerId = available.partnerId;
        order.status = OrderStatus.OUT_FOR_DELIVERY;
        System.out.println("[RestaurantService] Assigned partner: " + available.name
                + " | Order #" + orderId + " → " + order.status);
    }
}
