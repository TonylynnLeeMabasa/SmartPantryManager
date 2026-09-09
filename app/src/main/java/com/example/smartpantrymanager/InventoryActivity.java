package com.example.smartpantrymanager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class InventoryActivity extends AppCompatActivity {

    private RecyclerView inventoryRecyclerView;
    private PantryItemAdapter pantryItemAdapter;

    private final List<PantryItem> pantryItems =
            new ArrayList<>();

    private final List<PantryItem> allPantryItems =
            new ArrayList<>();

    private final List<String> categories =
            new ArrayList<>();

    private final List<String> locations =
            new ArrayList<>();

    private EditText searchInput;
    private Spinner categorySpinner;
    private Spinner locationSpinner;

    private DatabaseReference databaseReference;

    private boolean loadingFilterOptions = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_inventory
        );

        inventoryRecyclerView =
                findViewById(
                        R.id.inventoryRecyclerView
                );

        searchInput =
                findViewById(
                        R.id.searchInput
                );

        categorySpinner =
                findViewById(
                        R.id.categorySpinner
                );

        locationSpinner =
                findViewById(
                        R.id.locationSpinner
                );

        inventoryRecyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        pantryItemAdapter =
                new PantryItemAdapter(
                        pantryItems
                );

        inventoryRecyclerView.setAdapter(
                pantryItemAdapter
        );

        findViewById(
                R.id.backButton
        ).setOnClickListener(
                v -> finish()
        );

        databaseReference =
                FirebaseDatabase
                        .getInstance(
                                "https://smart-pantry-manager-7e502-default-rtdb.europe-west1.firebasedatabase.app/"
                        )
                        .getReference();

        setupFilterListeners();

        loadPantryItems();

        // Filter the inventory as the user types.
        searchInput.addTextChangedListener(
                new TextWatcher() {

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

                        filterPantryItems();
                    }

                    @Override
                    public void afterTextChanged(
                            Editable editable
                    ) {
                    }
                }
        );
    }

    // Listen for changes to the category and location filters.
    private void setupFilterListeners() {

        categorySpinner.setOnItemSelectedListener(
                new android.widget.AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            android.widget.AdapterView<?> parent,
                            android.view.View view,
                            int position,
                            long id
                    ) {

                        if (!loadingFilterOptions) {
                            filterPantryItems();
                        }
                    }

                    @Override
                    public void onNothingSelected(
                            android.widget.AdapterView<?> parent
                    ) {
                    }
                }
        );

        locationSpinner.setOnItemSelectedListener(
                new android.widget.AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            android.widget.AdapterView<?> parent,
                            android.view.View view,
                            int position,
                            long id
                    ) {

                        if (!loadingFilterOptions) {
                            filterPantryItems();
                        }
                    }

                    @Override
                    public void onNothingSelected(
                            android.widget.AdapterView<?> parent
                    ) {
                    }
                }
        );
    }

    // Load pantry items from Firebase.
    private void loadPantryItems() {

        databaseReference
                .child("pantry_items")
                .addValueEventListener(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(
                                    @NonNull DataSnapshot snapshot
                            ) {

                                allPantryItems.clear();

                                for (
                                        DataSnapshot itemSnapshot :
                                        snapshot.getChildren()
                                ) {

                                    PantryItem item =
                                            itemSnapshot.getValue(
                                                    PantryItem.class
                                            );

                                    if (item != null) {

                                        // Use the Firebase key when an item has no ID.
                                        if (item.getId() == null
                                                || item.getId().isEmpty()) {

                                            item.setId(
                                                    itemSnapshot.getKey()
                                            );
                                        }

                                        allPantryItems.add(
                                                item
                                        );
                                    }
                                }

                                setupFilterOptions();

                                filterPantryItems();
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
                        }
                );
    }

    // Build filter options from the categories and locations in Firebase.
    private void setupFilterOptions() {

        Set<String> categorySet =
                new LinkedHashSet<>();

        Set<String> locationSet =
                new LinkedHashSet<>();

        for (PantryItem item :
                allPantryItems) {

            if (item.getCategory() != null
                    && !item.getCategory().trim().isEmpty()) {

                categorySet.add(
                        item.getCategory().trim()
                );
            }

            if (item.getLocation() != null
                    && !item.getLocation().trim().isEmpty()) {

                locationSet.add(
                        item.getLocation().trim()
                );
            }
        }

        categories.clear();
        categories.add("All Categories");
        categories.addAll(categorySet);

        locations.clear();
        locations.add("All Locations");
        locations.addAll(locationSet);

        loadingFilterOptions = true;

        ArrayAdapter<String> categoryAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        categories
                );

        categoryAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        categorySpinner.setAdapter(
                categoryAdapter
        );

        ArrayAdapter<String> locationAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        locations
                );

        locationAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        locationSpinner.setAdapter(
                locationAdapter
        );

        loadingFilterOptions = false;
    }

    // Apply search, category and location filters together.
    private void filterPantryItems() {

        String search =
                searchInput.getText()
                        .toString()
                        .trim()
                        .toLowerCase();

        String selectedCategory =
                categorySpinner.getSelectedItem() == null
                        ? "All Categories"
                        : categorySpinner
                        .getSelectedItem()
                        .toString();

        String selectedLocation =
                locationSpinner.getSelectedItem() == null
                        ? "All Locations"
                        : locationSpinner
                        .getSelectedItem()
                        .toString();

        pantryItems.clear();

        for (PantryItem item :
                allPantryItems) {

            String itemName =
                    item.getName() == null
                            ? ""
                            : item.getName()
                            .trim()
                            .toLowerCase();

            String category =
                    item.getCategory() == null
                            ? ""
                            : item.getCategory()
                            .trim()
                            .toLowerCase();

            String location =
                    item.getLocation() == null
                            ? ""
                            : item.getLocation()
                            .trim()
                            .toLowerCase();

            boolean matchesSearch =
                    search.isEmpty()
                            || itemName.contains(search)
                            || category.contains(search)
                            || location.contains(search);

            boolean matchesCategory =
                    selectedCategory.equals(
                            "All Categories"
                    )
                            || category.equals(
                            selectedCategory
                                    .trim()
                                    .toLowerCase()
                    );

            boolean matchesLocation =
                    selectedLocation.equals(
                            "All Locations"
                    )
                            || location.equals(
                            selectedLocation
                                    .trim()
                                    .toLowerCase()
                    );

            if (matchesSearch
                    && matchesCategory
                    && matchesLocation) {

                pantryItems.add(item);
            }
        }

        pantryItemAdapter.notifyDataSetChanged();
    }
}