package observer;

import Model.enums.OrderStatus;

public class UserOrderObserver implements OrderObserver {

    @Override
    public void onOrderStatusChange(int orderId, OrderStatus newStatus, int userId, int dealerId) {
        System.out.println("  [USER #" + userId + "] Notification -> Order #" + orderId + " is now: " + newStatus);
    }
}
