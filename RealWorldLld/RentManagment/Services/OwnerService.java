import java.util.*;

public class OwnerService {

    private static int propertyIdCounter = 1;

    // Owner lists a new property
    public static void addProperty(int ownerId, RoomType roomType, String location, int price) {
        int propertyId = propertyIdCounter++;
        Property p = new Property(propertyId, ownerId, roomType, location, price);

        Data.propertyHashMap.put(propertyId, p);

        Owner owner = Data.ownerHashMap.get(ownerId);
        if (owner != null) {
            owner.getProperties().add(p);
        }
        System.out.println("Property added: ID=" + propertyId + " | " + location + " | " + roomType + " | Rs." + price);
    }

    // Owner removes a property
    public static void deleteProperty(int ownerId, int propertyId) {
        Property p = Data.propertyHashMap.get(propertyId);
        if (p == null || p.getOwnerId() != ownerId) {
            System.out.println("Property not found or you don't own it.");
            return;
        }
        Data.propertyHashMap.remove(propertyId);
        Owner owner = Data.ownerHashMap.get(ownerId);
        if (owner != null) {
            owner.getProperties().removeIf(prop -> prop.getPropertyId() == propertyId);
        }
        System.out.println("Property " + propertyId + " deleted.");
    }

    // Owner updates the rent price of a property
    public static void updatePrice(int ownerId, int propertyId, int newPrice) {
        Property p = Data.propertyHashMap.get(propertyId);
        if (p == null || p.getOwnerId() != ownerId) {
            System.out.println("Property not found or you don't own it.");
            return;
        }
        p.setPrice(newPrice);
        System.out.println("Property " + propertyId + " price updated to Rs." + newPrice);
    }

    // Owner views all their listed properties
    public static void viewProperties(int ownerId) {
        Owner owner = Data.ownerHashMap.get(ownerId);
        if (owner == null || owner.getProperties().isEmpty()) {
            System.out.println("No properties listed.");
            return;
        }
        System.out.println("Properties for owner " + ownerId + ":");
        for (Property p : owner.getProperties()) {
            System.out.println("  [ID=" + p.getPropertyId() + "] " + p.getLocation()
                    + " | " + p.getRoomType() + " | Rs." + p.getPrice());
        }
    }
}
