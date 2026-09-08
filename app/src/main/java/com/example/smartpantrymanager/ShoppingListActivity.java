package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;

    private RecyclerView shoppingItemsRecyclerView;

    private ShoppingItemAdapter shoppingItemAdapter;

    private MaterialCardView emptyShoppingCard;

    private final List<ShoppingItem> shoppingItems =
            new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_shopping_list);

        // Shopping list RecyclerView
        shoppingItemsRecyclerView =
                findViewById(
                        R.id.shoppingItemsRecyclerView
                );

        shoppingItemsRecyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        shoppingItemsRecyclerView.setNestedScrollingEnabled(
                true
        );

        // Empty state
        emptyShoppingCard =
                findViewById(
                        R.id.emptyShoppingCard
                );

        // Adapter
        shoppingItemAdapter =
                new ShoppingItemAdapter(
                        shoppingItems
                );

        shoppingItemsRecyclerView.setAdapter(
                shoppingItemAdapter
        );

        // Firebase
        databaseReference = FirebaseDatabase
                .getInstance(
                        "https://smart-pantry-manager-7e502-default-rtdb.europe-west1.firebasedatabase.app/"
                )
                .getReference()
                .child("shopping_items");

        // Back button
        TextView backButton =
                findViewById(
                        R.id.backButton
                );

        backButton.setOnClickListener(v ->
                finish()
        );

        // Add shopping item
        findViewById(R.id.addShoppingItemCard)
                .setOnClickListener(v -> {

                    Intent intent =
                            new Intent(
                                    ShoppingListActivity.this,
                                    AddShoppingItemActivity.class
                            );

                    startActivity(intent);
                });

        // Load shopping items
        loadShoppingItems();
    }

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

                            /*
                             * Make sure the Firebase key is stored
                             * as the item's ID.
                             */
                            if (item.getId() == null
                                    || item.getId().isEmpty()) {

                                item.setId(
                                        itemSnapshot.getKey()
                                );
                            }

                            shoppingItems.add(item);
                        }

                        shoppingItemAdapter
                                .notifyDataSetChanged();

                        updateShoppingListVisibility();
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

    private void updateShoppingListVisibility() {

        if (shoppingItems.isEmpty()) {

            shoppingItemsRecyclerView.setVisibility(
                    View.GONE
            );

            emptyShoppingCard.setVisibility(
                    View.VISIBLE
            );

        } else {

            shoppingItemsRecyclerView.setVisibility(
                    View.VISIBLE
            );

            emptyShoppingCard.setVisibility(
                    View.GONE
            );
        }
    }
}