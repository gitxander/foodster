package com.xanderlopez.foodster;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;

public class CheckoutFragment extends Fragment {

    String PACKAGE_NAME;
    Double total;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    private static final String TAG = "Message";
    TextView cardNameInput, cardNumberInput, cvvInput, expiryInput, checkoutTextView, checkoutSuccessLabel;
    Button checkoutButton, goBackButton;

    public CheckoutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            PACKAGE_NAME = getArguments().getString("PACKAGE_NAME");
            total = getArguments().getDouble("TOTAL");
        }

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* get the root view fragment layout */
        View rootView = inflater.inflate(R.layout.fragment_checkout, container, false);

        checkoutSuccessLabel = rootView.findViewById(R.id.checkoutSuccessLabel);
        checkoutSuccessLabel.setVisibility(View.INVISIBLE);

        checkoutButton = rootView.findViewById(R.id.confirmButton);
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkout(v);
            }
        });

        goBackButton = rootView.findViewById(R.id.goBackButton);
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack(v);
            }
        });
        goBackButton.setVisibility(View.INVISIBLE);

        TextView totalLabel = rootView.findViewById(R.id.totalLabel);
        totalLabel.setText("$"+ NumberFormat.getInstance().format(round(total)));

        return rootView;
    }

    public void checkout(View view) {
        cardNameInput = getView().findViewById(R.id.cardName);
        cardNumberInput = getView().findViewById(R.id.cardNumber);
        cvvInput = getView().findViewById(R.id.cvv);
        expiryInput = getView().findViewById(R.id.expiry);
        checkoutTextView = getView().findViewById(R.id.checkoutTextView);

        String cardName = cardNameInput.getText().toString();
        String cardNumber = cardNumberInput.getText().toString();
        String cvv  = cvvInput.getText().toString();
        String expiry  = expiryInput.getText().toString();

        if(TextUtils.isEmpty(cardName) || TextUtils.isEmpty(cardNumber) || TextUtils.isEmpty(cvv) || TextUtils.isEmpty(expiry)) {
            Toast.makeText(view.getContext(), "Please answer all fields", Toast.LENGTH_SHORT).show();
        } else {

            FirebaseUser user = mAuth.getCurrentUser();

            db.collection("carts").whereEqualTo("ordered",false).whereEqualTo("userID", user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            // update qty & subtotal
                            Log.w(TAG, "ORDER");

                            int random_int = (int)(Math.random() * 100000000);
                            double total = 0.0;

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                int newQuantity = Integer.parseInt(String.valueOf(document.get("quantity"))) + 1;
                                double newSubtotal = Double.parseDouble(String.valueOf(document.get("price"))) * newQuantity;
                                total += newSubtotal;

                                db.collection("carts").document(String.valueOf(document.get("documentID"))).update("ordered", true, "orderID", random_int)
                                    .addOnSuccessListener(new OnSuccessListener < Void > () {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //Toast.makeText(view.getContext(), cartClass.getName() + " Added to cart", Toast.LENGTH_SHORT).show();
                                        }
                                    });;

                            }

                            checkoutSuccessFragment();


                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
        }
    }

    public void goBack(View view) {
        Intent homeIntent = new Intent(this.getContext(), MainActivity.class);
        startActivityForResult(homeIntent, 0);
    }

    public void checkoutSuccessFragment() {

        cardNameInput.setVisibility(View.INVISIBLE);
        cardNumberInput.setVisibility(View.INVISIBLE);
        cvvInput.setVisibility(View.INVISIBLE);
        expiryInput.setVisibility(View.INVISIBLE);
        checkoutButton.setVisibility(View.INVISIBLE);
        checkoutSuccessLabel.setVisibility(View.VISIBLE);
        goBackButton.setVisibility(View.VISIBLE);

    }

    /*  function for rounding off to two decimal places
     *   https://stackoverflow.com/questions/2808535/round-a-double-to-2-decimal-places  */
    public static double round(double value) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


}
