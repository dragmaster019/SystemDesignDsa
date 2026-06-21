package Model;

public class Product {
    private int productId;
    private String name;
    private String category;
    private double price;
    private int stockCount;
    private int dealerId;
    private boolean isAvailable;

    public Product(int productId, String name, String category, double price, int stockCount, int dealerId) {
        this.productId = productId;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stockCount = stockCount;
        this.dealerId = dealerId;
        this.isAvailable = stockCount > 0;
    }

    public int getProductId() { return productId; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public int getStockCount() { return stockCount; }
    public int getDealerId() { return dealerId; }
    public boolean isAvailable() { return isAvailable; }

    public void setStockCount(int stockCount) {
        this.stockCount = stockCount;
        this.isAvailable = stockCount > 0;
    }

    public void setAvailable(boolean available) { this.isAvailable = available; }
    public void setPrice(double price) { this.price = price; }
}
