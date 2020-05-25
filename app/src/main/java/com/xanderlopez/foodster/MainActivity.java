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

    private static final String TAG = "Message";

    List<Restaurant> propertyList = new ArrayList<>();
    String[] expenseType = {"Home Rent", "Eating Out", "Travel", "Shopping"};
    FirebaseFirestore db;
    RecyclerView recyclerView;
    ItemAdapter itemAdapter;
    public static String PACKAGE_NAME;

    BottomNavigationView bottomNavigationView;
    Fragment homeRestaurantFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Foodster");

        PACKAGE_NAME = getApplicationContext().getPackageName();

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.propertyRecyclerView);
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

        // Create a new user with a first and last name
//        Map<String, Object> user = new HashMap<>();
//        user.put("description", "Spaghetti | Pasta | Cheese");
//        user.put("image", "berry_bread_breakfast");
//        user.put("name", "Cheese Pasta");
//        user.put("price", "10.50");
//
//// Add a new document with a generated ID
////        db.collection("items")
////                .add(user)
////                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
////                    @Override
////                    public void onSuccess(DocumentReference documentReference) {
////                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
////                    }
////                })
////                .addOnFailureListener(new OnFailureListener() {
////                    @Override
////                    public void onFailure(@NonNull Exception e) {
////                        Log.w(TAG, "Error adding document", e);
////                    }
////                });

        //Query
        Query  query = db.collection("items");
        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(10)
                .setPageSize(3)
                .build();

        FirestorePagingOptions<ItemClass> options = new FirestorePagingOptions.Builder<ItemClass>()
                .setLifecycleOwner(this)
                .setQuery(query, config, ItemClass.class)
                .build();

        itemAdapter = new ItemAdapter(options, this.getApplicationContext(), PACKAGE_NAME, this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(itemAdapter);

        homeRestaurantFragment = new HomeRestaurantFragment();
//
    }

//
//    @Override
//    public void onItemClicked(int position) {
//
//    }

    public  void addToCart(View view) {
        Log.d(TAG, "add to cart");
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

        recyclerView.setVisibility(View.INVISIBLE);

        Bundle bundle = new Bundle();
        bundle.putString("PACKAGE_NAME", PACKAGE_NAME);

        homeRestaurantFragment.setArguments(bundle);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);

        ft.replace(R.id.activity_main, homeRestaurantFragment);
        ft.show(homeRestaurantFragment);
        ft.commit();

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
* */
