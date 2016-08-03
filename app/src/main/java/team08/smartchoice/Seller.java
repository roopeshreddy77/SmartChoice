package team08.smartchoice;

/**
 * Created by roope on 7/21/2016.
 */
public class Seller {
    public String sellerId;
    public String storeName;
    public Address address;
    public String locationID;

    public Seller() {
    }

    public Seller(String sellerId, String storeName, Address address, String locationID) {
        this.sellerId = sellerId;
        this.storeName = storeName;
        this.address = address;
        this.locationID = locationID;
    }
}
