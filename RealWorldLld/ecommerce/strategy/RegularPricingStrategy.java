package strategy;

import Model.CartItem;
import java.util.Map;

public class RegularPricingStrategy implements PricingStrategy {

    @Override
    public double calculate(Map<Integer, CartItem> items) {
        double total = 0;
        for (CartItem item : items.values()) {
            total += item.getPriceAtAddTime() * item.getQuantity();
        }
        return total;
    }
}
