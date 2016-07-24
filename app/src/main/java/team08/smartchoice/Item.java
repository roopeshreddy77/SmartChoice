package team08.smartchoice;

/**
 * Created by roope on 7/21/2016.
 */
public class Item {
    public String sellerId;
    public ItemDetails itemDetails;

    public Item(){

    }
    public  Item(String sellerId, ItemDetails itemDetails){
        this.sellerId = sellerId;
        this.itemDetails = itemDetails;
    }
}
