package com.xanderlopez.foodster;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class HomeRestaurantFragment extends Fragment implements HomeAdapter.OnListItemClicked {

    String[] newsCategories = {"National","National"};
    Integer[] newsCategoriesImages = {R.drawable.berry_bread_breakfast};

    List<Restaurant> propertyList;

    public HomeRestaurantFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /* Instantiate propertyList */
        propertyList = new ArrayList<>();

        Log.d("MESSAGE", "Home");

        /* get the root view fragment layout */
        View rootView = inflater.inflate(R.layout.fragment_home_restaurant, container, false);

        //get a reference to recyclerView
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.propertyRecyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        /* Iterate newsCategories object */
        for (int counter = 0; counter < newsCategories.length; counter++) {

            /* Initialise new property object */
            Restaurant property = new Restaurant("name","desc","berry_bread_breakfast");

            /* Add object to propertyList */
            propertyList.add(property);
        }

        /* Set property adapter for the recyclerview */
        PropertyAdapter propertyAdapter = new PropertyAdapter(propertyList, container.getContext());
        recyclerView.setAdapter(propertyAdapter);

        /* return rootView */
        return rootView;
    }

    @Override
    public void onItemClick(DocumentSnapshot snapshot, int position) {

    }
}
