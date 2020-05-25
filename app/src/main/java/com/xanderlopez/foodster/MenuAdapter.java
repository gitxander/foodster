package com.xanderlopez.foodster;

import android.content.Context;
import android.util.Log;
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

public class MenuAdapter extends FirestorePagingAdapter<Menu, MenuAdapter.MenuViewHolder> {

    private Context context;
    private String packageName;
   // private OnListItemClicked onListItemClicked;
//
//    public MenuAdapter(@NonNull FirestorePagingOptions<Menu> options, Context context, String packageName, OnListItemClicked onListItemClicked) {
//        super(options);
//        this.context = context;
//        this.packageName = packageName;
//        this.onListItemClicked = onListItemClicked;
//    }

    public MenuAdapter(@NonNull FirestorePagingOptions<Menu> options, Context context, String packageName) {
        super(options);
        this.context = context;
        this.packageName = packageName;
    }

    @NonNull
    @Override
    public MenuAdapter.MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("MESSAGE", String.valueOf(parent.getContext()));
        View view = LayoutInflater.from(context).inflate(R.layout.row, parent, false);
        Log.d("MESSAGE", String.valueOf(view));
        return new MenuAdapter.MenuViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull MenuViewHolder holder, int position, @NonNull Menu model) {
        holder.nameLabel.setText(model.getName());
        holder.descriptionLabel.setText(model.getDescription());
//
//        int drawable = this.context.getResources().getIdentifier(model.getImage()   ,"drawable", this.packageName);
//        holder.imageView.setImageResource(drawable);
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {

        private TextView nameLabel;
        private TextView descriptionLabel;
        //private ImageView imageView;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);

            nameLabel = itemView.findViewById(R.id.nameLabel);
            descriptionLabel = itemView.findViewById(R.id.descriptionLabel);
            //imageView = itemView.findViewById(R.id.imageView);

            //itemView.setOnClickListener(this);
        }

//        @Override
//        public void onClick(View v) {
//            onListItemClicked.onItemClick(getItem(getAdapterPosition()), getAdapterPosition());
//        }
    }
//
//    public interface OnListItemClicked {
//        void onItemClick(DocumentSnapshot snapshot, int position);
//    }
}
