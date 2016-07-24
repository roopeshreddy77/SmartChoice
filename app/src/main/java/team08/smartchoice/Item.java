package team08.smartchoice;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by roope on 7/21/2016.
 */
public class Item {
    public String sellerId;
    public ItemDetails itemDetails;
//    public String itemName;
//    public int originalPrice;
//    public int discountPrice;
//    public String expiryDate;
//    public String imageUrl;

    public Item(){

    }
    public  Item(String sellerId, ItemDetails itemDetails){
        this.sellerId = sellerId;
        this.itemDetails = itemDetails;
//        this.itemName = itemName;
//        this.originalPrice = originalPrice;
//        this.discountPrice = discountPrice;
//        this.expiryDate = expiryDate;
//        this.imageUrl = imageUrl;

        this.itemDetails = itemDetails;
    }

    public Map<String,Object> toMap(){
        HashMap<String,Object> result = new HashMap<>();
        result.put("sellerId", sellerId);
        result.put("itemDetails", itemDetails);
//        result.put("itemName", itemName);
//        result.put("originalPrice", String.valueOf(originalPrice));
//        result.put("discountPrice", String.valueOf(discountPrice));
//        result.put("expiryDate", expiryDate);
//        result.put("imageUrl", imageUrl);
        return result;
    }
}
