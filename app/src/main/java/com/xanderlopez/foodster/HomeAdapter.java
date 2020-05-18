package com.xanderlopez.foodster;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;

public class HomeAdapter extends FirestorePagingAdapter<Restaurant, HomeAdapter.RestaurantViewHolder> {

    private Context context;
    private String packageName;

    public HomeAdapter(@NonNull FirestorePagingOptions<Restaurant> options, Context context, String packageName) {
        super(options);
        this.context = context;
        this.packageName = packageName;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position, @NonNull Restaurant model) {
        holder.nameLabel.setText(model.getName());
        holder.descriptionLabel.setText(model.getDescription());

        int drawable = this.context.getResources().getIdentifier(model.getImage()   ,"drawable", this.packageName);
        holder.imageView.setImageResource(drawable);
    }

    public class RestaurantViewHolder extends RecyclerView.ViewHolder {

        private TextView nameLabel;
        private TextView descriptionLabel;
        private ImageView imageView;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);

            nameLabel = itemView.findViewById(R.id.nameLabel);
            descriptionLabel = itemView.findViewById(R.id.descriptionLabel);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
