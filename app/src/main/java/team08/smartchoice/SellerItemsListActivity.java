package team08.smartchoice;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ListView;

import com.firebase.geofire.GeoLocation;
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
}
