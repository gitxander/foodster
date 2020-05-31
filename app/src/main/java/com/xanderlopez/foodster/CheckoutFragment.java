package com.xanderlopez.foodster;

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

public class CheckoutFragment extends Fragment {

    String PACKAGE_NAME;
    Double total;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    private static final String TAG = "Message";
    Fragment checkoutSuccessFragment;


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
        checkoutSuccessFragment = new CheckoutFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* get the root view fragment layout */
        View rootView = inflater.inflate(R.layout.fragment_checkout, container, false);

        Button checkoutButton = rootView.findViewById(R.id.confirmButton);
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkout(v);
            }
        });

        TextView totalLabel = rootView.findViewById(R.id.totalLabel);
        totalLabel.setText("$" + total);

        return rootView;
    }

    public void checkout(View view) {
        TextView cardNameInput = getView().findViewById(R.id.cardName);
        TextView cardNumberInput = getView().findViewById(R.id.cardNumber);
        TextView cvvInput = getView().findViewById(R.id.cvv);
        TextView expiryInput = getView().findViewById(R.id.expiry);

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

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
        }
    }


}
