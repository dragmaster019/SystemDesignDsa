package strategy;

import Model.CartItem;
import java.util.Map;

// Applies a flat percentage discount. Swappable at runtime — zero changes needed in CartService
public class DiscountPricingStrategy implements PricingStrategy {
    private double discountPercent;

    public DiscountPricingStrategy(double discountPercent) {
        this.discountPercent = discountPercent;
    }

    @Override
    public double calculate(Map<Integer, CartItem> items) {
        double total = 0;
        for (CartItem item : items.values()) {
            total += item.getPriceAtAddTime() * item.getQuantity();
        }
        return Math.round(total * (1 - discountPercent / 100.0) * 100.0) / 100.0;
    }

    public double getDiscountPercent() { return discountPercent; }
}
