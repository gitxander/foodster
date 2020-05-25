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

    private Context context;
    private String packageName;
    private OnListItemClicked onListItemClicked;

    public ItemAdapter(@NonNull FirestorePagingOptions<ItemClass> options, Context context, String packageName, OnListItemClicked onListItemClicked) {
        super(options);
        this.context = context;
        this.packageName = packageName;
        this.onListItemClicked = onListItemClicked;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull ItemClass model) {
        holder.nameLabel.setText(model.getName());
        holder.descriptionLabel.setText(model.getDescription());

        int drawable = this.context.getResources().getIdentifier(model.getImage()   ,"drawable", this.packageName);
        holder.imageView.setImageResource(drawable);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView nameLabel;
        private TextView descriptionLabel;
        private ImageView imageView;

        public ItemViewHolder(@NonNull View itemView) {
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
