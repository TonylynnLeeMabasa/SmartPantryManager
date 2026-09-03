package com.example.smartpantrymanager;

import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

public class InventoryActivity extends AppCompatActivity {

    private RecyclerView inventoryRecyclerView;
    private PantryItemAdapter pantryItemAdapter;
    private final List<PantryItem> pantryItems = new ArrayList<>();

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_inventory);

        inventoryRecyclerView = findViewById(R.id.inventoryRecyclerView);

        inventoryRecyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        pantryItemAdapter = new PantryItemAdapter(pantryItems);
        inventoryRecyclerView.setAdapter(pantryItemAdapter);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        databaseReference = FirebaseDatabase
                .getInstance(
                        "https://smart-pantry-manager-7e502-default-rtdb.europe-west1.firebasedatabase.app/"
                )
                .getReference();

        loadPantryItems();
    }

    private void loadPantryItems() {

        databaseReference
                .child("pantry_items")
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot
                    ) {

                        pantryItems.clear();

                        for (DataSnapshot itemSnapshot : snapshot.getChildren()) {

                            PantryItem item =
                                    itemSnapshot.getValue(PantryItem.class);

                            if (item != null) {
                                pantryItems.add(item);
                            }
                        }

                        pantryItemAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error
                    ) {

                        Toast.makeText(
                                InventoryActivity.this,
                                "Could not load pantry items: "
                                        + error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}