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

public class CartAdapter extends FirestorePagingAdapter<CartClass, CartAdapter.CartViewHolder> {

    private Context context;
    private String packageName;
    private OnListItemClicked onListItemClicked;

    public CartAdapter(@NonNull FirestorePagingOptions<CartClass> options, Context context, String packageName, OnListItemClicked onListItemClicked) {
        super(options);
        this.context = context;
        this.packageName = packageName;
        this.onListItemClicked = onListItemClicked;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cart, parent, false);
        return new CartViewHolder(view);
    }

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

    public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView nameLabel;
        private TextView descriptionLabel;
        private TextView priceLabel;
        private TextView quantityLabel;
        private TextView subtotalLabel;
        private ImageView imageView;

        public CartViewHolder(@NonNull View view) {
            super(view);

            nameLabel = view.findViewById(R.id.nameLabel);
            descriptionLabel = view.findViewById(R.id.descriptionLabel);
            priceLabel = view.findViewById(R.id.priceLabel);
            imageView = view.findViewById(R.id.imageView);
            quantityLabel = view.findViewById(R.id.quantityLabel);
            subtotalLabel = view.findViewById(R.id.subtotalLabel);

            view.setOnClickListener(this);
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
