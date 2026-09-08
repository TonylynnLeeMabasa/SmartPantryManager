package com.example.smartpantrymanager;

import android.app.AlertDialog;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ShoppingItemAdapter
        extends RecyclerView.Adapter<ShoppingItemAdapter.ShoppingItemViewHolder> {

    private final List<ShoppingItem> shoppingItems;

    private final DatabaseReference databaseReference;

    public ShoppingItemAdapter(List<ShoppingItem> shoppingItems) {

        this.shoppingItems = shoppingItems;

        databaseReference = FirebaseDatabase
                .getInstance(
                        "https://smart-pantry-manager-7e502-default-rtdb.europe-west1.firebasedatabase.app/"
                )
                .getReference()
                .child("shopping_items");
    }

    @NonNull
    @Override
    public ShoppingItemViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.item_shopping,
                        parent,
                        false
                );

        return new ShoppingItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ShoppingItemViewHolder holder,
            int position
    ) {

        ShoppingItem item = shoppingItems.get(position);

        holder.itemNameText.setText(
                item.getName()
        );

        holder.quantityText.setText(
                "Quantity: " + item.getQuantity()
        );

        // Remove any previous listener before setting
        // the checkbox state.
        holder.purchasedCheckBox.setOnCheckedChangeListener(
                null
        );

        holder.purchasedCheckBox.setChecked(
                item.isPurchased()
        );

        updatePurchasedAppearance(
                holder.itemNameText,
                holder.quantityText,
                item.isPurchased()
        );

        // Mark as purchased / not purchased
        holder.purchasedCheckBox.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    item.setPurchased(isChecked);

                    updatePurchasedAppearance(
                            holder.itemNameText,
                            holder.quantityText,
                            isChecked
                    );

                    databaseReference
                            .child(item.getId())
                            .child("purchased")
                            .setValue(isChecked)
                            .addOnFailureListener(error -> {

                                Toast.makeText(
                                        buttonView.getContext(),
                                        "Could not update item.",
                                        Toast.LENGTH_SHORT
                                ).show();
                            });
                }
        );

        // Delete item
        holder.deleteButton.setOnClickListener(v -> {

            new AlertDialog.Builder(v.getContext())
                    .setTitle("Delete shopping item")
                    .setMessage(
                            "Are you sure you want to delete \""
                                    + item.getName()
                                    + "\"?"
                    )
                    .setNegativeButton(
                            "Cancel",
                            null
                    )
                    .setPositiveButton(
                            "Delete",
                            (dialog, which) -> {

                                databaseReference
                                        .child(item.getId())
                                        .removeValue()
                                        .addOnSuccessListener(unused -> {

                                            Toast.makeText(
                                                    v.getContext(),
                                                    "Item deleted.",
                                                    Toast.LENGTH_SHORT
                                            ).show();
                                        })
                                        .addOnFailureListener(error -> {

                                            Toast.makeText(
                                                    v.getContext(),
                                                    "Could not delete item.",
                                                    Toast.LENGTH_SHORT
                                            ).show();
                                        });
                            }
                    )
                    .show();
        });
    }

    private void updatePurchasedAppearance(
            TextView itemNameText,
            TextView quantityText,
            boolean purchased
    ) {

        if (purchased) {

            itemNameText.setPaintFlags(
                    itemNameText.getPaintFlags()
                            | Paint.STRIKE_THRU_TEXT_FLAG
            );

            quantityText.setPaintFlags(
                    quantityText.getPaintFlags()
                            | Paint.STRIKE_THRU_TEXT_FLAG
            );

        } else {

            itemNameText.setPaintFlags(
                    itemNameText.getPaintFlags()
                            & ~Paint.STRIKE_THRU_TEXT_FLAG
            );

            quantityText.setPaintFlags(
                    quantityText.getPaintFlags()
                            & ~Paint.STRIKE_THRU_TEXT_FLAG
            );
        }
    }

    @Override
    public int getItemCount() {

        return shoppingItems.size();
    }

    public static class ShoppingItemViewHolder
            extends RecyclerView.ViewHolder {

        CheckBox purchasedCheckBox;

        TextView itemNameText;

        TextView quantityText;

        TextView deleteButton;

        public ShoppingItemViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);

            purchasedCheckBox =
                    itemView.findViewById(
                            R.id.purchasedCheckBox
                    );

            itemNameText =
                    itemView.findViewById(
                            R.id.shoppingItemName
                    );

            quantityText =
                    itemView.findViewById(
                            R.id.shoppingItemQuantity
                    );

            deleteButton =
                    itemView.findViewById(
                            R.id.deleteShoppingItemButton
                    );
        }
    }
}