package com.xanderlopez.foodster;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class CartAdapter extends FirestorePagingAdapter<CartClass, CartAdapter.CartViewHolder> {

    /* Declare variables */
    private Context context;
    private String packageName;
    private OnListItemClicked onListItemClicked;
    private static final String TAG = "Message";

    /* Construct the adapter */
    public CartAdapter(@NonNull FirestorePagingOptions<CartClass> options, Context context, String packageName, OnListItemClicked onListItemClicked) {
        super(options);
        this.context = context;
        this.packageName = packageName;
        this.onListItemClicked = onListItemClicked;
    }

    /* Inflate view holder */
    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cart, parent, false), onListItemClicked);
    }

    /* Set the labels and images base from the retrieved data collection */
    @Override
    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull CartClass model) {
        holder.nameLabel.setText(model.getName());
        holder.descriptionLabel.setText(model.getDescription());
        holder.priceLabel.setText("$"+ model.getPrice());
        holder.quantityLabel.setText(""+model.getQuantity());
        holder.subtotalLabel.setText(""+model.getSubtotal());

        int drawable = this.context.getResources().getIdentifier(model.getImage()   ,"drawable", this.packageName);
        holder.imageView.setImageResource(drawable);
    }

    /* Implement the view holder */
    public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        /* Declare variables for labels, image, and button */
        private TextView nameLabel;
        private TextView descriptionLabel;
        private TextView priceLabel;
        private TextView quantityLabel;
        private TextView subtotalLabel;
        private ImageView imageView;
        private Button decreaseButton, increaseButton;

        private OnListItemClicked onListItemClicked;


        public CartViewHolder(@NonNull View view, OnListItemClicked onListItemClicked) {
            super(view);

            /* set the labels, image, and button from the view resource */
            nameLabel = view.findViewById(R.id.nameLabel);
            descriptionLabel = view.findViewById(R.id.descriptionLabel);
            priceLabel = view.findViewById(R.id.priceLabel);
            imageView = view.findViewById(R.id.imageView);
            quantityLabel = view.findViewById(R.id.quantityLabel);
            subtotalLabel = view.findViewById(R.id.subtotalLabel);
            decreaseButton = view.findViewById(R.id.decreaseButton);
            increaseButton = view.findViewById(R.id.increaseButton);

            /* implement click lister */

            this.onListItemClicked = onListItemClicked;

            decreaseButton.setOnClickListener(this);
            increaseButton.setOnClickListener(this);
        }

        /* If a button is clicked, what to do? Declare them here */
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.decreaseButton:
                    onListItemClicked.onItemDecreased(getItem(getAdapterPosition()), getAdapterPosition());
                    break;
                case R.id.increaseButton:
                    onListItemClicked.onItemIncreased(getItem(getAdapterPosition()), getAdapterPosition());

            }
        }
    }

    /* Interface to implement for the classes implementing this adapter */
    public interface OnListItemClicked {
        void onItemDecreased(DocumentSnapshot snapshot, int position);
        void onItemIncreased(DocumentSnapshot snapshot, int position);
    }
}
