package com.xanderlopez.foodster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements ItemAdapter.OnListItemClicked {

    /* Declare variables */
    private static final String TAG = "Message";
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    public static String PACKAGE_NAME;
    private BottomNavigationView bottomNavigationView;
    private  Fragment itemFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Foodster");

        /* Instantiate PACKAGE_NAME */
        PACKAGE_NAME = getApplicationContext().getPackageName();

        /* Instantiate database */
        db = FirebaseFirestore.getInstance();

        /* Instantiate recycler view */
        recyclerView = findViewById(R.id.propertyRecyclerView);

        /* Call bottom navigation function */
        this.bottomNavigation();

        /* retrieve items collection */
        Query query = db.collection("items");
        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(10)
                .setPageSize(3)
                .build();

        FirestorePagingOptions<ItemClass> options = new FirestorePagingOptions.Builder<ItemClass>()
                .setLifecycleOwner(this)
                .setQuery(query, config, ItemClass.class)
                .build();

        /* Instantiate adapter. Pass firebase query to the adapter */
        itemAdapter = new ItemAdapter(options, this.getApplicationContext(), PACKAGE_NAME, this);

        /* Configure recycler view */
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(itemAdapter);

        /* Instantiate a new item fragment */
        itemFragment = new ItemFragment();

    }

    @Override
    protected void onStart() {
        super.onStart();
        itemAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        itemAdapter.stopListening();
    }

    /* Function to show the item add to cart */
    @Override
    public void onItemClick(DocumentSnapshot snapshot, int position) {
        Log.d(TAG, position + " " + snapshot.getId());

        /* Hide recycler view */
        recyclerView.setVisibility(View.INVISIBLE);

        /* Set variables to be pass on the fragment */
        Bundle bundle = new Bundle();
        bundle.putString("PACKAGE_NAME", PACKAGE_NAME);
        bundle.putString("name", (String) snapshot.get("name"));

        /* Pass variables to the fragment */
        itemFragment.setArguments(bundle);

        /* Begin fragment transaction */
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);

        ft.replace(R.id.activity_main, itemFragment);
        ft.show(itemFragment);
        ft.commit();

    }

    /* Function to set the functionality of the bottom navigation */
    public void bottomNavigation() {

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        Log.d(TAG, "Home");
                        Intent homeIntent = new Intent(MainActivity.this, MainActivity.class);
                        startActivityForResult(homeIntent, 0);
                        return  true;
                    case R.id.cart:
                        Log.d(TAG, "Cart");
                        Intent cartIntent = new Intent(MainActivity.this, CartActivity.class);
                        startActivityForResult(cartIntent, 0);
                        return  true;
                    case R.id.order:
                        Log.d(TAG, "Order");
                        Intent orderIntent = new Intent(MainActivity.this, OrderActivity.class);
                        startActivityForResult(orderIntent, 0);
                        return  true;
                    case R.id.profile:
                        Log.d(TAG, "Profile");
                        Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
                        startActivityForResult(profileIntent, 0);
                        return  true;
                    default:
                        return false;
                }
            }
        });

    }
}

/*
* References:
*
* Firestore Database to RecyclerView - Android Studio Tutorial
* https://www.youtube.com/watch?v=cBwaJYocb9I&list=RDCMUCl6DxakCjDR5AfRwWhWNbMg&index=1
*
* Click Events for RecyclerView using Firestore Adapters
* https://www.youtube.com/watch?v=JLW7z_AaUHA
*
* How to get package name from anywhere?
* https://stackoverflow.com/questions/6589797/how-to-get-package-name-from-anywhere
*
* Firestore Paging Adapter- How to know if query returns 0 results
* https://stackoverflow.com/questions/57441563/firestore-paging-adapter-how-to-know-if-query-returns-0-results
*
* How can I set OnClickListener to two buttons in RecyclerView?
* https://stackoverflow.com/questions/45474333/how-can-i-set-onclicklistener-to-two-buttons-in-recyclerview/45474550
*
* Handle Button click inside a row in RecyclerView
* https://stackoverflow.com/questions/30284067/handle-button-click-inside-a-row-in-recyclerview
* */
