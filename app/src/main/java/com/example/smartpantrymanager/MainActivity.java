package com.example.smartpantrymanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME =
            "smart_pantry_settings";

    private static final String EXPIRY_ALERTS_KEY =
            "expiry_alerts_enabled";

    private static final String LOW_STOCK_ALERTS_KEY =
            "low_stock_alerts_enabled";

    private DatabaseReference databaseReference;

    private TextView totalItemsCount;
    private TextView expiringSoonCount;

    private TextView lowStockCount;
    private TextView lowStockDescription;

    private RecyclerView expiringItemsRecyclerView;
    private RecyclerView lowStockItemsRecyclerView;

    private MaterialCardView emptyExpiryCard;
    private MaterialCardView emptyLowStockCard;

    private ExpiringItemAdapter expiringItemAdapter;
    private LowStockItemAdapter lowStockItemAdapter;

    private final List<PantryItem> pantryItems =
            new ArrayList<>();

    private final List<PantryItem> expiringItems =
            new ArrayList<>();

    private final List<PantryItem> lowStockItems =
            new ArrayList<>();

    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_main
        );

        totalItemsCount =
                findViewById(
                        R.id.totalItemsCount
                );

        expiringSoonCount =
                findViewById(
                        R.id.expiringSoonCount
                );

        lowStockCount =
                findViewById(
                        R.id.lowStockCount
                );

        lowStockDescription =
                findViewById(
                        R.id.lowStockDescription
                );

        expiringItemsRecyclerView =
                findViewById(
                        R.id.expiringItemsRecyclerView
                );

        lowStockItemsRecyclerView =
                findViewById(
                        R.id.lowStockItemsRecyclerView
                );

        emptyExpiryCard =
                findViewById(
                        R.id.emptyExpiryCard
                );

        emptyLowStockCard =
                findViewById(
                        R.id.emptyLowStockCard
                );

        setupRecyclerViews();

        databaseReference =
                FirebaseDatabase
                        .getInstance(
                                "https://smart-pantry-manager-7e502-default-rtdb.europe-west1.firebasedatabase.app/"
                        )
                        .getReference()
                        .child("pantry_items");

        setupNavigation();

        loadPantryItems();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Refresh dashboard visibility after returning from Settings.
        if (pantryItems != null && !pantryItems.isEmpty()) {
            updateDashboard();
        }
    }

    private void setupRecyclerViews() {

        expiringItemsRecyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        expiringItemsRecyclerView
                .setNestedScrollingEnabled(false);

        expiringItemAdapter =
                new ExpiringItemAdapter(
                        expiringItems
                );

        expiringItemsRecyclerView.setAdapter(
                expiringItemAdapter
        );

        lowStockItemsRecyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        lowStockItemsRecyclerView
                .setNestedScrollingEnabled(false);

        lowStockItemAdapter =
                new LowStockItemAdapter(
                        lowStockItems
                );

        lowStockItemsRecyclerView.setAdapter(
                lowStockItemAdapter
        );
    }

    private void setupNavigation() {

        // Open the pantry inventory.
        findViewById(R.id.inventoryCard)
                .setOnClickListener(v -> {

                    Intent intent =
                            new Intent(
                                    MainActivity.this,
                                    InventoryActivity.class
                            );

                    startActivity(intent);
                });

        // Open the add pantry item form.
        findViewById(R.id.addItemCard)
                .setOnClickListener(v -> {

                    Intent intent =
                            new Intent(
                                    MainActivity.this,
                                    AddItemActivity.class
                            );

                    startActivity(intent);
                });

        // Open the shopping list.
        findViewById(R.id.shoppingListCard)
                .setOnClickListener(v -> {

                    Intent intent =
                            new Intent(
                                    MainActivity.this,
                                    ShoppingListActivity.class
                            );

                    startActivity(intent);
                });

        // Open the recipe finder.
        findViewById(R.id.recipeFinderCard)
                .setOnClickListener(v -> {

                    Intent intent =
                            new Intent(
                                    MainActivity.this,
                                    RecipeListActivity.class
                            );

                    startActivity(intent);
                });

        // Open the required Settings/Profile screen.
        findViewById(R.id.profileNav)
                .setOnClickListener(v -> {

                    Intent intent =
                            new Intent(
                                    MainActivity.this,
                                    SettingsActivity.class
                            );

                    startActivity(intent);
                });
    }

    // Load pantry items from Firebase.
    private void loadPantryItems() {

        databaseReference.addValueEventListener(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot
                    ) {

                        pantryItems.clear();

                        for (
                                DataSnapshot itemSnapshot :
                                snapshot.getChildren()
                        ) {

                            PantryItem item =
                                    itemSnapshot.getValue(
                                            PantryItem.class
                                    );

                            if (item == null) {
                                continue;
                            }

                            // Use the Firebase key when an item has no ID.
                            if (item.getId() == null
                                    || item.getId().isEmpty()) {

                                item.setId(
                                        itemSnapshot.getKey()
                                );
                            }

                            pantryItems.add(item);
                        }

                        updateDashboard();
                    }

                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error
                    ) {

                        Toast.makeText(
                                MainActivity.this,
                                "Could not load pantry data: "
                                        + error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    // Update dashboard statistics and alert sections.
    private void updateDashboard() {

        totalItemsCount.setText(
                String.valueOf(
                        pantryItems.size()
                )
        );

        updateExpiringItems();

        updateLowStockItems();
    }

    // Find pantry items expiring within the next seven days.
    private void updateExpiringItems() {

        SharedPreferences preferences =
                getSharedPreferences(
                        PREFS_NAME,
                        MODE_PRIVATE
                );

        boolean alertsEnabled =
                preferences.getBoolean(
                        EXPIRY_ALERTS_KEY,
                        true
                );

        if (!alertsEnabled) {

            expiringItems.clear();

            expiringSoonCount.setText("0");

            expiringItemsRecyclerView.setVisibility(
                    View.GONE
            );

            emptyExpiryCard.setVisibility(
                    View.GONE
            );

            return;
        }

        expiringItems.clear();

        Calendar today =
                Calendar.getInstance();

        Calendar sevenDaysFromNow =
                Calendar.getInstance();

        sevenDaysFromNow.add(
                Calendar.DAY_OF_YEAR,
                7
        );

        for (PantryItem item :
                pantryItems) {

            String expiryDateText =
                    item.getExpiryDate();

            if (expiryDateText == null
                    || expiryDateText.isEmpty()) {

                continue;
            }

            try {

                Date expiryDate =
                        dateFormat.parse(
                                expiryDateText
                        );

                if (expiryDate == null) {
                    continue;
                }

                Calendar expiryCalendar =
                        Calendar.getInstance();

                expiryCalendar.setTime(
                        expiryDate
                );

                if (!expiryCalendar.before(today)
                        && !expiryCalendar.after(
                        sevenDaysFromNow
                )) {

                    expiringItems.add(item);
                }

            } catch (ParseException ignored) {
                // Ignore invalid expiry dates.
            }
        }

        expiringSoonCount.setText(
                String.valueOf(
                        expiringItems.size()
                )
        );

        expiringItemAdapter.notifyDataSetChanged();

        if (expiringItems.isEmpty()) {

            expiringItemsRecyclerView.setVisibility(
                    View.GONE
            );

            emptyExpiryCard.setVisibility(
                    View.VISIBLE
            );

        } else {

            expiringItemsRecyclerView.setVisibility(
                    View.VISIBLE
            );

            emptyExpiryCard.setVisibility(
                    View.GONE
            );
        }
    }

    // Find pantry items at or below their low-stock level.
    private void updateLowStockItems() {

        SharedPreferences preferences =
                getSharedPreferences(
                        PREFS_NAME,
                        MODE_PRIVATE
                );

        boolean alertsEnabled =
                preferences.getBoolean(
                        LOW_STOCK_ALERTS_KEY,
                        true
                );

        if (!alertsEnabled) {

            lowStockItems.clear();

            lowStockCount.setText("0");

            lowStockItemsRecyclerView.setVisibility(
                    View.GONE
            );

            emptyLowStockCard.setVisibility(
                    View.GONE
            );

            lowStockDescription.setText(
                    "Low-stock alerts are disabled"
            );

            return;
        }

        lowStockItems.clear();

        for (PantryItem item :
                pantryItems) {

            if (item.getQuantity()
                    <= item.getLowStockLevel()) {

                lowStockItems.add(item);
            }
        }

        lowStockCount.setText(
                String.valueOf(
                        lowStockItems.size()
                )
        );

        if (lowStockItems.isEmpty()) {

            lowStockDescription.setText(
                    "Items that need attention"
            );

            lowStockItemsRecyclerView.setVisibility(
                    View.GONE
            );

            emptyLowStockCard.setVisibility(
                    View.VISIBLE
            );

        } else {

            lowStockDescription.setText(
                    "These items are running low"
            );

            lowStockItemsRecyclerView.setVisibility(
                    View.VISIBLE
            );

            emptyLowStockCard.setVisibility(
                    View.GONE
            );
        }

        lowStockItemAdapter.notifyDataSetChanged();
    }
}