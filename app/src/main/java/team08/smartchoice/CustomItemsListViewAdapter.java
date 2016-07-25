package team08.smartchoice;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;

/**
 * Created by roope on 7/24/2016.
 */
public class CustomItemsListViewAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] itemName;
    private final String[] imgid;
    private String[] originalPrice;
    private String[] discountPrice;
    private String[] expiryDate;
    private ImageView imageView;

    public CustomItemsListViewAdapter(Activity context, String[] itemName, String[] imageUrl,
                                      String[] originalPrice, String[] discountPrice, String[] expiryDate) {
        super(context, R.layout.seller_items_list, itemName);
        this.context = context;
        this.itemName = itemName;
        this.imgid = imageUrl;
        this.originalPrice = originalPrice;
        this.discountPrice = discountPrice;
        this.expiryDate = expiryDate;
    }

    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.seller_items_list, null, true);

        TextView textTitle = (TextView) rowView.findViewById(R.id.imageTitle);
        imageView = (ImageView) rowView.findViewById(R.id.imageView);
        TextView textOriginalPrice = (TextView) rowView.findViewById(R.id.list_item_original_price);
        TextView textDiscountPrice = (TextView) rowView.findViewById(R.id.list_item_discount_price);
        TextView textExpiryDate = (TextView) rowView.findViewById(R.id.list_item_expiry_date);

        textOriginalPrice.setText("Original Price : $"+ originalPrice[position]);
        textDiscountPrice.setText("Discount Price : $" + discountPrice[position]);
        textExpiryDate.setText("Deal Expiry Date : " + expiryDate[position]);
        textTitle.setText(itemName[position]);
        new DownloadImageTask((ImageView) rowView.findViewById(R.id.imageView)).execute(imgid[position]);
        return rowView;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
