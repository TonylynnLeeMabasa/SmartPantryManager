package com.example.smartpantrymanager;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PantryItemAdapter
        extends RecyclerView.Adapter<PantryItemAdapter.PantryItemViewHolder> {

    private final List<PantryItem> pantryItems;

    public PantryItemAdapter(List<PantryItem> pantryItems) {
        this.pantryItems = pantryItems;
    }

    @NonNull
    @Override
    public PantryItemViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);

        return new PantryItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull PantryItemViewHolder holder,
            int position
    ) {
        PantryItem item = pantryItems.get(position);

        holder.itemNameText.setText(item.getName());
        holder.categoryText.setText(item.getCategory());
        holder.quantityText.setText(
                String.valueOf(item.getQuantity())
        );
        holder.locationText.setText(item.getLocation());

        holder.expiryDateText.setText(
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
        return pantryItems.size();
    }

    public static class PantryItemViewHolder
            extends RecyclerView.ViewHolder {

        TextView itemNameText;
        TextView categoryText;
        TextView quantityText;
        TextView locationText;
        TextView expiryDateText;

        public PantryItemViewHolder(@NonNull View itemView) {
            super(itemView);

            itemNameText = itemView.findViewById(R.id.itemNameText);
            categoryText = itemView.findViewById(R.id.categoryText);
            quantityText = itemView.findViewById(R.id.quantityText);
            locationText = itemView.findViewById(R.id.locationText);
            expiryDateText = itemView.findViewById(R.id.expiryDateText);
        }
    }
}