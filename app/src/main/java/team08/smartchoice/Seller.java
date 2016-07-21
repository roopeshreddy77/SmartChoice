package team08.smartchoice;

/**
 * Created by roope on 7/21/2016.
 */
public class Seller {
    public String sellerId;
    public String storeName;
    public Address address;

    public Seller() {
    }

    public Seller(String sellerId, String storeName, Address address) {
        this.sellerId = sellerId;
        this.storeName = storeName;
        this.address = address;
    }
}
