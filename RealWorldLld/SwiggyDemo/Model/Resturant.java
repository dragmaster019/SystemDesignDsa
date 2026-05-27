package RealWorldLld.SwiggyDemo.Model;

import java.util.List;

public class Resturant {
    public String name;
    public String location;
    public Integer restaurantId;
    public List<MenuItem> menu;

    public Resturant(String name, String location, Integer restaurantId, List<MenuItem> menu) {
        this.name = name;
        this.location = location;
        this.restaurantId = restaurantId;
        this.menu = menu;
    }
}
