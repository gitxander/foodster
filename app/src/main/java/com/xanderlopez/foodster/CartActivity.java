package com.xanderlopez.foodster;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnListItemClicked {

    private static final String TAG = "Message";

    FirebaseFirestore db;
    RecyclerView recyclerView;
    CartAdapter cartAdapter;
    Fragment checkoutFragment;
    Button checkoutButton;
    TextView emptyCart;
    TextView totalLabel, totalLabel2;
    private FirebaseAuth mAuth;
    double total = 0.0;

    public static String PACKAGE_NAME;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Set second content view layout */
        setContentView(R.layout.cart);

        mAuth = FirebaseAuth.getInstance();

        this.bottomNavigation();

        PACKAGE_NAME = getApplicationContext().getPackageName();

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.propertyRecyclerView);

        FirebaseUser user = mAuth.getCurrentUser();
        String userID = user != null ? user.getUid() : "null";

        //Query
        Query query = db.collection("carts").whereEqualTo("userID", userID).whereEqualTo("ordered",false);
        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(10)
                .setPageSize(3)
                .build();

        FirestorePagingOptions<CartClass> options = new FirestorePagingOptions.Builder<CartClass>()
                .setLifecycleOwner(this)
                .setQuery(query, config, CartClass.class)
                .build();

        cartAdapter = new CartAdapter(options, this.getApplicationContext(), PACKAGE_NAME, this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(cartAdapter);

        checkoutFragment = new CheckoutFragment();




        query.get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());

                            total += Double.parseDouble(String.valueOf(document.get("subtotal")));

                        }

                        totalLabel = findViewById(R.id.totalLabel);
                        totalLabel2 = findViewById(R.id.totalLabel2);
                        totalLabel.setText("$"+NumberFormat.getInstance().format(round(total)));

                        if(total == 0) {
                            recyclerView.setVisibility(View.GONE);
                            //emptyCart.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                }
            });

        checkoutButton = (Button) findViewById(R.id.checkoutButton);
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkout();
            }
        });

    }

    public void checkout() {
        recyclerView.setVisibility(View.INVISIBLE);
        checkoutButton.setVisibility(View.INVISIBLE);
        totalLabel.setVisibility(View.INVISIBLE);
        totalLabel2.setVisibility(View.INVISIBLE);

        Bundle bundle = new Bundle();
        bundle.putString("PACKAGE_NAME", PACKAGE_NAME);
        bundle.putDouble("TOTAL", total);

        checkoutFragment.setArguments(bundle);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);

        ft.replace(R.id.cart, checkoutFragment);
        ft.show(checkoutFragment);
        ft.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        cartAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        cartAdapter.stopListening();
    }

    @Override
    public void onItemClick(DocumentSnapshot snapshot, int position) {
        Log.d(TAG, position + " " + snapshot.getId());

//        /* Initialise new intent */
//        Intent intent = new Intent(this, HomeMenuActivity.class);
//
//        /* Pass expense type data to the second activity */
//        intent.putExtra("EXPENSE_TYPE", position + " " + snapshot.getId());
//
//        /* Start next activity */
//        startActivityForResult(intent, 0);


    }

    /*  function for rounding off to two decimal places
     *   https://stackoverflow.com/questions/2808535/round-a-double-to-2-decimal-places  */
    public static double round(double value) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public  void bottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.getMenu().getItem(1).setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        Log.d(TAG, "Home");
                        Intent homeIntent = new Intent(CartActivity.this, MainActivity.class);
                        startActivityForResult(homeIntent, 0);
                        return  true;
                    case R.id.cart:
                        Log.d(TAG, "Cart");
                        Intent cartIntent = new Intent(CartActivity.this, CartActivity.class);
                        startActivityForResult(cartIntent, 0);
                        return  true;
                    case R.id.order:
                        Log.d(TAG, "Order");
                        Intent orderIntent = new Intent(CartActivity.this, OrderActivity.class);
                        startActivityForResult(orderIntent, 0);
                        return  true;
                    case R.id.profile:
                        Log.d(TAG, "Profile");
                        Intent profileIntent = new Intent(CartActivity.this, ProfileActivity.class);
                        startActivityForResult(profileIntent, 0);
                        return  true;
                    default:
                        return false;
                }
            }
        });
    }

}
