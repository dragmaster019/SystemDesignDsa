import java.util.*;

public class RenterService {

    // Renter applies filters and system shows matching properties
    public static void filterAndShow(int renterId) {
        Renter renter = Data.renterHashMap.get(renterId);
        if (renter == null) {
            System.out.println("Renter not found.");
            return;
        }

        List<Property> results = MatchingService.findProperties(
                renter.getBudget(), renter.getLocation(), renter.getRoomType());

        if (results.isEmpty()) {
            System.out.println("No properties found matching your criteria.");
            return;
        }

        System.out.println("Available properties (cheapest first):");
        for (Property p : results) {
            System.out.println("  [ID=" + p.getPropertyId() + "] " + p.getLocation()
                    + " | " + p.getRoomType() + " | Rs." + p.getPrice());
        }
    }

    // Renter adds a property to cart
    public static void addToCart(int renterId, int propertyId) {
        if (!Data.propertyHashMap.containsKey(propertyId)) {
            System.out.println("Property not found.");
            return;
        }
        if (Data.bookedSet.contains(propertyId)) {
            System.out.println("Property already booked by someone else.");
            return;
        }
        // computeIfAbsent: if renterId not in cartMap, create empty list first
        Data.cartMap.computeIfAbsent(renterId, k -> new ArrayList<>()).add(propertyId);
        System.out.println("Property " + propertyId + " added to cart.");
    }

    // Renter removes a property from cart
    public static void removeFromCart(int renterId, int propertyId) {
        List<Integer> cart = Data.cartMap.get(renterId);
        if (cart == null || !cart.remove(Integer.valueOf(propertyId))) {
            System.out.println("Property not in your cart.");
            return;
        }
        System.out.println("Property " + propertyId + " removed from cart.");
    }

    // Renter views their cart
    public static void viewCart(int renterId) {
        List<Integer> cart = Data.cartMap.get(renterId);
        if (cart == null || cart.isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }
        System.out.println("Cart for renter " + renterId + ":");
        for (int pid : cart) {
            Property p = Data.propertyHashMap.get(pid);
            if (p != null) {
                System.out.println("  [ID=" + p.getPropertyId() + "] " + p.getLocation()
                        + " | " + p.getRoomType() + " | Rs." + p.getPrice());
            }
        }
    }

    // Renter books a property (marks it as taken)
    public static void bookProperty(int renterId, int propertyId) {
        if (!Data.propertyHashMap.containsKey(propertyId)) {
            System.out.println("Property not found.");
            return;
        }
        if (Data.bookedSet.contains(propertyId)) {
            System.out.println("Sorry, property " + propertyId + " is already booked.");
            return;
        }
        Data.bookedSet.add(propertyId);

        // also remove from cart if it was there
        List<Integer> cart = Data.cartMap.get(renterId);
        if (cart != null) cart.remove(Integer.valueOf(propertyId));

        System.out.println("Property " + propertyId + " booked successfully by renter " + renterId + "!");
    }
}
