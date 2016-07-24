package team08.smartchoice;

import android.app.DialogFragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class SellerDashboard extends AppCompatActivity {
    DatePickerFragment datePickerFragment;
    TextView itemName;
    TextView originalPrice;
    TextView discountPrice;
    Button expiryDate;
    Button imageUrl;
    Button viewPrevious;
    Button addAnother;
    Button addGohome;
    String userID;
    private DatabaseReference mDatabase;
    private FirebaseStorage storage;
    Uri imageContentURI;
    StorageReference storageRef;
    String imageDownloadURL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Seller Dashboard");
        userID = getIntent().getExtras().getString("userID");

        itemName = (TextView) findViewById(R.id.item_name);
        originalPrice = (TextView) findViewById(R.id.original_cost);
        discountPrice = (TextView) findViewById(R.id.discount_cost);
        viewPrevious = (Button) findViewById(R.id.view_previousitems);
        addAnother = (Button) findViewById(R.id.item_add_another);
        addGohome = (Button) findViewById(R.id.submit_gotohome);
        expiryDate = (Button) findViewById(R.id.dealExpiry_button);
        imageUrl = (Button) findViewById(R.id.item_image);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://smart-choice-81134.appspot.com");

        addAnother.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewItem(true);
            }
        });

        addGohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewItem(false);
            }
        });

        Button uploadImage = (Button) findViewById(R.id.item_image);

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
                startActivityForResult(intent, 0);
            }
        });
    }

    private void addNewItem(Boolean flag) {
        ItemDetails itemDetails = new ItemDetails(itemName.getText().toString(),
                Integer.parseInt(originalPrice.getText().toString()),
                Integer.parseInt(discountPrice.getText().toString()),
                datePickerFragment.getSelectedDate(),
                imageDownloadURL);

        String key = mDatabase.child("Items").child(userID).push().getKey();
        Map<String, Object> postData = itemDetails.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/Items/" + userID + "/" + key, postData);

        Log.d("Path String",userID.toString());

        mDatabase.updateChildren(childUpdates);

        Log.d("Path String",userID.toString());

        if (flag){
            Intent intent = new Intent(getApplicationContext(), SellerDashboard.class);
            intent.putExtra("userID", userID);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    private void uploadImage(){
        Uri file = Uri.fromFile(new File(getPathFromURI()));
        Log.d("File ", file + "");
        StorageReference storageReference = storageRef.child(userID).child("item_images/"+file.getLastPathSegment());
        UploadTask uploadTask = storageReference.putFile(file);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("failure", "onFailure: failed");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("Success" ,taskSnapshot.getDownloadUrl().toString());
                imageDownloadURL = taskSnapshot.getDownloadUrl().toString();
                Log.d("success", "onSuccess: success");
            }
        });
    }

    private String getPathFromURI(){
        String result;
        Cursor cursor = getContentResolver().query(imageContentURI,null,null,null,null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        result = cursor.getString(idx);
        cursor.close();
        return result;
    }

    public void showDatePickerDialog(View view){
        datePickerFragment = new DatePickerFragment();
        datePickerFragment.show(getFragmentManager(),"date_Picker");
        //Log.d("Date Selected", datePickerFragment.getSelectedDate());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Print Image", "Hey I'm here");
        if (resultCode == RESULT_OK) {
            imageContentURI = data.getData();
            Log.d("Image URL",data.getData().toString());
            uploadImage();
        }
    }
}