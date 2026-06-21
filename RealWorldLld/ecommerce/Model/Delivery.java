package Model;

import Model.enums.DeliveryStatus;

public class Delivery {
    private int deliveryId;
    private int orderId;
    private String deliveryPartnerId;
    private DeliveryStatus status;
    private String estimatedDelivery;

    public Delivery(int deliveryId, int orderId) {
        this.deliveryId = deliveryId;
        this.orderId = orderId;
        this.status = DeliveryStatus.PENDING;
    }

    public int getDeliveryId() { return deliveryId; }
    public int getOrderId() { return orderId; }
    public String getDeliveryPartnerId() { return deliveryPartnerId; }
    public DeliveryStatus getStatus() { return status; }
    public String getEstimatedDelivery() { return estimatedDelivery; }

    public void setDeliveryPartnerId(String deliveryPartnerId) { this.deliveryPartnerId = deliveryPartnerId; }
    public void setStatus(DeliveryStatus status) { this.status = status; }
    public void setEstimatedDelivery(String estimatedDelivery) { this.estimatedDelivery = estimatedDelivery; }
}
