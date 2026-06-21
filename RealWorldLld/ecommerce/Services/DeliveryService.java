package Services;

import DataSet.DataStore;
import Model.Delivery;
import Model.Order;
import Model.enums.DeliveryStatus;
import Model.enums.OrderStatus;

public class DeliveryService {
    private DataStore dataStore = DataStore.getInstance();
    private OrderService orderService;

    public DeliveryService(OrderService orderService) {
        this.orderService = orderService;
    }

    public Delivery createDelivery(int orderId) {
        int deliveryId = dataStore.nextDeliveryId();
        Delivery delivery = new Delivery(deliveryId, orderId);
        delivery.setStatus(DeliveryStatus.ASSIGNED);
        delivery.setDeliveryPartnerId("DP-001");
        delivery.setEstimatedDelivery("2-3 business days");

        dataStore.getDeliveries().put(deliveryId, delivery);

        Order order = dataStore.getOrders().get(orderId);
        if (order != null) order.setDeliveryId(deliveryId);

        System.out.println("  [DELIVERY] Created #" + deliveryId + " for Order #" + orderId + " | Partner: DP-001");
        return delivery;
    }

    public void updateDeliveryStatus(int deliveryId, DeliveryStatus newStatus) {
        Delivery delivery = dataStore.getDeliveries().get(deliveryId);
        if (delivery == null) { System.out.println("  [DELIVERY] Not found: #" + deliveryId); return; }
        delivery.setStatus(newStatus);
        System.out.println("  [DELIVERY] #" + deliveryId + " -> " + newStatus);

        // Mirror delivery status into order status
        if (newStatus == DeliveryStatus.OUT_FOR_DELIVERY) {
            orderService.updateStatus(delivery.getOrderId(), OrderStatus.OUT_FOR_DELIVERY);
        } else if (newStatus == DeliveryStatus.DELIVERED) {
            orderService.updateStatus(delivery.getOrderId(), OrderStatus.DELIVERED);
        }
    }

    public void viewDeliveryStatus(int deliveryId) {
        Delivery d = dataStore.getDeliveries().get(deliveryId);
        if (d == null) { System.out.println("  Delivery #" + deliveryId + " not found."); return; }
        System.out.println("  Delivery #" + deliveryId + " | Order #" + d.getOrderId() + " | Status: " + d.getStatus() + " | ETA: " + d.getEstimatedDelivery());
    }
}
