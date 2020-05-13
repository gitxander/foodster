package com.xanderlopez.foodster;


import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/* Adapters provide a binding from an app-specific data set to views that are displayed within a RecyclerView.
 * An adapter is responsible for providing views that represent items in a data set
 * https://developer.android.com/reference/android/support/v7/widget/RecyclerView.Adapter */

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder> {

    private List<Restaurant> propertyList;
    private OnItemClickedListener onItemClickedListener;

    private Context context;

    /* Property adapter constructor */
    public PropertyAdapter(List<Restaurant> propertyList, Context context, OnItemClickedListener onItemClickedListener) {
        this.propertyList = propertyList;
        this.context = context;
        this.onItemClickedListener = onItemClickedListener;
    }

    /* References views to a data item */
    public class PropertyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name;
        OnItemClickedListener onItemClicked;

        /* Constructor which accepts view and click listener for each item  */
        public  PropertyViewHolder (View view, OnItemClickedListener onItemClicked) {
            super(view);

            /* set this.name depending on the nameLabel */
            name = (TextView)view.findViewById(R.id.nameLabel);

            /* set click listener to each item */
            this.onItemClicked = onItemClicked;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClicked.onItemClicked(getAdapterPosition());
        }
    }

    /* Create an interface to be declared by another class implementing this class */
    public interface OnItemClickedListener {
        void onItemClicked(int position);
    }

    /* Inflates layout (row.xml) and return property holder */
    @NonNull
    @Override
    public PropertyAdapter.PropertyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.row, viewGroup, false);
        return new PropertyAdapter.PropertyViewHolder(itemView, onItemClickedListener);
    }

    /* Populate data into the recycler view list from property list */
    @Override
    public void onBindViewHolder(@NonNull PropertyAdapter.PropertyViewHolder propertyViewHolder, int i) {
        propertyViewHolder.name.setText(propertyList.get(i).getName());
    }

    @Override
    public int getItemCount() {
        return propertyList.size();
    }
}