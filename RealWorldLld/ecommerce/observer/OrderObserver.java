package observer;

import Model.enums.OrderStatus;

// Interface Segregation: one focused method — observers only handle status change events
public interface OrderObserver {
    void onOrderStatusChange(int orderId, OrderStatus newStatus, int userId, int dealerId);
}
