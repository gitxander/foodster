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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private Fragment checkoutFragment;
    private Button checkoutButton;
    private TextView totalLabel, totalLabel2, emptyCartLabel;
    private FirebaseAuth mAuth;
    private double total = 0.0;

    public static String PACKAGE_NAME;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Foodster / Cart");

        /* Set content view layout */
        setContentView(R.layout.cart);

        /* Instantiate Firebase authentication */
        mAuth = FirebaseAuth.getInstance();

        /* Call bottom navigation function */
        this.bottomNavigation();

        /* Instantiate PACKAGE_NAME */
        PACKAGE_NAME = getApplicationContext().getPackageName();

        /* Instantiate database */
        db = FirebaseFirestore.getInstance();

        /* Instantiate recycler view */
        recyclerView = findViewById(R.id.propertyRecyclerView);

        /* Call function to retrieve cart */
        this.retrieveCart();

        /* Instantiate a new checkout fragment */
        checkoutFragment = new CheckoutFragment();

        /* Instantiate labels and button */
        emptyCartLabel = findViewById(R.id.noOrderLabel);
        totalLabel = findViewById(R.id.totalLabel);
        totalLabel2 = findViewById(R.id.totalLabel2);
        checkoutButton = findViewById(R.id.checkoutButton);

        /* Call function to change view when cart is empty */
        this.cartIsEmpty();

        /* Add onClickListener to checkout button */
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkout();
            }
        });

    }

    /* Retrieve cart items */
    public void retrieveCart() {

        /* Check if user is logged in */
        FirebaseUser user = mAuth.getCurrentUser();
        String userID = user != null ? user.getUid() : "null";

        /* retrieve carts collection where userID field is equal to the current logged in user and ordered field is false */
        Query query = db.collection("carts").whereEqualTo("userID", userID).whereEqualTo("ordered",false);
        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(10)
                .setPageSize(3)
                .build();

        FirestorePagingOptions<CartClass> options = new FirestorePagingOptions.Builder<CartClass>()
                .setLifecycleOwner(this)
                .setQuery(query, config, CartClass.class)
                .build();

        /* Instantiate adapter. Pass firebase query to the adapter */
        cartAdapter = new CartAdapter(options, this.getApplicationContext(), PACKAGE_NAME, this);

        /* Configure recycler view */
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(cartAdapter);

        /* call function to check if cart is empty */
        this.checkIfCartIsEmpty(query);
    }

    /* If cart is empty, show that the cart is empty */
    public void checkIfCartIsEmpty(Query query) {
        query.get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            /* Compute for the total price of the items in cart */
                            total += Double.parseDouble(String.valueOf(document.get("subtotal")));
                        }

                        /* Set total price on the label */
                        totalLabel.setText("$"+NumberFormat.getInstance().format(round(total)));

                        /* If there are no items, show that cart is empty. Otherwise show items */
                        if(total == 0) {
                            cartIsEmpty();
                            emptyCartLabel.setVisibility(View.VISIBLE);
                        } else {
                            cartIsNotEmpty();
                            emptyCartLabel.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                }
            });
    }

    /* Show/hide labels and button when cart is empty */
    public void cartIsEmpty() {
        emptyCartLabel.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        checkoutButton.setVisibility(View.INVISIBLE);
        totalLabel.setVisibility(View.INVISIBLE);
        totalLabel2.setVisibility(View.INVISIBLE);
    }

    /* Show/hide labels and button when cart is not empty */
    public void cartIsNotEmpty() {
        emptyCartLabel.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        checkoutButton.setVisibility(View.VISIBLE);
        totalLabel.setVisibility(View.VISIBLE);
        totalLabel2.setVisibility(View.VISIBLE);
    }

    /* Function to show the checkout fragment */
    public void checkout() {

        this.cartIsEmpty();
        emptyCartLabel.setVisibility(View.INVISIBLE);

        /* Set variables to be pass on the fragment */
        Bundle bundle = new Bundle();
        bundle.putString("PACKAGE_NAME", PACKAGE_NAME);
        bundle.putDouble("TOTAL", total);

        /* Pass variables to the fragment */
        checkoutFragment.setArguments(bundle);

        /* Begin fragment transaction */
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

    /* Function to call when user decreases an item on a cart */
    @Override
    public void onItemDecreased(DocumentSnapshot document, int position) {
        Log.d(TAG, "onItemDecreased - cartactivity");
        Log.d(TAG, position + " " + document.getId() + " " + document.get("quantity"));

        /* Compute for the new quantity and new subtotal */
        int newQuantity = Integer.parseInt(String.valueOf(document.get("quantity"))) - 1;
        double newSubtotal = Double.parseDouble(String.valueOf(document.get("price"))) * newQuantity;

        /* Check if new quantity is 0: delete item from the cart */
        if(newQuantity == 0) {
            db.collection("carts").document(document.getId()).delete()
            .addOnSuccessListener(new OnSuccessListener< Void >() {
                @Override
                public void onSuccess(Void aVoid) {
                    //Toast.makeText(view.getContext(), cartClass.getName() + " Added to cart", Toast.LENGTH_SHORT).show();
                    total = 0.0;
                    retrieveCart();
                }
            });
        } else {
            /* Update the cart. Decrease item count and update new subtotal */
            db.collection("carts").document(document.getId()).update("quantity", newQuantity, "subtotal", newSubtotal)
                    .addOnSuccessListener(new OnSuccessListener< Void >() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //Toast.makeText(view.getContext(), cartClass.getName() + " Added to cart", Toast.LENGTH_SHORT).show();
                            total = 0.0;
                            retrieveCart();
                        }
                    });
        }

    }

    /* Function to call when user increases an item on a cart */
    @Override
    public void onItemIncreased(DocumentSnapshot document, int position) {
        Log.d(TAG, "onItemIncreased - cartactivity");

        /* Compute for the new quantity and new subtotal */
        int newQuantity = Integer.parseInt(String.valueOf(document.get("quantity"))) + 1;
        double newSubtotal = Double.parseDouble(String.valueOf(document.get("price"))) * newQuantity;

        /* Update the cart. Decrease item count and update new subtotal */
        db.collection("carts").document(document.getId()).update("quantity", newQuantity, "subtotal", newSubtotal)
                .addOnSuccessListener(new OnSuccessListener< Void >() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Toast.makeText(view.getContext(), cartClass.getName() + " Added to cart", Toast.LENGTH_SHORT).show();
                        total = 0.0;
                        retrieveCart();
                    }
                });;
    }


    /*  function for rounding off to two decimal places
     *   https://stackoverflow.com/questions/2808535/round-a-double-to-2-decimal-places  */
    public static double round(double value) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /* Function to set the functionality of the bottom navigation */
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
