package com.xanderlopez.foodster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter_LifecycleAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements HomeAdapter.OnListItemClicked {

    private static final String TAG = "Message";

    List<Restaurant> propertyList = new ArrayList<>();
    String[] expenseType = {"Home Rent", "Eating Out", "Travel", "Shopping"};
    FirebaseFirestore db;
    RecyclerView recyclerView;
    HomeAdapter adapter;
    public static String PACKAGE_NAME;

    BottomNavigationView bottomNavigationView;

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
                        return  true;
                    case R.id.cart:
                        Log.d(TAG, "Cart");
                        return  true;
                    case R.id.account:
                        Log.d(TAG, "Account");
                        return  true;
                    default:
                        return false;
                }
            }
        });

//        // Create a new user with a first and last name
//        Map<String, Object> user = new HashMap<>();
//        user.put("description", "Cheeseburger | French Fries | Cokes");
//        user.put("image", "berry_bread_breakfast");
//        user.put("name", "Burger and Fries");
//
//// Add a new document with a generated ID
//        db.collection("restaurants")
//                .add(user)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error adding document", e);
//                    }
//                });

        //Query
        Query  query = db.collection("restaurants");
        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(10)
                .setPageSize(3)
                .build();

        FirestorePagingOptions<Restaurant> options = new FirestorePagingOptions.Builder<Restaurant>()
                .setLifecycleOwner(this)
                .setQuery(query, config, Restaurant.class)
                .build();

        adapter = new HomeAdapter(options, this.getApplicationContext(), PACKAGE_NAME, this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }

//
//    @Override
//    public void onItemClicked(int position) {
//
//    }



    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onItemClick(DocumentSnapshot snapshot, int position) {
        Log.d("ITEM_CLICK", position + " " + snapshot.getId());
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
