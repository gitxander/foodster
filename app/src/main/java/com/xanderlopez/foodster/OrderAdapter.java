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

public class OrderAdapter extends FirestorePagingAdapter<CartClass, OrderAdapter.OrderViewHolder> {

    private Context context;
    private String packageName;
    private OrderAdapter.OnListItemClicked onListItemClicked;

    public OrderAdapter(@NonNull FirestorePagingOptions<CartClass> options, Context context, String packageName, OrderAdapter.OnListItemClicked onListItemClicked) {
        super(options);
        this.context = context;
        this.packageName = packageName;
        this.onListItemClicked = onListItemClicked;
    }

    @NonNull
    @Override
    public OrderAdapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_order, parent, false);
        return new OrderAdapter.OrderViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull OrderAdapter.OrderViewHolder holder, int position, @NonNull CartClass model) {
        holder.nameLabel.setText(model.getName());
        holder.descriptionLabel.setText(model.getDescription());
        holder.orderLabel.setText(""+model.getOrderID());
        holder.priceLabel.setText("$"+model.getPrice());
        holder.quantityLabel.setText(""+model.getQuantity());
        holder.subtotalLabel.setText("$"+model.getSubtotal());

        int drawable = this.context.getResources().getIdentifier(model.getImage()   ,"drawable", this.packageName);
        holder.imageView.setImageResource(drawable);
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView nameLabel, descriptionLabel, priceLabel, orderLabel, quantityLabel, subtotalLabel;
        private ImageView imageView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            nameLabel = itemView.findViewById(R.id.nameLabel);
            descriptionLabel = itemView.findViewById(R.id.descriptionLabel);
            orderLabel = itemView.findViewById(R.id.orderLabel);
            priceLabel = itemView.findViewById(R.id.priceLabel);
            quantityLabel = itemView.findViewById(R.id.quantityLabel);
            subtotalLabel = itemView.findViewById(R.id.subtotalLabel);
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