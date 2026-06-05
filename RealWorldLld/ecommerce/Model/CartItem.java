package Model;

// Snapshot of price at add time — insulates the cart from later price changes
public class CartItem {
    private int productId;
    private int quantity;
    private double priceAtAddTime;

    public CartItem(int productId, int quantity, double priceAtAddTime) {
        this.productId = productId;
        this.quantity = quantity;
        this.priceAtAddTime = priceAtAddTime;
    }

    public int getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public double getPriceAtAddTime() { return priceAtAddTime; }

    public void setQuantity(int quantity) { this.quantity = quantity; }
}
