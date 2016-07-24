package team08.smartchoice;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by roope on 7/23/2016.
 */
public class CustomHomeListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] storeName;
    private final String[] storeAddress;


    public CustomHomeListAdapter(Activity context, String[] storeName, String[] storeAddress){
        super(context, R.layout.seller_list, storeName);
        this.context = context;
        this.storeName = storeName;
        this.storeAddress = storeAddress;
    }

    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.seller_list, null, true);

        TextView storeNameText = (TextView) rowView.findViewById(R.id.list_store_name);
        TextView storeAddressText = (TextView) rowView.findViewById(R.id.list_store_address);

        storeNameText.setText(storeName[position]);
        storeAddressText.setText(storeAddress[position]);

        return rowView;

    }

}
