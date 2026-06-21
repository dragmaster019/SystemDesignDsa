package Services;

import DataSet.DataStore;
import Model.*;
import strategy.DiscountPricingStrategy;
import strategy.RegularPricingStrategy;

// Single Responsibility: CartService only manages cart state — nothing else
public class CartService {
    private DataStore dataStore = DataStore.getInstance();

    public Cart getOrCreateCart(int userId) {
        User user = dataStore.getUsers().get(userId);
        if (user == null) throw new IllegalArgumentException("User not found: " + userId);

        if (user.getCartId() == -1) {
            int cartId = dataStore.nextCartId();
            Cart cart = new Cart(cartId, userId, new RegularPricingStrategy());
            dataStore.getCarts().put(cartId, cart);
            user.setCartId(cartId);
        }
        return dataStore.getCarts().get(user.getCartId());
    }

    public void addToCart(int userId, int productId, int quantity) {
        Product product = dataStore.getProducts().get(productId);
        if (product == null || !product.isAvailable() || product.getStockCount() < quantity) {
            System.out.println("  [CART] Product unavailable or insufficient stock.");
            return;
        }

        Cart cart = getOrCreateCart(userId);
        // LinkedHashMap.get is O(1); if key exists, increase quantity instead of duplicate
        CartItem existing = cart.getItems().get(productId);
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
        } else {
            // Snapshot price at add time — user locked in this price even if dealer changes it later
            cart.getItems().put(productId, new CartItem(productId, quantity, product.getPrice()));
        }
        System.out.println("  [CART] Added \"" + product.getName() + "\" x" + quantity + " | Cart total: ₹" + cart.calculateTotal());
    }

    public void removeFromCart(int userId, int productId) {
        Cart cart = getOrCreateCart(userId);
        if (cart.getItems().remove(productId) != null) {
            System.out.println("  [CART] Removed product #" + productId);
        }
    }

    public boolean validateCart(int userId) {
        Cart cart = getOrCreateCart(userId);
        if (cart.getItems().isEmpty()) {
            System.out.println("  [CART] Cart is empty.");
            return false;
        }
        for (CartItem item : cart.getItems().values()) {
            Product p = dataStore.getProducts().get(item.getProductId());
            if (p == null || p.getStockCount() < item.getQuantity()) {
                System.out.println("  [CART] \"" + (p != null ? p.getName() : "Product#" + item.getProductId()) + "\" is out of stock.");
                return false;
            }
        }
        return true;
    }

    // Strategy pattern: swap pricing at runtime, CartService code never changes
    public void applyDiscount(int userId, double discountPercent) {
        Cart cart = getOrCreateCart(userId);
        cart.setPricingStrategy(new DiscountPricingStrategy(discountPercent));
        System.out.println("  [CART] " + discountPercent + "% discount applied. New total: ₹" + cart.calculateTotal());
    }

    public void removeDiscount(int userId) {
        Cart cart = getOrCreateCart(userId);
        cart.setPricingStrategy(new RegularPricingStrategy());
        System.out.println("  [CART] Discount removed. Total: ₹" + cart.calculateTotal());
    }

    public void viewCart(int userId) {
        Cart cart = getOrCreateCart(userId);
        System.out.println("\n--- Cart (User #" + userId + ") ---");
        if (cart.getItems().isEmpty()) {
            System.out.println("  Empty cart.");
        } else {
            // LinkedHashMap iterates in insertion order — items appear as user added them
            cart.getItems().forEach((pid, item) -> {
                Product p = dataStore.getProducts().get(pid);
                String name = p != null ? p.getName() : "Product#" + pid;
                System.out.println("  " + name + " x" + item.getQuantity() + " @ ₹" + item.getPriceAtAddTime());
            });
        }
        System.out.println("  Total: ₹" + cart.calculateTotal());
        System.out.println("--------------------------------");
    }
}
