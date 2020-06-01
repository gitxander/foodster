package com.xanderlopez.foodster;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ItemFragment extends Fragment {

    private static final String TAG = "Message";

    FirebaseFirestore db;

    String PACKAGE_NAME, name;

     TextView nameLabel;
     TextView descriptionLabel;
    TextView priceLabel;
     ImageView imageView;

     ItemClass itemClass;

    private FirebaseAuth mAuth;

    public ItemFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            PACKAGE_NAME = getArguments().getString("PACKAGE_NAME");
            name = getArguments().getString("name");
        }

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        itemClass = new ItemClass();

        /* get the root view fragment layout */
        View rootView = inflater.inflate(R.layout.fragment_item, container, false);

        Button addToCart = (Button) rootView.findViewById(R.id.addToCartButton);
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart(v);
            }
        });

        nameLabel = rootView.findViewById(R.id.nameLabel);
        descriptionLabel = rootView.findViewById(R.id.descriptionLabel);
        priceLabel = rootView.findViewById(R.id.priceLabel);
        imageView = rootView.findViewById(R.id.imageView);

        db = FirebaseFirestore.getInstance();
        db.collection("items").whereEqualTo("name",name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                itemClass.setName(String.valueOf(document.get("name")));
                                itemClass.setDescription(String.valueOf(document.get("description")));
                                itemClass.setImage(String.valueOf(document.get("image")));
                                itemClass.setPrice(Double.parseDouble(String.valueOf(document.get("price"))));

                                nameLabel.setText(itemClass.getName());
                                descriptionLabel.setText(itemClass.getDescription());
                                priceLabel.setText("$" + itemClass.getPrice());

                                int drawable = container.getContext().getResources().getIdentifier(itemClass.getImage(),"drawable", PACKAGE_NAME);
                                imageView.setImageResource(drawable);

                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        return rootView;
    }

    public void addToCart(final View view) {
        Log.w("MESSAGE", "add to cartssss");

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Intent profileIntent = new Intent(view.getContext(), ProfileActivity.class);
            startActivityForResult(profileIntent, 0);
            return;
        }

        final CartClass cartClass = new CartClass();
        cartClass.setName(itemClass.getName());
        cartClass.setDescription(itemClass.getDescription());
        cartClass.setPrice(itemClass.getPrice());
        cartClass.setUserID(user.getUid());
        cartClass.setQuantity(1);
        cartClass.setSubtotal(itemClass.getPrice());
        cartClass.setImage(itemClass.getImage());
        cartClass.setOrdered(false);

        db.collection("carts").whereEqualTo("name",itemClass.getName()).whereEqualTo("userID", user.getUid()).whereEqualTo("ordered",false)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    if(task.getResult().isEmpty()) {
                        // add
                        Log.w(TAG, "ADD ITEM");

                        // Add a new document with a generated ID
                        db.collection("carts")
                                .add(cartClass)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                        db.collection("carts").document(documentReference.getId()).update("documentID", documentReference.getId());
                                        Toast.makeText(view.getContext(), cartClass.getName() + " added to cart", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error adding document", e);
                                    }
                                });
                    } else {
                        // update qty & subtotal
                        Log.w(TAG, "UPDATE ITEM QTY");

                        Toast.makeText(view.getContext(), cartClass.getName() + " Adding to cart ... ", Toast.LENGTH_SHORT).show();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());

                            int newQuantity = Integer.parseInt(String.valueOf(document.get("quantity"))) + 1;
                            double newSubtotal = Double.parseDouble(String.valueOf(document.get("price"))) * newQuantity;

                            db.collection("carts").document(String.valueOf(document.get("documentID"))).update("quantity", newQuantity, "subtotal", newSubtotal)
                                    .addOnSuccessListener(new OnSuccessListener < Void > () {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(view.getContext(), cartClass.getName() + " Added to cart", Toast.LENGTH_SHORT).show();
                                        }
                            });;

                        }


                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
                }
            });

    }


}
