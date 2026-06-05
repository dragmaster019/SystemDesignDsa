package Model;

import strategy.PricingStrategy;
import java.util.LinkedHashMap;

// LinkedHashMap preserves insertion order — cart shows items in the sequence they were added
public class Cart {
    private int cartId;
    private int userId;
    private LinkedHashMap<Integer, CartItem> items;
    private PricingStrategy pricingStrategy;

    public Cart(int cartId, int userId, PricingStrategy pricingStrategy) {
        this.cartId = cartId;
        this.userId = userId;
        this.items = new LinkedHashMap<>();
        this.pricingStrategy = pricingStrategy;
    }

    public double calculateTotal() {
        return pricingStrategy.calculate(items);
    }

    public int getCartId() { return cartId; }
    public int getUserId() { return userId; }
    public LinkedHashMap<Integer, CartItem> getItems() { return items; }
    public PricingStrategy getPricingStrategy() { return pricingStrategy; }

    public void setPricingStrategy(PricingStrategy pricingStrategy) { this.pricingStrategy = pricingStrategy; }
}
