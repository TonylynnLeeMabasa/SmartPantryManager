package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.EditText;
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
    private final List<PantryItem> allPantryItems = new ArrayList<>();

    private EditText searchInput;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_inventory);

        inventoryRecyclerView = findViewById(R.id.inventoryRecyclerView);
        searchInput = findViewById(R.id.searchInput);

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

        searchInput.addTextChangedListener(
                new android.text.TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence text,
                            int start,
                            int count,
                            int after
                    ) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence text,
                            int start,
                            int before,
                            int count
                    ) {
                        filterPantryItems(text.toString());
                    }

                    @Override
                    public void afterTextChanged(
                            android.text.Editable editable
                    ) {
                    }
                }
        );
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
                        allPantryItems.clear();

                        for (DataSnapshot itemSnapshot : snapshot.getChildren()) {

                            PantryItem item =
                                    itemSnapshot.getValue(PantryItem.class);

                            if (item != null) {
                                pantryItems.add(item);
                                allPantryItems.add(item);
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

    private void filterPantryItems(String searchText) {

        String search = searchText.trim().toLowerCase();

        pantryItems.clear();

        if (search.isEmpty()) {
            pantryItems.addAll(allPantryItems);
        } else {

            for (PantryItem item : allPantryItems) {

                String itemName = item.getName() == null
                        ? ""
                        : item.getName().toLowerCase();

                String category = item.getCategory() == null
                        ? ""
                        : item.getCategory().toLowerCase();

                String location = item.getLocation() == null
                        ? ""
                        : item.getLocation().toLowerCase();

                if (itemName.contains(search)
                        || category.contains(search)
                        || location.contains(search)) {

                    pantryItems.add(item);
                }
            }
        }

        pantryItemAdapter.notifyDataSetChanged();
    }
}