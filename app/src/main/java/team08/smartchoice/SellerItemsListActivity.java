package team08.smartchoice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SellerItemsListActivity extends AppCompatActivity {

    private String sellerID;
    private DatabaseReference mDatabase;
    private String[] itemName;
    private String[] originalPrice;
    private String[] discountPrice;
    private String[] expiryDate;
    private String[] imageURL;

    private int count = 0;
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_items_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        setTitle("Items");
        //Get Firebase Database Reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        sellerID = getIntent().getExtras().getString("sellerID");
        Log.d("Seller ID", sellerID.toString());
        connectToFirebase();
    }

    //Set List View Properties
    private void customSellerItemsListLoadAdapter(){
        CustomItemsListViewAdapter adapter = new CustomItemsListViewAdapter(this, itemName,
                imageURL, originalPrice, discountPrice, expiryDate);
        list = (ListView) findViewById(R.id.seller_items_listview);
        list.setAdapter(adapter);
    }

    //Load Items
    private void connectToFirebase(){
        mDatabase.child("Items").child(sellerID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long childCount = dataSnapshot.getChildrenCount();
                itemName = new String[childCount.intValue()];
                originalPrice = new String[childCount.intValue()];
                discountPrice = new String[childCount.intValue()];
                expiryDate = new String[childCount.intValue()];
                imageURL = new String[childCount.intValue()];
                for (DataSnapshot child: dataSnapshot.getChildren()){
                    itemName[count] = child.child("itemName").getValue().toString();
                    originalPrice[count] = child.child("originalPrice").getValue().toString();
                    discountPrice[count] = child.child("discountPrice").getValue().toString();
                    expiryDate[count] = child.child("expiryDate").getValue().toString();
                    imageURL[count] = child.child("imageUrl").getValue().toString();
                    count++;
                    Log.d("Item Name", child.child("itemName").getValue().toString());
                }
                customSellerItemsListLoadAdapter();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
