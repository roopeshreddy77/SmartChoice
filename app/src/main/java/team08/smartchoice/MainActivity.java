package team08.smartchoice;

import android.content.Context;
import android.content.Intent;
import android.location.*;
import android.location.Address;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LocationListener {

    private MapView mapView;

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    public void setGoogleMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }
    private GoogleMap googleMap;
    private GeoQuery geoQuery;
    private GeoFire geoFire;
    private Map<String,Marker> markers;
    private CameraPosition cameraPosition;
    private DatabaseReference mDatabase;

    private ArrayList<String> keys;

    private String[] storeName;
    private String[] storeAddress;
    private String[] sellerID;

    private ListView list;

    private LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        keys = new ArrayList<>();

        mapView = (MapView) findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0 ,0 , this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0 ,0 , this);

        geoFire = new GeoFire(mDatabase.child("locations"));

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                setGoogleMap(googleMap);
            }
        });
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

    private void sellersListAdapter(){
        CustomSellersListViewAdapter adapter =
                new CustomSellersListViewAdapter(this, storeName, storeAddress);
        list = (ListView) findViewById(R.id.sellers_list_view);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Integer p = +position;
                String seletedItem = sellerID[p];
                String name = storeName[p];
                Log.d("Seller Id", seletedItem.toString());
                Log.d("Seller Name", name.toString());
                Intent intent = new Intent(getApplicationContext(),SellerItemsListActivity.class);
                intent.putExtra("sellerID", seletedItem);
                intent.putExtra("storeName", name);
                startActivityForResult(intent, 0);
            }
        });
    }

    private void loadSellerList(){
        mDatabase.child("sellers")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Integer numberOfSellers = 0;
                        Integer count = 0;
                        for (DataSnapshot children: dataSnapshot.getChildren()){
                            if (!keys.isEmpty() && keys.contains(children.child("sellerId").getValue().toString())){
                                numberOfSellers++;
                            }
                        }
                        storeName = new String[numberOfSellers];
                        storeAddress = new String[numberOfSellers];
                        sellerID = new String[numberOfSellers];
                        for (DataSnapshot child : dataSnapshot.getChildren()){
                            if (!keys.isEmpty() && keys.contains(child.child("sellerId").getValue().toString())){
                                Log.d("Store Name::",child.child("storeName").getValue().toString());
                                storeName[count] = child.child("storeName").getValue().toString();
                                String address = child.child("address").child("addrLine1").getValue().toString() +" "
                                        + child.child("address").child("addrLine2").getValue().toString() + ", "
                                        + child.child("address").child("city").getValue().toString() + ", "
                                        + child.child("address").child("state").getValue().toString() +". "
                                        + child.child("address").child("zip").getValue().toString();
                                storeAddress[count] = address;
                                sellerID[count] = child.child("sellerId").getValue().toString();
                                Log.d("Load Zip",child.child("address").child("zip").toString());
                                count++;
                            }
                        }
                        sellersListAdapter();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("DataBase Error", databaseError.getDetails());
                    }
                });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_seller_sign_in) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_help) {
            HelpFragment helpFragment = new HelpFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(
                    R.id.relativelayout_for_fragment,
                    helpFragment,
                    helpFragment.getTag()).addToBackStack( "help" ).commit();

        } else if (id == R.id.nav_about) {
            AboutFragment aboutFragment = new AboutFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(
                    R.id.relativelayout_for_fragment,
                    aboutFragment,
                    aboutFragment.getTag()).addToBackStack( "about" ).commit();

        } else if (id == R.id.nav_contact_us) {
            FirebaseAuth.getInstance().signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("Location Service", "Log" + location.getLongitude() + "Lat" + location.getLatitude());
        LatLng latLng = new LatLng(location.getLatitude(),
                location.getLongitude());
        MapsInitializer.initialize(getApplicationContext());
        this.geoQuery = this.geoFire.queryAtLocation(new
                GeoLocation(location.getLatitude(), location.getLongitude()), 3);
        cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(),
                        location.getLongitude())).zoom(15).bearing(90).tilt(40).build();
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        this.googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        geoQueryEventListener();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude",provider);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude",provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude",provider);
    }


    private void geoQueryEventListener(){
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Log.d("onKeyEntered", key);
                keys.add(key);
                Marker marker = googleMap.addMarker(new MarkerOptions().
                        position(new LatLng(location.latitude, location.longitude)));
                markers.put(key, marker);
            }

            @Override
            public void onKeyExited(String key) {
                Marker marker = markers.get(key);
                if (marker != null) {
                    marker.remove();
                    markers.remove(key);
                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                loadSellerList();
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

        this.markers = new HashMap<>();
    }
}
