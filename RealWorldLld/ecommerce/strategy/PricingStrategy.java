package strategy;

import Model.CartItem;
import java.util.Map;

// Strategy interface — CartService depends on this abstraction, not concrete classes (Dependency Inversion)
public interface PricingStrategy {
    double calculate(Map<Integer, CartItem> items);
}
