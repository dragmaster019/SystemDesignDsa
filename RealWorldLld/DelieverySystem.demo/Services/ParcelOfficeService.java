package Services;

import Model.*;
import DataSet.dataMap;
import java.util.*;

public class ParcelOfficeService {

    public List<Item> viewPendingParcels(Integer officeId) {
        List<Item> pending = new ArrayList<>();
        for (Item item : dataMap.itemMap.values()) {
            if (item.officeId.equals(officeId) && item.status == ParcelStatus.BOOKED) {
                pending.add(item);
            }
        }
        System.out.println("[ParcelOfficeService] Pending parcels at office #" + officeId + ": " + pending.size());
        return pending;
    }

    public void assignDeliveryPartner(Integer itemId) {
        Item item = dataMap.itemMap.get(itemId);
        if (item == null) {
            System.out.println("Parcel not found.");
            return;
        }
        DelieveryPartner available = null;
        for (DelieveryPartner p : dataMap.partnerMap.values()) {
            if (p.isAvailable) {
                available = p;
                break;
            }
        }
        if (available == null) {
            System.out.println("[ParcelOfficeService] No delivery partner available.");
            return;
        }
        available.isAvailable = false;
        item.deliveryPartnerId = available.partnerId;
        item.status = ParcelStatus.PICKED_UP;
        System.out.println("[ParcelOfficeService] Assigned: " + available.name
                + " | Parcel #" + itemId + " → " + item.status);
    }
}
