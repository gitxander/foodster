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

public class ItemAdapter extends FirestorePagingAdapter<ItemClass, ItemAdapter.ItemViewHolder> {

    /* Declare variables */
    private Context context;
    private String packageName;
    private OnListItemClicked onListItemClicked;

    /* Construct the adapter */
    public ItemAdapter(@NonNull FirestorePagingOptions<ItemClass> options, Context context, String packageName, OnListItemClicked onListItemClicked) {
        super(options);
        this.context = context;
        this.packageName = packageName;
        this.onListItemClicked = onListItemClicked;
    }

    /* Inflate view holder */
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new ItemViewHolder(view);
    }

    /* Set the labels and images base from the retrieved data collection */
    @Override
    protected void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull ItemClass model) {
        holder.nameLabel.setText(model.getName());
        holder.descriptionLabel.setText(model.getDescription());
        holder.priceLabel.setText("$"+ model.getPrice());

        int drawable = this.context.getResources().getIdentifier(model.getImage()   ,"drawable", this.packageName);
        holder.imageView.setImageResource(drawable);
    }

    /* Implement the view holder */
    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        /* Declare variables for labels, image, and button */
        private TextView nameLabel;
        private TextView descriptionLabel;
        private TextView priceLabel;
        private ImageView imageView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            /* set the labels, image, and button from the view resource */
            nameLabel = itemView.findViewById(R.id.nameLabel);
            descriptionLabel = itemView.findViewById(R.id.descriptionLabel);
            priceLabel = itemView.findViewById(R.id.priceLabel);
            imageView = itemView.findViewById(R.id.imageView);

            /* implement click lister */
            itemView.setOnClickListener(this);
        }

        /* If a button is clicked, what to do? Declare them here */
        @Override
        public void onClick(View v) {
            onListItemClicked.onItemClick(getItem(getAdapterPosition()), getAdapterPosition());
        }
    }

    /* Interface to implement for the classes implementing this adapter */
    public interface OnListItemClicked {
        void onItemClick(DocumentSnapshot snapshot, int position);
    }
}
