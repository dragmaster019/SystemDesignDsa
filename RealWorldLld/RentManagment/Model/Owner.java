import java.util.*;


public class Owner{

    private int ownerId;
    private ArrayList<Property> properties;

    public Owner(int ownerId, ArrayList<Property> properties) {
        this.ownerId = ownerId;
        this.properties = properties;
    }

    public int getOwnerId() {
        return ownerId;
    }
    
    public ArrayList<Property> getProperties() {
        return properties;
    }
    






}
