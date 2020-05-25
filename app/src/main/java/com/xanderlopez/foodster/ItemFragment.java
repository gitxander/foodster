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
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class ItemFragment extends Fragment {

    String[] newsCategories = {"National","National"};
    Integer[] newsCategoriesImages = {R.drawable.berry_bread_breakfast};
    private static final String TAG = "Message";

    List<Restaurant> propertyList;
    FirebaseFirestore db;

    String PACKAGE_NAME, name;

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

        /* get the root view fragment layout */
        View rootView = inflater.inflate(R.layout.fragment_item, container, false);

        final TextView nameLabel = rootView.findViewById(R.id.nameLabel);
        final TextView descriptionLabel = rootView.findViewById(R.id.descriptionLabel);
        final ImageView imageView = rootView.findViewById(R.id.imageView);

        db = FirebaseFirestore.getInstance();
        db.collection("items").whereEqualTo("name",name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());


                                nameLabel.setText(String.valueOf(document.get("name")));
                                descriptionLabel.setText(String.valueOf(document.get("description")));

                                int drawable = container.getContext().getResources().getIdentifier(String.valueOf(document.get("image"))   ,"drawable", PACKAGE_NAME);
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
        Log.w("MESSAGE", "add to cart");
    }


}
