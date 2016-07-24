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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    TextView emailID;
    TextView password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Login");

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    Log.d("FireBase Auth", "User Logged In");
                } else {
                    Log.d("FireBase Auth","User Logged Out");
                }
            }
        };
        emailID = (TextView) findViewById(R.id.login_email);
        password = (TextView) findViewById(R.id.login_password);
        Button signUpButton = (Button) findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            }
        });
        Button loginButton =(Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticateUser();
            }
        });
    }

    private void authenticateUser(){

        AuthCredential credential = EmailAuthProvider.getCredential(emailID.getText().toString(),
                password.getText().toString());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("Login Fragment", "Login Status :: " + task.isSuccessful());
                        if (!task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Authentication Failed",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("User ID is ", mAuth.getCurrentUser().getUid().toString());
                            Intent intent = new Intent(getApplicationContext(), SellerDashboard.class);
                            intent.putExtra("userID",mAuth.getCurrentUser().getUid().toString());
                            startActivity(intent);
                        }
            }
        });
    }
}
