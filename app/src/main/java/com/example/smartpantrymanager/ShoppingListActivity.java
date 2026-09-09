package com.example.smartpantrymanager;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;

    private RecyclerView shoppingItemsRecyclerView;
    private ShoppingItemAdapter shoppingItemAdapter;

    private MaterialCardView emptyShoppingCard;
    private MaterialButton clearPurchasedButton;

    private TextView shoppingListCount;

    private final List<ShoppingItem> shoppingItems =
            new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_shopping_list
        );

        shoppingItemsRecyclerView =
                findViewById(
                        R.id.shoppingItemsRecyclerView
                );

        shoppingItemsRecyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        shoppingItemsRecyclerView.setNestedScrollingEnabled(
                false
        );

        emptyShoppingCard =
                findViewById(
                        R.id.emptyShoppingCard
                );

        clearPurchasedButton =
                findViewById(
                        R.id.clearPurchasedButton
                );

        shoppingListCount =
                findViewById(
                        R.id.shoppingListCount
                );

        shoppingItemAdapter =
                new ShoppingItemAdapter(
                        shoppingItems
                );

        shoppingItemsRecyclerView.setAdapter(
                shoppingItemAdapter
        );

        // Connect to the shopping_items section in Firebase.
        databaseReference =
                FirebaseDatabase
                        .getInstance(
                                "https://smart-pantry-manager-7e502-default-rtdb.europe-west1.firebasedatabase.app/"
                        )
                        .getReference()
                        .child("shopping_items");

        TextView backButton =
                findViewById(
                        R.id.backButton
                );

        // Return to the previous screen.
        backButton.setOnClickListener(
                v -> finish()
        );

        // Open the form for adding a shopping item manually.
        findViewById(
                R.id.addShoppingItemCard
        ).setOnClickListener(
                v -> {

                    Intent intent =
                            new Intent(
                                    ShoppingListActivity.this,
                                    AddShoppingItemActivity.class
                            );

                    startActivity(intent);
                }
        );

        // Ask for confirmation before clearing purchased items.
        clearPurchasedButton.setOnClickListener(
                v -> showClearPurchasedConfirmation()
        );

        // Load the shopping list when the screen opens.
        loadShoppingItems();
    }

    // Load shopping items from Firebase.
    private void loadShoppingItems() {

        databaseReference.addValueEventListener(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot
                    ) {

                        shoppingItems.clear();

                        for (DataSnapshot itemSnapshot :
                                snapshot.getChildren()) {

                            ShoppingItem item =
                                    itemSnapshot.getValue(
                                            ShoppingItem.class
                                    );

                            if (item == null) {
                                continue;
                            }

                            // Use the Firebase key when the item has no ID.
                            if (item.getId() == null
                                    || item.getId().isEmpty()) {

                                item.setId(
                                        itemSnapshot.getKey()
                                );
                            }

                            shoppingItems.add(
                                    item
                            );
                        }

                        shoppingItemAdapter
                                .notifyDataSetChanged();

                        updateShoppingListDisplay();
                    }

                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error
                    ) {

                        Toast.makeText(
                                ShoppingListActivity.this,
                                "Could not load shopping list: "
                                        + error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    // Update the item count and empty-list state.
    private void updateShoppingListDisplay() {

        int itemCount =
                shoppingItems.size();

        shoppingListCount.setText(
                itemCount
                        + (itemCount == 1
                        ? " item"
                        : " items")
        );

        if (shoppingItems.isEmpty()) {

            shoppingItemsRecyclerView.setVisibility(
                    View.GONE
            );

            emptyShoppingCard.setVisibility(
                    View.VISIBLE
            );

            clearPurchasedButton.setEnabled(
                    false
            );

        } else {

            shoppingItemsRecyclerView.setVisibility(
                    View.VISIBLE
            );

            emptyShoppingCard.setVisibility(
                    View.GONE
            );

            // Only enable the button when there are purchased items.
            boolean hasPurchasedItems =
                    hasPurchasedItems();

            clearPurchasedButton.setEnabled(
                    hasPurchasedItems
            );
        }
    }

    // Check whether the shopping list contains purchased items.
    private boolean hasPurchasedItems() {

        for (ShoppingItem item :
                shoppingItems) {

            if (item != null
                    && item.isPurchased()) {

                return true;
            }
        }

        return false;
    }

    // Show a confirmation dialog before removing purchased items.
    private void showClearPurchasedConfirmation() {

        if (!hasPurchasedItems()) {

            Toast.makeText(
                    this,
                    "There are no purchased items to clear.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        new AlertDialog.Builder(this)
                .setTitle(
                        "Clear Purchased Items"
                )
                .setMessage(
                        "Are you sure you want to remove all purchased items from your shopping list?"
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Clear",
                        (dialog, which) ->
                                clearPurchasedItems()
                )
                .show();
    }

    // Remove all purchased shopping items from Firebase.
    private void clearPurchasedItems() {

        clearPurchasedButton.setEnabled(
                false
        );

        List<String> purchasedItemIds =
                new ArrayList<>();

        // Collect the IDs of purchased items before deleting them.
        for (ShoppingItem item :
                shoppingItems) {

            if (item != null
                    && item.isPurchased()
                    && item.getId() != null
                    && !item.getId().isEmpty()) {

                purchasedItemIds.add(
                        item.getId()
                );
            }
        }

        if (purchasedItemIds.isEmpty()) {

            clearPurchasedButton.setEnabled(
                    true
            );

            Toast.makeText(
                    this,
                    "There are no purchased items to clear.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Delete the purchased items one at a time.
        clearNextPurchasedItem(
                purchasedItemIds,
                0
        );
    }

    // Continue deleting purchased items until the list is complete.
    private void clearNextPurchasedItem(
            List<String> purchasedItemIds,
            int index
    ) {

        if (index >= purchasedItemIds.size()) {

            Toast.makeText(
                    this,
                    "Purchased items cleared.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        String itemId =
                purchasedItemIds.get(index);

        databaseReference
                .child(itemId)
                .removeValue()
                .addOnCompleteListener(
                        task -> {

                            if (task.isSuccessful()) {

                                clearNextPurchasedItem(
                                        purchasedItemIds,
                                        index + 1
                                );

                            } else {

                                clearPurchasedButton
                                        .setEnabled(true);

                                Toast.makeText(
                                        ShoppingListActivity.this,
                                        "Could not clear all purchased items.",
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }
}