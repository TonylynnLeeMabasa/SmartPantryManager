package com.example.smartpantrymanager;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExpiringItemAdapter
        extends RecyclerView.Adapter<ExpiringItemAdapter.ExpiringItemViewHolder> {

    private final List<PantryItem> expiringItems;

    public ExpiringItemAdapter(List<PantryItem> expiringItems) {
        this.expiringItems = expiringItems;
    }

    @NonNull
    @Override
    public ExpiringItemViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expiring, parent, false);

        return new ExpiringItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ExpiringItemViewHolder holder,
            int position
    ) {
        PantryItem item = expiringItems.get(position);

        holder.itemNameText.setText(item.getName());
        holder.categoryText.setText(item.getCategory());

        holder.expiryText.setText(
                "Expires: " + item.getExpiryDate()
        );

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(
                    v.getContext(),
                    EditItemActivity.class
            );

            intent.putExtra("itemId", item.getId());

            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return expiringItems.size();
    }

    public static class ExpiringItemViewHolder
            extends RecyclerView.ViewHolder {

        TextView itemNameText;
        TextView categoryText;
        TextView expiryText;

        public ExpiringItemViewHolder(@NonNull View itemView) {
            super(itemView);

            itemNameText =
                    itemView.findViewById(R.id.expiringItemName);

            categoryText =
                    itemView.findViewById(R.id.expiringItemCategory);

            expiryText =
                    itemView.findViewById(R.id.expiringItemExpiry);
        }
    }
}