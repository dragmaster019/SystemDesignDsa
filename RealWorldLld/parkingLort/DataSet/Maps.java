package DataSet;

import Model.*;
import java.util.*;

public class Maps {
    public static Map<Integer, ParkingSpot> spotMap       = new HashMap<>();
    public static Map<String, Ticket>       activeTickets = new HashMap<>();
    public static Queue<String[]>           waitingQueue  = new LinkedList<>();
    public static int ticketCounter = 1;

    static {
        spotMap.put(1, new ParkingSpot(1));
        spotMap.put(2, new ParkingSpot(2));
    }
}
