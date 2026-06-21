package RealWorldLld.SwiggyDemo.Services;

import RealWorldLld.SwiggyDemo.DataSet.Maps;
import RealWorldLld.SwiggyDemo.Model.*;
import java.util.*;

public class DelieveryPartnerServices {

    public List<Order> viewAssignedOrders(Integer partnerId) {
        List<Order> assigned = new ArrayList<>();
        for (Order order : Maps.orders.values()) {
            if (partnerId.equals(order.deliveryPartnerId)) {
                assigned.add(order);
            }
        }
        System.out.println("[DeliveryService] Orders assigned to partner #" + partnerId + ": " + assigned.size());
        return assigned;
    }

    public void updateOrderStatus(Integer orderId, OrderStatus newStatus) {
        Order order = Maps.orders.get(orderId);
        if (order == null) {
            System.out.println("Order not found.");
            return;
        }
        order.status = newStatus;
        System.out.println("[DeliveryService] Order #" + orderId + " → " + newStatus);

        // Free up partner once delivered
        if (newStatus == OrderStatus.DELIVERED && order.deliveryPartnerId != null) {
            DelieveryPartner partner = Maps.partners.get(order.deliveryPartnerId);
            if (partner != null) partner.isAvailable = true;
        }
    }
}
