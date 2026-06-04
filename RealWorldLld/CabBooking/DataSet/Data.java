package DataSet;

import Model.*;
import java.util.*;

public class Data {

    // ── HashMaps ────────────────────────────────────────────────
    public static Map<Integer, User>   userMap   = new HashMap<>();
    public static Map<Integer, Driver> driverMap = new HashMap<>();
    public static Map<Integer, Ride>   rideMap   = new HashMap<>();

    // ── PriorityQueue — best rated available driver first ───────
    public static PriorityQueue<Driver> availableDrivers =
            new PriorityQueue<>((a, b) -> Double.compare(b.getRating(), a.getRating()));

    // ── Queue — ride requests waiting for a driver ──────────────
    public static Queue<Ride> pendingRides = new LinkedList<>();

    // ── Stack — ride history per user ───────────────────────────
    public static Map<Integer, Stack<Ride>> rideHistory = new HashMap<>();

    // ── ArrayList — all drivers in the system ───────────────────
    public static ArrayList<Driver> allDrivers = new ArrayList<>();

    // ── Distance map — pickup-drop → km ─────────────────────────
    public static Map<String, Integer> distanceMap = new HashMap<>();

    public static int rideCounter = 1;
    public static final double RATE_PER_KM = 10.0;

    static {
        // Seed users
        userMap.put(1, new User(1, "Sarthak", "7872100365"));
        userMap.put(2, new User(2, "Shreya",  "9233122091"));

        // Seed drivers
        Driver d1 = new Driver(1, "WB12AB1234", "Kolkata", 4.8);
        Driver d2 = new Driver(2, "WB14XY5678", "Haldia",  4.5);
        driverMap.put(1, d1);
        driverMap.put(2, d2);

        // All drivers list
        allDrivers.add(d1);
        allDrivers.add(d2);

        // Available drivers PQ
        availableDrivers.add(d1);
        availableDrivers.add(d2);

        // Ride history stacks
        rideHistory.put(1, new Stack<>());
        rideHistory.put(2, new Stack<>());

        // Distance map
        distanceMap.put("Kolkata-Haldia",  120);
        distanceMap.put("Haldia-Kolkata",  120);
        distanceMap.put("Kolkata-Durgapur",180);
        distanceMap.put("Durgapur-Kolkata",180);
        distanceMap.put("Haldia-Durgapur", 170);
        distanceMap.put("Durgapur-Haldia", 170);
    }
}
