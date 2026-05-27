package RealWorldLld.SwiggyDemo.DataSet;

import RealWorldLld.SwiggyDemo.Model.*;
import java.util.*;

public class Maps {
    public static Map<Integer, User> users = new HashMap<>();
    public static Map<Integer, Resturant> restaurants = new HashMap<>();
    public static Map<Integer, Order> orders = new HashMap<>();
    public static Map<Integer, DelieveryPartner> partners = new HashMap<>();
    public static int orderCounter = 1;

    static {
        // Seed users
        users.put(1, new User("Sarthak", 9876543210L, 1, "Pune"));
        users.put(2, new User("Rahul",   9123456789L, 2, "Mumbai"));

        // Seed menu
        List<MenuItem> menu = new ArrayList<>();
        menu.add(new MenuItem(101, "Paneer Butter Masala", 220.0));
        menu.add(new MenuItem(102, "Butter Naan",          40.0));
        menu.add(new MenuItem(103, "Veg Biryani",          180.0));

        // Seed restaurants
        restaurants.put(1, new Resturant("Spice Garden", "Koregaon Park, Pune", 1, menu));

        // Seed delivery partners
        partners.put(1, new DelieveryPartner("Raj",  "MH12AB1234", 1));
        partners.put(2, new DelieveryPartner("Amit", "MH14XY5678", 2));
    }
}
