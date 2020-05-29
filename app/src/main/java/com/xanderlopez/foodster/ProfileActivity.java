package com.xanderlopez.foodster;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "Message";

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Set second content view layout */
        setContentView(R.layout.profile);

        bottomNavigation();

        mAuth = FirebaseAuth.getInstance();

//        Button signupButton = (Button) findViewById(R.id.signupButton);
//        signupButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                createUserWithEmailAndPassword(v);
//            }
//        });

//        Button signinpButton = (Button) findViewById(R.id.signinButton);
//        signinpButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                signInWithEmailAndPassword(v);
//            }
//        });

        Button logoutButton = (Button) findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                setContentView(R.layout.profile_signin);
            }
        });



        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            //Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();

            TextView nameTextView = (TextView) findViewById(R.id.nameTextView);
            nameTextView.setText(email);

            Log.d(TAG, "name " + name);
            Log.d(TAG, "email " + email);

            //setContentView(R.layout.profile);
        } else {
            //setContentView(R.layout.profile_signin);
        }



    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
        Log.d(TAG, "currentUser " + currentUser);
    }

    public void createUserWithEmailAndPassword(View view) {

        TextView emailInput = (TextView) findViewById(R.id.emailInput);
        TextView passwordInput = (TextView) findViewById(R.id.passwordInput);
        TextView passwordConfirmInput = (TextView) findViewById(R.id.passwordConfirmInput);

        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        setContentView(R.layout.profile_signin);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(ProfileActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        //updateUI(null);
                    }

                    // ...
                }
            });
    }

    public  void signInWithEmailAndPassword(View view) {

        TextView emailInput = (TextView) findViewById(R.id.emailInput);
        TextView passwordInput = (TextView) findViewById(R.id.passwordInput);

        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    setContentView(R.layout.profile);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(ProfileActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    //updateUI(null);
                }

                    // ...
                }
            });
    }

    public  void bottomNavigation() {

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.getMenu().getItem(3).setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        Log.d(TAG, "Home");
                        Intent homeIntent = new Intent(ProfileActivity.this, MainActivity.class);
                        startActivityForResult(homeIntent, 0);
                        return  true;
                    case R.id.cart:
                        Log.d(TAG, "Cart");
                        Intent cartIntent = new Intent(ProfileActivity.this, CartActivity.class);
                        startActivityForResult(cartIntent, 0);
                        return  true;
                    case R.id.order:
                        Log.d(TAG, "Order");
                        Intent orderIntent = new Intent(ProfileActivity.this, OrderActivity.class);
                        startActivityForResult(orderIntent, 0);
                        return  true;
                    case R.id.profile:
                        Log.d(TAG, "Profile");
                        Intent profileIntent = new Intent(ProfileActivity.this, ProfileActivity.class);
                        startActivityForResult(profileIntent, 0);
                        return  true;
                    default:
                        return false;
                }
            }
        });

    }
}
