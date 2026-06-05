package Model;

import java.util.ArrayList;
import java.util.List;

public class Dealer {
    private int dealerId;
    private String companyName;
    private List<Integer> productIds;

    public Dealer(int dealerId, String companyName) {
        this.dealerId = dealerId;
        this.companyName = companyName;
        this.productIds = new ArrayList<>();
    }

    public int getDealerId() { return dealerId; }
    public String getCompanyName() { return companyName; }
    public List<Integer> getProductIds() { return productIds; }
}
