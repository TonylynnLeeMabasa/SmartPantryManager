package com.example.smartpantrymanager;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Paint;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Locale;

public class ShoppingItemAdapter
        extends RecyclerView.Adapter<ShoppingItemAdapter.ShoppingItemViewHolder> {

    private final List<ShoppingItem> shoppingItems;

    private final DatabaseReference shoppingReference;
    private final DatabaseReference pantryReference;

    public ShoppingItemAdapter(
            List<ShoppingItem> shoppingItems
    ) {

        this.shoppingItems = shoppingItems;

        FirebaseDatabase database =
                FirebaseDatabase.getInstance(
                        "https://smart-pantry-manager-7e502-default-rtdb.europe-west1.firebasedatabase.app/"
                );

        shoppingReference =
                database
                        .getReference()
                        .child("shopping_items");

        pantryReference =
                database
                        .getReference()
                        .child("pantry_items");
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
                            .setValue(isChecked)
                            .addOnCompleteListener(
                                    task -> {

                                        if (!task.isSuccessful()) {

                                            Toast.makeText(
                                                    buttonView.getContext(),
                                                    "Could not update purchase status.",
                                                    Toast.LENGTH_LONG
                                            ).show();

                                            return;
                                        }

                                        if (isChecked) {

                                            showAddToPantryDialog(
                                                    buttonView.getContext(),
                                                    item
                                            );
                                        }
                                    }
                            );
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

    private void showAddToPantryDialog(
            Context context,
            ShoppingItem item
    ) {

        new AlertDialog.Builder(context)
                .setTitle(
                        "Add to Pantry?"
                )
                .setMessage(
                        "Would you like to add "
                                + item.getQuantity()
                                + " "
                                + item.getName()
                                + " to your pantry?"
                )
                .setNegativeButton(
                        "No",
                        null
                )
                .setPositiveButton(
                        "Yes",
                        (dialog, which) -> {

                            addShoppingItemToPantry(
                                    context,
                                    item
                            );
                        }
                )
                .show();
    }

    private void addShoppingItemToPantry(
            Context context,
            ShoppingItem shoppingItem
    ) {

        pantryReference.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot
                    ) {

                        PantryItem existingPantryItem =
                                findPantryItem(
                                        snapshot,
                                        shoppingItem.getName()
                                );

                        if (existingPantryItem != null) {

                            updateExistingPantryItem(
                                    context,
                                    existingPantryItem,
                                    shoppingItem
                            );

                        } else {

                            createNewPantryItem(
                                    context,
                                    shoppingItem
                            );
                        }
                    }

                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error
                    ) {

                        Toast.makeText(
                                context,
                                "Could not access pantry: "
                                        + error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    private PantryItem findPantryItem(
            DataSnapshot snapshot,
            String ingredientName
    ) {

        if (ingredientName == null) {
            return null;
        }

        String requiredName =
                normalizeName(
                        ingredientName
                );

        for (DataSnapshot itemSnapshot :
                snapshot.getChildren()) {

            PantryItem pantryItem =
                    itemSnapshot.getValue(
                            PantryItem.class
                    );

            if (pantryItem == null
                    || pantryItem.getName() == null) {
                continue;
            }

            if (normalizeName(
                    pantryItem.getName()
            ).equals(requiredName)) {

                if (pantryItem.getId() == null
                        || pantryItem.getId().isEmpty()) {

                    pantryItem.setId(
                            itemSnapshot.getKey()
                    );
                }

                return pantryItem;
            }
        }

        return null;
    }

    private void updateExistingPantryItem(
            Context context,
            PantryItem existingItem,
            ShoppingItem shoppingItem
    ) {

        int newQuantity =
                existingItem.getQuantity()
                        + shoppingItem.getQuantity();

        existingItem.setQuantity(
                newQuantity
        );

        if (existingItem.getId() == null
                || existingItem.getId().isEmpty()) {

            Toast.makeText(
                    context,
                    "Pantry item ID is missing.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        pantryReference
                .child(existingItem.getId())
                .setValue(existingItem)
                .addOnCompleteListener(
                        task -> {

                            if (task.isSuccessful()) {

                                Toast.makeText(
                                        context,
                                        shoppingItem.getQuantity()
                                                + " "
                                                + shoppingItem.getName()
                                                + " added to pantry. "
                                                + "New quantity: "
                                                + newQuantity,
                                        Toast.LENGTH_LONG
                                ).show();

                            } else {

                                Toast.makeText(
                                        context,
                                        "Could not update pantry item.",
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }

    private void createNewPantryItem(
            Context context,
            ShoppingItem shoppingItem
    ) {

        String pantryItemId =
                pantryReference
                        .push()
                        .getKey();

        if (pantryItemId == null) {

            Toast.makeText(
                    context,
                    "Could not create pantry item.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        PantryItem newPantryItem =
                new PantryItem(
                        pantryItemId,
                        shoppingItem.getName(),
                        "Other",
                        shoppingItem.getQuantity(),
                        "",
                        1,
                        "Pantry"
                );

        pantryReference
                .child(pantryItemId)
                .setValue(newPantryItem)
                .addOnCompleteListener(
                        task -> {

                            if (task.isSuccessful()) {

                                Toast.makeText(
                                        context,
                                        shoppingItem.getQuantity()
                                                + " "
                                                + shoppingItem.getName()
                                                + " added to pantry.",
                                        Toast.LENGTH_LONG
                                ).show();

                            } else {

                                Toast.makeText(
                                        context,
                                        "Could not add item to pantry.",
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }

    private String normalizeName(
            String name
    ) {

        return name
                .trim()
                .toLowerCase(Locale.getDefault());
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

        CheckBox purchasedCheckBox;

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