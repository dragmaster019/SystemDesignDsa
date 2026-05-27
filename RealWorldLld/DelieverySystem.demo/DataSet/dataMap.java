package DataSet;

import Model.*;
import java.util.*;

public class dataMap {
    public static Map<Integer, User> userMap           = new HashMap<>();
    public static Map<Integer, ParcelOffice> officeMap = new HashMap<>();
    public static Map<Integer, DelieveryPartner> partnerMap = new HashMap<>();
    public static Map<Integer, Item> itemMap           = new HashMap<>();
    public static int itemCounter = 1;

    static {
        userMap.put(1, new User("Sarthak", 7872100365L, "Haldia",   1));
        userMap.put(2, new User("Shreya",  9233122091L, "Kolkata",  2));
        userMap.put(3, new User("Rahul",   9876543210L, "Durgapur", 3));

        officeMap.put(1, new ParcelOffice("Haldia",   1));
        officeMap.put(2, new ParcelOffice("Kolkata",  2));
        officeMap.put(3, new ParcelOffice("Durgapur", 3));

        partnerMap.put(1, new DelieveryPartner("Raj",  "WB12AB1234", 1));
        partnerMap.put(2, new DelieveryPartner("Amit", "WB14XY5678", 2));
    }
}
