package team08.smartchoice;


import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment{

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;

    public LoginFragment() {
        // Required empty public constructor
    }

    TextView emailID;
    TextView password;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("Yikes", "ascasd");
        View view =  inflater.inflate(R.layout.fragment_login, container, false);
        Log.d("Yikes", "ascasasasasasasadafdcasd");
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
        emailID = (TextView) view.findViewById(R.id.login_email);
        password = (TextView) view.findViewById(R.id.login_password);
        Button signUpButton = (Button)view.findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(getActivity(), SignupActivity.class);
                startActivity(intent);
            }
        });
        Button loginButton =(Button) view.findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticateUser();
            }
        });
        return view;
    }

    private void authenticateUser(){

        AuthCredential credential = EmailAuthProvider.getCredential(emailID.getText().toString(),
                password.getText().toString());

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Login Fragment", "Login Status :: " + task.isSuccessful());
                        if (!task.isSuccessful()){
                            Toast.makeText(getActivity(),"Authentication Failed",Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("User ID is ", mAuth.getCurrentUser().getUid().toString());
                            Intent intent = new Intent(getActivity(), SellerDashboard.class);
                            intent.putExtra("userID",mAuth.getCurrentUser().getUid().toString());
                            startActivity(intent);
                        }
                    }
                });
    }
}
