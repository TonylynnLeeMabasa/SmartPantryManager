package com.example.smartpantrymanager;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LowStockItemAdapter
        extends RecyclerView.Adapter<LowStockItemAdapter.LowStockItemViewHolder> {

    private final List<PantryItem> lowStockItems;

    public LowStockItemAdapter(List<PantryItem> lowStockItems) {
        this.lowStockItems = lowStockItems;
    }

    @NonNull
    @Override
    public LowStockItemViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_low_stock, parent, false);

        return new LowStockItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull LowStockItemViewHolder holder,
            int position
    ) {
        PantryItem item = lowStockItems.get(position);

        holder.itemNameText.setText(item.getName());
        holder.categoryText.setText(item.getCategory());

        holder.quantityText.setText(
                "Quantity: "
                        + item.getQuantity()
                        + " • Minimum: "
                        + item.getLowStockLevel()
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
        return lowStockItems.size();
    }

    public static class LowStockItemViewHolder
            extends RecyclerView.ViewHolder {

        TextView itemNameText;
        TextView categoryText;
        TextView quantityText;

        public LowStockItemViewHolder(@NonNull View itemView) {
            super(itemView);

            itemNameText =
                    itemView.findViewById(R.id.lowStockItemName);

            categoryText =
                    itemView.findViewById(R.id.lowStockItemCategory);

            quantityText =
                    itemView.findViewById(R.id.lowStockItemQuantity);
        }
    }
}