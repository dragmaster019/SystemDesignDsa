package Services;

import Model.*;
import DataSet.dataMap;

public class UserService {

    public Item sendParcel(Integer senderId, String receiverName, String receiverCity,
                           String itemName, Integer weight, Integer officeId) {
        if (!dataMap.userMap.containsKey(senderId)) {
            System.out.println("Sender not found.");
            return null;
        }
        if (!dataMap.officeMap.containsKey(officeId)) {
            System.out.println("Parcel office not found.");
            return null;
        }
        Item item = new Item(dataMap.itemCounter++, itemName, weight,
                             senderId, receiverName, receiverCity, officeId);
        dataMap.itemMap.put(item.itemId, item);
        System.out.println("[UserService] Parcel booked! Item ID: " + item.itemId + " | Status: " + item.status);
        return item;
    }

    public void trackParcel(Integer itemId) {
        Item item = dataMap.itemMap.get(itemId);
        if (item == null) {
            System.out.println("Parcel not found.");
            return;
        }
        System.out.println("[UserService] Parcel #" + itemId + " → Status: " + item.status);
    }
}
