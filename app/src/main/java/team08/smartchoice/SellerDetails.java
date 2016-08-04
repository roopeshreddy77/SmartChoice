package team08.smartchoice;

import android.content.Intent;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.geofire.LocationCallback;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class SellerDetails extends AppCompatActivity {

    Button submit;
    TextView storeName;
    TextView addressLine1;
    TextView addressLine2;
    TextView city;
    TextView state;
    TextView zip;
    String userID;
    private Double latitude;
    private Double logitude;

    private DatabaseReference mDatabase;
    private GetLocationCoordinates getLocationCoordinates;
    private GeoFire geoFire;
    private GeoQuery geoQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        userID = getIntent().getExtras().getString("userID");

        storeName = (TextView) findViewById(R.id.store_name);
        addressLine1 = (TextView) findViewById(R.id.store_street1);
        addressLine2 = (TextView) findViewById(R.id.store_street2);
        city = (TextView) findViewById(R.id.store_city);
        state = (TextView) findViewById(R.id.store_state);
        zip = (TextView) findViewById(R.id.store_zip);
        submit = (Button) findViewById(R.id.store_submit);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        geoFire = new GeoFire(mDatabase);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCoOrdinates();
            }
        });
    }

    /*
    // Get User Location CoOrdintes based on Address
     */
    private void getCoOrdinates(){
        Log.d("getCoOrdinates", "Start");
        String constructedAddress = addressLine1.getText().toString() +"," + city.getText().toString()
                +"," + state.getText().toString() + "," + zip.getText().toString();
        Log.d("Seller Address", constructedAddress);

        getLocationCoordinates = new GetLocationCoordinates(constructedAddress,
                new GetLocationCoordinates.AsyncResponse() {
            @Override
            public void processFinish(Map<String, Double> map) {
                latitude = map.get("Lat");
                logitude = map.get("Lng");
                Log.d("Map", latitude.toString());
                Log.d("Map", logitude.toString());
                addNewSeller();
            }
        });

        getLocationCoordinates.execute();

        Intent intent = new Intent(this, SellerDashboard.class);
        intent.putExtra("userID", userID.toString());
        startActivity(intent);
        Log.d("getCoOrdinates", "End");
    }

    /*
     / Add new Seller to Firebase
     */
    private void addNewSeller(){
        Log.d("addNewSeller", "Start");
        // Update Firebase with GeoFire - Save Seller's CoOrdinates with Key
        geoFire.setLocation("locations/"+userID, new GeoLocation(latitude, logitude), new GeoFire.CompletionListener(){
            @Override
            public void onComplete(String key, DatabaseError error) {
                if (error != null){
                    Log.d("Geo Fire", "error");
                } else {
                    Log.d("Geo Fire", "Update SuccessFull");
                    Log.d("Geo Fire KEY", key);
                }
            }
        });
        Seller seller = new Seller(userID, storeName.getText().toString(), new Address(addressLine1.getText().toString(),
                addressLine2.getText().toString(),
                city.getText().toString(), state.getText().toString(),
                Integer.parseInt(zip.getText().toString())));
        mDatabase.child("sellers").child(userID).setValue(seller);
        Log.d("addNewSeller", "End");
    }
}
