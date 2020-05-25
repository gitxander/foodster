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

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class HomeRestaurantFragment extends Fragment {

    String[] newsCategories = {"National","National"};
    Integer[] newsCategoriesImages = {R.drawable.berry_bread_breakfast};

    List<Restaurant> propertyList;
    FirebaseFirestore db;

    String PACKAGE_NAME;

    public HomeRestaurantFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            PACKAGE_NAME = getArguments().getString("PACKAGE_NAME");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        /* Instantiate propertyList */
        propertyList = new ArrayList<>();

        Log.d("MESSAGE", "Home");

        Log.d("MESSAGE", PACKAGE_NAME);

        /* get the root view fragment layout */
        View rootView = inflater.inflate(R.layout.fragment_home_restaurant, container, false);

//        //get a reference to recyclerView
//        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.menuRecyclerView);
//        //recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
//        //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        //recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
//        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext(), LinearLayoutManager.VERTICAL, false));
//
//        /* Iterate newsCategories object */
//        for (int counter = 0; counter < newsCategories.length; counter++) {
//
//            /* Initialise new property object */
//            Restaurant property = new Restaurant("name","desc","berry_bread_breakfast");
//
//            /* Add object to propertyList */
//            propertyList.add(property);
//        }
//
//        /* Set property adapter for the recyclerview */
//        PropertyAdapter propertyAdapter = new PropertyAdapter(propertyList, container.getContext());
//        recyclerView.setAdapter(propertyAdapter);

//
//
//        db = FirebaseFirestore.getInstance();
//        Query query = db.collection("menu");
//        PagedList.Config config = new PagedList.Config.Builder()
//                .setInitialLoadSizeHint(10)
//                .setPageSize(3)
//                .build();
//
//        FirestorePagingOptions<Menu> options = new FirestorePagingOptions.Builder<Menu>()
//                .setLifecycleOwner(this)
//                .setQuery(query, config, Menu.class)
//                .build();
//
//        MenuAdapter adapter = new MenuAdapter(options, getActivity(), PACKAGE_NAME);
//
//        db.collection("menu")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d("MESSAGE", document.getId() + " => " + document.getData());
//                            }
//                        } else {
//                            Log.w("MESSAGE", "Error getting documents.", task.getException());
//                        }
//                    }
//                });
//
//        recyclerView.setAdapter(adapter);
//        recyclerView.setVisibility(View.VISIBLE);

        /* return rootView */
        return rootView;
    }

    public void addToCart(View view) {
        Log.w("MESSAGE", "add to cart");
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        adapter.startListening();
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        adapter.stopListening();
//    }

}
