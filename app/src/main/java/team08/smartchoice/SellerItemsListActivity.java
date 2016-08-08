package team08.smartchoice;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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

    private MapView mapView;
    private GoogleMap googleMaps;
    private Double latitude;
    private Double longitude;

    private Point p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_items_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        setTitle(getIntent().getExtras().getString("storeName"));
        //Get Firebase Database Reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        sellerID = getIntent().getExtras().getString("sellerID");

        mapView = (MapView) findViewById(R.id.store_map_view);
        mapView.onCreate(savedInstanceState);


        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMaps = googleMap;
                googleMaps.setMyLocationEnabled(true);
                googleMaps.getUiSettings().setMyLocationButtonEnabled(true);
            }
        });

        Log.d("Seller ID", sellerID.toString());
        connectToFirebase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void setSellerLocation(){
        MapsInitializer.initialize(getApplicationContext());
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(this.latitude, this.longitude))
                .zoom(15).bearing(90).tilt(40).build();
        this.googleMaps.addMarker(new MarkerOptions().
                position(new LatLng(this.latitude, this.longitude)));
        this.googleMaps.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    //Set List View Properties
    private void customSellerItemsListLoadAdapter(){
        CustomItemsListViewAdapter adapter = new CustomItemsListViewAdapter(this, itemName,
                imageURL, originalPrice, discountPrice, expiryDate);
        list = (ListView) findViewById(R.id.seller_items_listview);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("On Item Click", "sdcscs");
                if ( p!= null ){
                    showPopup(SellerItemsListActivity.this, p);
                }
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        //Initialize the Point with x, and y positions
        p = new Point();
        p.x = 200;
        p.y = 500;
        Log.d("Location ag",String.valueOf(p.x));
        Log.d("Location ag",String.valueOf(p.y));
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

        mDatabase.child("locations").child(sellerID).child("l")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                latitude = Double.valueOf(dataSnapshot.child("0").getValue().toString());
                longitude = Double.valueOf(dataSnapshot.child("1").getValue().toString());
                setSellerLocation();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    // The method that displays the popup.
    private void showPopup(final Activity context, Point p) {
        int popupWidth = 700;
        int popupHeight = 400;

        // Inflate the popup_layout.xml
        LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.popup);
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.popup_layout, viewGroup);

        // Creating the PopupWindow
        final PopupWindow popup = new PopupWindow(context);
        popup.setContentView(layout);
        popup.setWidth(popupWidth);
        popup.setHeight(popupHeight);
        popup.setFocusable(true);

        // Some offset to align the popup a bit to the right, and a bit down, relative to button's position.
        int OFFSET_X = 30;
        int OFFSET_Y = 30;

        // Clear the default translucent background
        popup.setBackgroundDrawable(new BitmapDrawable());

        // Displaying the popup at the specified location, + offsets.
        popup.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);

        // Getting a reference to Close button, and close the popup when clicked.
        Button close = (Button) layout.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });
    }

}
