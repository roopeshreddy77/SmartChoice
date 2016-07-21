package team08.smartchoice;

/**
 * Created by roope on 7/21/2016.
 */
public class Address {
    public String addrLine1;
    public String addrLine2;
    public String city;
    public String state;
    public int zip;

    public Address() {
    }

    public Address(String addrLine1, String addrLine2, String city, String state, int zip) {
        this.addrLine1 = addrLine1;
        this.addrLine2 = addrLine2;
        this.city = city;
        this.state = state;
        this.zip = zip;
    }
}
