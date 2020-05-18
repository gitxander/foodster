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
import com.google.firebase.firestore.DocumentSnapshot;

import org.w3c.dom.Document;

public class HomeAdapter extends FirestorePagingAdapter<Restaurant, HomeAdapter.RestaurantViewHolder> {

    private Context context;
    private String packageName;
    private OnListItemClicked onListItemClicked;

    public HomeAdapter(@NonNull FirestorePagingOptions<Restaurant> options, Context context, String packageName, OnListItemClicked onListItemClicked) {
        super(options);
        this.context = context;
        this.packageName = packageName;
        this.onListItemClicked = onListItemClicked;
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

    public class RestaurantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView nameLabel;
        private TextView descriptionLabel;
        private ImageView imageView;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);

            nameLabel = itemView.findViewById(R.id.nameLabel);
            descriptionLabel = itemView.findViewById(R.id.descriptionLabel);
            imageView = itemView.findViewById(R.id.imageView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onListItemClicked.onItemClick(getItem(getAdapterPosition()), getAdapterPosition());
        }
    }

    public interface OnListItemClicked {
        void onItemClick(DocumentSnapshot snapshot, int position);
    }
}
