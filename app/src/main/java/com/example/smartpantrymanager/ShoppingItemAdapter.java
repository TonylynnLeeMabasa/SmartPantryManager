package com.example.smartpantrymanager;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Paint;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

    private final DatabaseReference shoppingReference;

    public ShoppingItemAdapter(
            List<ShoppingItem> shoppingItems
    ) {

        this.shoppingItems = shoppingItems;

        shoppingReference =
                FirebaseDatabase
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

        View view =
                LayoutInflater
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

        ShoppingItem item =
                shoppingItems.get(position);

        holder.shoppingItemName.setText(
                item.getName()
        );

        holder.shoppingItemQuantity.setText(
                "Quantity: "
                        + item.getQuantity()
        );

        holder.purchasedCheckBox.setOnCheckedChangeListener(
                null
        );

        holder.purchasedCheckBox.setChecked(
                item.isPurchased()
        );

        updatePurchasedAppearance(
                holder,
                item.isPurchased()
        );

        holder.purchasedCheckBox.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    item.setPurchased(
                            isChecked
                    );

                    updatePurchasedAppearance(
                            holder,
                            isChecked
                    );

                    if (item.getId() == null
                            || item.getId().isEmpty()) {

                        Toast.makeText(
                                buttonView.getContext(),
                                "Shopping item ID is missing.",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    shoppingReference
                            .child(item.getId())
                            .child("purchased")
                            .setValue(isChecked);
                }
        );

        holder.editShoppingItemButton.setOnClickListener(
                v -> showEditQuantityDialog(
                        v.getContext(),
                        item
                )
        );

        holder.deleteShoppingItemButton.setOnClickListener(
                v -> showDeleteConfirmation(
                        v.getContext(),
                        item
                )
        );
    }

    private void updatePurchasedAppearance(
            ShoppingItemViewHolder holder,
            boolean purchased
    ) {

        if (purchased) {

            holder.shoppingItemName.setPaintFlags(
                    holder.shoppingItemName
                            .getPaintFlags()
                            | Paint.STRIKE_THRU_TEXT_FLAG
            );

            holder.shoppingItemQuantity.setPaintFlags(
                    holder.shoppingItemQuantity
                            .getPaintFlags()
                            | Paint.STRIKE_THRU_TEXT_FLAG
            );

        } else {

            holder.shoppingItemName.setPaintFlags(
                    holder.shoppingItemName
                            .getPaintFlags()
                            & ~Paint.STRIKE_THRU_TEXT_FLAG
            );

            holder.shoppingItemQuantity.setPaintFlags(
                    holder.shoppingItemQuantity
                            .getPaintFlags()
                            & ~Paint.STRIKE_THRU_TEXT_FLAG
            );
        }
    }

    private void showEditQuantityDialog(
            Context context,
            ShoppingItem item
    ) {

        EditText quantityInput =
                new EditText(context);

        quantityInput.setInputType(
                InputType.TYPE_CLASS_NUMBER
        );

        quantityInput.setSingleLine(true);

        quantityInput.setText(
                String.valueOf(
                        item.getQuantity()
                )
        );

        quantityInput.setSelectAllOnFocus(
                true
        );

        int horizontalPadding =
                50;

        int verticalPadding =
                10;

        quantityInput.setPadding(
                horizontalPadding,
                verticalPadding,
                horizontalPadding,
                verticalPadding
        );

        AlertDialog dialog =
                new AlertDialog.Builder(context)
                        .setTitle(
                                "Edit Quantity"
                        )
                        .setMessage(
                                "Enter the new quantity for "
                                        + item.getName()
                        )
                        .setView(
                                quantityInput
                        )
                        .setNegativeButton(
                                "Cancel",
                                null
                        )
                        .setPositiveButton(
                                "Save",
                                null
                        )
                        .create();

        dialog.setOnShowListener(
                dialogInterface -> {

                    dialog.getButton(
                            AlertDialog.BUTTON_POSITIVE
                    ).setOnClickListener(
                            v -> {

                                String quantityText =
                                        quantityInput
                                                .getText()
                                                .toString()
                                                .trim();

                                if (quantityText.isEmpty()) {

                                    quantityInput.setError(
                                            "Enter a quantity."
                                    );

                                    return;
                                }

                                int newQuantity;

                                try {

                                    newQuantity =
                                            Integer.parseInt(
                                                    quantityText
                                            );

                                } catch (NumberFormatException e) {

                                    quantityInput.setError(
                                            "Enter a valid number."
                                    );

                                    return;
                                }

                                if (newQuantity <= 0) {

                                    quantityInput.setError(
                                            "Quantity must be greater than 0."
                                    );

                                    return;
                                }

                                if (item.getId() == null
                                        || item.getId().isEmpty()) {

                                    Toast.makeText(
                                            context,
                                            "Shopping item ID is missing.",
                                            Toast.LENGTH_LONG
                                    ).show();

                                    return;
                                }

                                shoppingReference
                                        .child(
                                                item.getId()
                                        )
                                        .child(
                                                "quantity"
                                        )
                                        .setValue(
                                                newQuantity
                                        )
                                        .addOnCompleteListener(
                                                task -> {

                                                    if (task.isSuccessful()) {

                                                        item.setQuantity(
                                                                newQuantity
                                                        );

                                                        int currentPosition =
                                                                shoppingItems
                                                                        .indexOf(
                                                                                item
                                                                        );

                                                        if (currentPosition
                                                                != -1) {

                                                            notifyItemChanged(
                                                                    currentPosition
                                                            );
                                                        }

                                                        Toast.makeText(
                                                                context,
                                                                "Quantity updated.",
                                                                Toast.LENGTH_SHORT
                                                        ).show();

                                                        dialog.dismiss();

                                                    } else {

                                                        Toast.makeText(
                                                                context,
                                                                "Could not update quantity.",
                                                                Toast.LENGTH_LONG
                                                        ).show();
                                                    }
                                                }
                                        );
                            }
                    );
                }
        );

        dialog.show();
    }

    private void showDeleteConfirmation(
            Context context,
            ShoppingItem item
    ) {

        new AlertDialog.Builder(context)
                .setTitle(
                        "Delete Shopping Item"
                )
                .setMessage(
                        "Are you sure you want to remove "
                                + item.getName()
                                + " from your shopping list?"
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> {

                            if (item.getId() == null
                                    || item.getId().isEmpty()) {

                                Toast.makeText(
                                        context,
                                        "Shopping item ID is missing.",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }

                            shoppingReference
                                    .child(
                                            item.getId()
                                    )
                                    .removeValue()
                                    .addOnCompleteListener(
                                            task -> {

                                                if (task.isSuccessful()) {

                                                    Toast.makeText(
                                                            context,
                                                            "Shopping item deleted.",
                                                            Toast.LENGTH_SHORT
                                                    ).show();

                                                } else {

                                                    Toast.makeText(
                                                            context,
                                                            "Could not delete shopping item.",
                                                            Toast.LENGTH_LONG
                                                    ).show();
                                                }
                                            }
                                    );
                        }
                )
                .show();
    }

    @Override
    public int getItemCount() {
        return shoppingItems.size();
    }

    public static class ShoppingItemViewHolder
            extends RecyclerView.ViewHolder {

        TextView shoppingItemName;
        TextView shoppingItemQuantity;
        TextView editShoppingItemButton;
        TextView deleteShoppingItemButton;

        android.widget.CheckBox purchasedCheckBox;

        public ShoppingItemViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);

            shoppingItemName =
                    itemView.findViewById(
                            R.id.shoppingItemName
                    );

            shoppingItemQuantity =
                    itemView.findViewById(
                            R.id.shoppingItemQuantity
                    );

            purchasedCheckBox =
                    itemView.findViewById(
                            R.id.purchasedCheckBox
                    );

            editShoppingItemButton =
                    itemView.findViewById(
                            R.id.editShoppingItemButton
                    );

            deleteShoppingItemButton =
                    itemView.findViewById(
                            R.id.deleteShoppingItemButton
                    );
        }
    }
}