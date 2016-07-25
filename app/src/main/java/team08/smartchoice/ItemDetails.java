package team08.smartchoice;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by roope on 7/21/2016.
 */
public class ItemDetails {
    public String itemName;
    public Double originalPrice;
    public Double discountPrice;
    public String expiryDate;
    public String imageUrl;

    public ItemDetails(){

    }

    public ItemDetails(String itemName, Double originalPrice, Double discountPrice, String expiryDate, String imageUrl){
        this.itemName = itemName;
        this.originalPrice = originalPrice;
        this.discountPrice = discountPrice;
        this.expiryDate = expiryDate;
        this.imageUrl = imageUrl;
    }

    public Map<String,Object> toMap(){
        HashMap<String,Object> result = new HashMap<>();
        result.put("itemName", itemName);
        result.put("originalPrice", String.valueOf(originalPrice));
        result.put("discountPrice", String.valueOf(discountPrice));
        result.put("expiryDate", expiryDate);
        result.put("imageUrl", imageUrl);
        return result;
    }
}
