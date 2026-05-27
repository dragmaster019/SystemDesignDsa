import Model.*;
import Services.*;

public class Main {

    public static void main(String[] args) {

        UserService userService               = new UserService();
        ParcelOfficeService officeService     = new ParcelOfficeService();
        DeliveryPartnerService partnerService = new DeliveryPartnerService();

        System.out.println("========== Parcel Delivery Demo ==========\n");

        // Step 1: Sarthak sends a laptop to Shreya via Haldia office
        Item item = userService.sendParcel(1, "Shreya", "Kolkata", "Laptop", 3, 1);

        // Step 2: Sender tracks the parcel
        userService.trackParcel(item.itemId);

        System.out.println();

        // Step 3: Parcel office sees pending parcels
        officeService.viewPendingParcels(1);

        // Step 4: Office assigns a delivery partner
        officeService.assignDeliveryPartner(item.itemId);

        System.out.println();

        // Step 5: Delivery partner views assigned parcels
        partnerService.viewAssignedParcels(1);

        // Step 6: Out for delivery
        partnerService.updateParcelStatus(item.itemId, ParcelStatus.OUT_FOR_DELIVERY);

        // Step 7: Delivered
        partnerService.updateParcelStatus(item.itemId, ParcelStatus.DELIVERED);

        System.out.println();

        // Step 8: Sender checks final status
        userService.trackParcel(item.itemId);

        System.out.println("\n========== Delivery Complete ==========");
    }
}
