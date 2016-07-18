package team08.smartchoice;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity implements OnCompleteListener<AuthResult>, View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText emailid;
    private EditText password;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("xyx", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("xyz", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        Button button = (Button)findViewById(R.id.register);
        button.setOnClickListener(this);

        emailid = (EditText)findViewById(R.id.signup_email);
        password = (EditText)findViewById(R.id.signup_password);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        Log.d("", "createUserWithEmail:onComplete:" + task.isSuccessful());

        // If sign in fails, display a message to the user. If sign in succeeds
        // the auth state listener will be notified and logic to handle the
        // signed in user can be handled in the listener.
        if (!task.isSuccessful()) {
            Toast.makeText(SignupActivity.this, "Authentication failed.",
                    Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, SellerDetails.class);
            startActivity(intent);
        }


        // ...
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.register:
                mAuth.createUserWithEmailAndPassword(emailid.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(this, this);
                break;
        }

    }
}
