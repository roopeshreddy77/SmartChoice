package team08.smartchoice;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SellerDetails extends AppCompatActivity {
    Button submit;
    TextView storeName;
    TextView addressLine1;
    TextView addressLine2;
    TextView city;
    TextView state;
    TextView zip;
    String userID;
    private DatabaseReference mDatabase;

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

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeNewSeller();
            }
        });
    }


    public class Seller {
        public String storeName;
        public String addressLine1;
        public String addressLine2;
        public String city;
        public String state;
        public int zip;

        public Seller(){}
        public Seller(String storeName, String addressLine1, String addressLine2, String city, String state, int zip){
            this.storeName = storeName;
            this.addressLine1 = addressLine1;
            this.addressLine2 = addressLine2;
            this.city = city;
            this.state = state;
            this.zip = zip;

        }
    }

    private void writeNewSeller(){
        Seller seller = new Seller(storeName.getText().toString(), addressLine1.getText().toString(),
                addressLine2.getText().toString(),
                city.getText().toString(), state.getText().toString(),
                Integer.parseInt(zip.getText().toString()));

        mDatabase.child("0").child("seller").child(userID).setValue(seller);
    }





}
