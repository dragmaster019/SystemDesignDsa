package observer;

import Model.enums.OrderStatus;

public class DealerOrderObserver implements OrderObserver {

    @Override
    public void onOrderStatusChange(int orderId, OrderStatus newStatus, int userId, int dealerId) {
        System.out.println("  [DEALER #" + dealerId + "] Notification -> Order #" + orderId + " updated to: " + newStatus);
    }
}
