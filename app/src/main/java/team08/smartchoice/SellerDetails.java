package team08.smartchoice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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


    private void writeNewSeller(){
        Seller seller = new Seller(userID, storeName.getText().toString(), new Address(addressLine1.getText().toString(),
                addressLine2.getText().toString(),
                city.getText().toString(), state.getText().toString(),
                Integer.parseInt(zip.getText().toString())));


        mDatabase.child("sellers").child(userID).setValue(seller);
        Intent intent = new Intent(this, SellerDashboard.class);
        intent.putExtra("userID", userID.toString());
        startActivity(intent);


    }





}
