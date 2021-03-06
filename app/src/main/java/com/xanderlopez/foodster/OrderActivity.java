package com.xanderlopez.foodster;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
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

import java.text.NumberFormat;

public class OrderActivity extends AppCompatActivity implements OrderAdapter.OnListItemClicked {

    /* Declare variables */
    private static final String TAG = "Message";
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    public static String PACKAGE_NAME;
    private FirebaseAuth mAuth;
    private TextView noOrderLabel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Foodster / Order");

        /* Set second content view layout */
        setContentView(R.layout.order);

        /* Instantiate Firebase authentication */
        mAuth = FirebaseAuth.getInstance();

        /* Call bottom navigation function */
        this.bottomNavigation();

        /* Instantiate PACKAGE_NAME */
        PACKAGE_NAME = getApplicationContext().getPackageName();

        /* Instantiate database */
        db = FirebaseFirestore.getInstance();

        /* Instantiate recycler view and other properties */
        recyclerView = findViewById(R.id.propertyRecyclerView);
        noOrderLabel = findViewById(R.id.noOrderLabel);
        noOrderLabel.setVisibility(View.INVISIBLE);

        /* Get the current logged in user */
        FirebaseUser user = mAuth.getCurrentUser();
        String userID = user != null ? user.getUid() : "null";

        /* retrieve carts collection where userID field is equal to the current logged in user and ordered field is true */
        Query query = db.collection("carts").whereEqualTo("userID", userID).whereEqualTo("ordered",true).orderBy("orderID");
        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(10)
                .setPageSize(3)
                .build();

        FirestorePagingOptions<CartClass> options = new FirestorePagingOptions.Builder<CartClass>()
                .setLifecycleOwner(this)
                .setQuery(query, config, CartClass.class)
                .build();

        /* Instantiate adapter. Pass firebase query to the adapter */
        orderAdapter = new OrderAdapter(options, this.getApplicationContext(), PACKAGE_NAME, this);

        /* Configure recycler view */
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(orderAdapter);

        /* If order is empty, show that the order is empty */
        query.get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        double total = 0.0;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            total += Double.parseDouble(String.valueOf(document.get("subtotal")));
                        }

                        if(total == 0) {
                            noOrderLabel.setVisibility(View.VISIBLE);
                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                }
            });

    }

    /* When user clicks an item on the list */
    @Override
    public void onItemClick(DocumentSnapshot snapshot, int position) {
        Log.d(TAG, position + " " + snapshot.getId());
    }

    /* Function to set the functionality of the bottom navigation */
    public  void bottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.getMenu().getItem(2).setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        Log.d(TAG, "Home");
                        Intent homeIntent = new Intent(OrderActivity.this, MainActivity.class);
                        startActivityForResult(homeIntent, 0);
                        return  true;
                    case R.id.cart:
                        Log.d(TAG, "Cart");
                        Intent cartIntent = new Intent(OrderActivity.this, CartActivity.class);
                        startActivityForResult(cartIntent, 0);
                        return  true;
                    case R.id.order:
                        Log.d(TAG, "Order");
                        Intent orderIntent = new Intent(OrderActivity.this, OrderActivity.class);
                        startActivityForResult(orderIntent, 0);
                        return  true;
                    case R.id.profile:
                        Log.d(TAG, "Profile");
                        Intent profileIntent = new Intent(OrderActivity.this, ProfileActivity.class);
                        startActivityForResult(profileIntent, 0);
                        return  true;
                    default:
                        return false;
                }
            }
        });

    }
}
