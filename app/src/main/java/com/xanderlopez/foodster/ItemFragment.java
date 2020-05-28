package com.xanderlopez.foodster;

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

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

    String[] newsCategories = {"National","National"};
    Integer[] newsCategoriesImages = {R.drawable.berry_bread_breakfast};
    private static final String TAG = "Message";

    List<Restaurant> propertyList;
    FirebaseFirestore db;

    String PACKAGE_NAME, name;

     TextView nameLabel;
     TextView descriptionLabel;
     ImageView imageView;

     ItemClass itemClass;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        /* Instantiate propertyList */
        propertyList = new ArrayList<>();

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

                                nameLabel.setText(itemClass.getName());
                                descriptionLabel.setText(itemClass.getDescription());

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

    public void addToCart(View view) {
        Log.w("MESSAGE", "add to cartssss");

        CartClass cartClass = new CartClass();
        cartClass.setName(itemClass.getName());
        cartClass.setDescription(itemClass.getDescription());
        cartClass.setId(1);
        cartClass.setQuantity(1);
        cartClass.setImage(itemClass.getImage());

        // Create a new user with a first and last name
//        Map<String, Object> obj = new HashMap<>();
//        obj.put("description", "Spaghetti | Pasta | Cheese");
//        obj.put("image", "berry_bread_breakfast");
//        obj.put("name", "Cheese Pasta");
//        obj.put("price", "10.50");

        //Add a new document with a generated ID
        db.collection("carts")
                .add(cartClass)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }


}
