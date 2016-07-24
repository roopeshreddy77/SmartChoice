package team08.smartchoice;

import android.content.Intent;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference mDatabase;
    private Integer count = 0;

    private String[] storeName = new String[10];

    private String[] storeAddress = new String[10];

    private String[] sellerID = new String[10] ;

    private ListView list;

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
        //sellersListAdapter();
        loadSellerList();
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
                Log.d("Seller Id", seletedItem.toString());
                Intent intent = new Intent(getApplicationContext(),SellerItemsListActivity.class);
                intent.putExtra("sellerID", seletedItem);
                startActivityForResult(intent, 0);
                Toast.makeText(MainActivity.this, seletedItem, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSellerList(){
        mDatabase.child("sellers")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("HYeyyyyy", "Database");
                        Long childCount = dataSnapshot.getChildrenCount();
                        Log.d("Child COunt", String.valueOf(dataSnapshot.getChildrenCount()));
                        for (DataSnapshot child : dataSnapshot.getChildren()){
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
            ContactFragment contactFragment = new ContactFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(
                    R.id.relativelayout_for_fragment,
                    contactFragment,
                    contactFragment.getTag()).addToBackStack( "contact" ).commit();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
