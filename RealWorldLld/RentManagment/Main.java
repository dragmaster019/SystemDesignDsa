import java.util.*;

/*
 * STEP-BY-STEP FLOW:
 *
 * 1. Owner registers -> lists properties (addProperty)
 * 2. Owner can view / update price / delete their properties
 * 3. Renter registers with budget + location + roomType
 * 4. Renter calls filter -> MatchingService loops propertyHashMap,
 *    uses PriorityQueue to return cheapest matches first
 * 5. Renter adds to cart, views cart, removes from cart
 * 6. Renter books -> property is marked booked, disappears from future searches
 */

public class Main {

    public static void main(String[] args) {

        // ── OWNER SIDE ────────────────────────────────────────────────
        System.out.println("===== OWNER REGISTERS =====");
        Owner owner1 = new Owner(1, new ArrayList<>());
        Data.ownerHashMap.put(1, owner1);

        OwnerService.addProperty(1, RoomType.TWO_BHK,   "Bangalore", 15000);
        OwnerService.addProperty(1, RoomType.ONE_BHK,   "Bangalore",  8000);
        OwnerService.addProperty(1, RoomType.TWO_BHK,   "Mumbai",    25000);
        OwnerService.addProperty(1, RoomType.TWO_BHK,   "Bangalore", 12000);

        System.out.println();
        OwnerService.viewProperties(1);

        // ── RENTER SIDE ───────────────────────────────────────────────
        System.out.println("\n===== RENTER REGISTERS =====");
        // budget=20000, wants TWO_BHK in Bangalore
        Renter renter1 = new Renter(101, 20000, RoomType.TWO_BHK, "Bangalore");
        Data.renterHashMap.put(101, renter1);
        System.out.println("Renter 101 | Budget: 20000 | Bangalore | TWO_BHK");

        // ── FILTER / SEARCH ───────────────────────────────────────────
        System.out.println("\n===== RENTER FILTERS PROPERTIES =====");
        // System matches: propertyId=1 (15000) and propertyId=4 (12000) both qualify
        // PriorityQueue returns 12000 first, then 15000
        RenterService.filterAndShow(101);

        // ── CART ──────────────────────────────────────────────────────
        System.out.println("\n===== RENTER ADDS TO CART =====");
        RenterService.addToCart(101, 4);   // Rs.12000 property
        RenterService.addToCart(101, 1);   // Rs.15000 property
        RenterService.viewCart(101);

        System.out.println("\n===== RENTER REMOVES ONE FROM CART =====");
        RenterService.removeFromCart(101, 1);
        RenterService.viewCart(101);

        // ── BOOKING ───────────────────────────────────────────────────
        System.out.println("\n===== RENTER BOOKS PROPERTY =====");
        RenterService.bookProperty(101, 4);

        // ── SECOND RENTER TRIES SAME PROPERTY ────────────────────────
        System.out.println("\n===== ANOTHER RENTER TRIES SAME PROPERTY =====");
        Renter renter2 = new Renter(102, 20000, RoomType.TWO_BHK, "Bangalore");
        Data.renterHashMap.put(102, renter2);
        RenterService.bookProperty(102, 4);  // should fail - already booked

        System.out.println("\n===== RENTER 102 FILTERS (property 4 gone now) =====");
        RenterService.filterAndShow(102);    // only property 1 (15000) shows up now

        // ── OWNER UPDATES / DELETES ───────────────────────────────────
        System.out.println("\n===== OWNER UPDATES PRICE =====");
        OwnerService.updatePrice(1, 1, 13000);
        OwnerService.viewProperties(1);

        System.out.println("\n===== OWNER DELETES PROPERTY =====");
        OwnerService.deleteProperty(1, 2);   // delete ONE_BHK Bangalore 8000
        OwnerService.viewProperties(1);
    }
}
