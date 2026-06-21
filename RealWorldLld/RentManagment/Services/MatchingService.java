import java.util.*;

public class MatchingService {

    /*
     * This is the core matching algorithm you described in Main.java:
     *  - Take renter's budget, location, roomType
     *  - Loop through owner propertyHashMap
     *  - Filter by location + roomType + price <= budget + not already booked
     *  - Use PriorityQueue to return results sorted lowest price first
     */
    public static List<Property> findProperties(int budget, String location, RoomType roomType) {

        // min-heap: property with lowest price comes out first
        PriorityQueue<Property> pq = new PriorityQueue<>((a, b) -> a.getPrice() - b.getPrice());

        for (Property p : Data.propertyHashMap.values()) {
            boolean locationMatch = p.getLocation().equalsIgnoreCase(location);
            boolean roomMatch    = p.getRoomType() == roomType;
            boolean withinBudget = p.getPrice() <= budget;
            boolean notBooked    = !Data.bookedSet.contains(p.getPropertyId());

            if (locationMatch && roomMatch && withinBudget && notBooked) {
                pq.add(p);
            }
        }

        // drain PriorityQueue into a list (already sorted cheapest first)
        List<Property> result = new ArrayList<>();
        while (!pq.isEmpty()) {
            result.add(pq.poll());
        }
        return result;
    }
}
