import java.util.*;

public class Data {

    public static HashMap<Integer, Owner> ownerHashMap = new HashMap<>();
    public static HashMap<Integer, Renter> renterHashMap = new HashMap<>();
    public static HashMap<Integer, Property> propertyHashMap = new HashMap<>();

    // renterId -> list of propertyIds in cart
    public static HashMap<Integer, List<Integer>> cartMap = new HashMap<>();

    // propertyIds that are already booked
    public static Set<Integer> bookedSet = new HashSet<>();
}
