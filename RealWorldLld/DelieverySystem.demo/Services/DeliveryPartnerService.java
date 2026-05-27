package Services;

import Model.*;
import DataSet.dataMap;
import java.util.*;

public class DeliveryPartnerService {

    public List<Item> viewAssignedParcels(Integer partnerId) {
        List<Item> assigned = new ArrayList<>();
        for (Item item : dataMap.itemMap.values()) {
            if (partnerId.equals(item.deliveryPartnerId)) {
                assigned.add(item);
            }
        }
        System.out.println("[DeliveryPartnerService] Parcels assigned to partner #" + partnerId + ": " + assigned.size());
        return assigned;
    }

    public void updateParcelStatus(Integer itemId, ParcelStatus newStatus) {
        Item item = dataMap.itemMap.get(itemId);
        if (item == null) {
            System.out.println("Parcel not found.");
            return;
        }
        item.status = newStatus;
        System.out.println("[DeliveryPartnerService] Parcel #" + itemId + " → " + newStatus);

        if (newStatus == ParcelStatus.DELIVERED && item.deliveryPartnerId != null) {
            DelieveryPartner partner = dataMap.partnerMap.get(item.deliveryPartnerId);
            if (partner != null) partner.isAvailable = true;
        }
    }
}
