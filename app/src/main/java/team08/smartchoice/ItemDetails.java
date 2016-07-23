package team08.smartchoice;

/**
 * Created by roope on 7/21/2016.
 */
public class ItemDetails {
    public String itemName;
    public int originalPrice;
    public int discountPrice;
    public String expiryDate;
    public String imageUrl;

    public ItemDetails(){

    }

    public ItemDetails(String itemName, int originalPrice, int discountPrice, String expiryDate, String imageUrl){
        this.itemName = itemName;
        this.originalPrice = originalPrice;
        this.discountPrice = discountPrice;
        this.expiryDate = expiryDate;
        this.imageUrl = imageUrl;
    }
}
