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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;

    private TextView totalItemsCount;
    private TextView lowStockCount;
    private TextView expiringSoonCount;

    private TextView lowStockMessage;
    private TextView lowStockDescription;

    private RecyclerView expiringItemsRecyclerView;
    private ExpiringItemAdapter expiringItemAdapter;

    private final List<PantryItem> expiringItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Dashboard statistics
        totalItemsCount = findViewById(R.id.totalItemsCount);
        lowStockCount = findViewById(R.id.lowStockCount);
        expiringSoonCount = findViewById(R.id.expiringSoonCount);

        lowStockMessage = findViewById(R.id.lowStockMessage);
        lowStockDescription = findViewById(R.id.lowStockDescription);

        // Expiring items list
        expiringItemsRecyclerView =
                findViewById(R.id.expiringItemsRecyclerView);

        expiringItemsRecyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        expiringItemsRecyclerView.setNestedScrollingEnabled(false);

        expiringItemAdapter =
                new ExpiringItemAdapter(expiringItems);

        expiringItemsRecyclerView.setAdapter(
                expiringItemAdapter
        );

        // Firebase
        databaseReference = FirebaseDatabase
                .getInstance(
                        "https://smart-pantry-manager-7e502-default-rtdb.europe-west1.firebasedatabase.app/"
                )
                .getReference();

        loadDashboardStatistics();

        // Add item
        findViewById(R.id.addItemCard).setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    AddItemActivity.class
            );

            startActivity(intent);
        });

        // Inventory
        findViewById(R.id.inventoryCard).setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    InventoryActivity.class
            );

            startActivity(intent);
        });
    }

    private void loadDashboardStatistics() {

        databaseReference
                .child("pantry_items")
                .addValueEventListener(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(
                                    @NonNull DataSnapshot snapshot
                            ) {

                                int totalItems = 0;
                                int lowStockItems = 0;
                                int expiringSoonItems = 0;

                                expiringItems.clear();

                                for (DataSnapshot itemSnapshot
                                        : snapshot.getChildren()) {

                                    PantryItem item =
                                            itemSnapshot.getValue(
                                                    PantryItem.class
                                            );

                                    if (item == null) {
                                        continue;
                                    }

                                    totalItems++;

                                    // Check low stock
                                    if (item.getQuantity()
                                            <= item.getLowStockLevel()) {

                                        lowStockItems++;
                                    }

                                    // Check expiry
                                    if (isExpiringSoon(
                                            item.getExpiryDate()
                                    )) {

                                        expiringSoonItems++;

                                        expiringItems.add(item);
                                    }
                                }

                                // Sort expiring items
                                // by closest expiry date
                                sortExpiringItems();

                                // Update dashboard numbers
                                totalItemsCount.setText(
                                        String.valueOf(totalItems)
                                );

                                lowStockCount.setText(
                                        String.valueOf(lowStockItems)
                                );

                                expiringSoonCount.setText(
                                        String.valueOf(
                                                expiringSoonItems
                                        )
                                );

                                // Update low-stock message
                                updateLowStockMessage(
                                        lowStockItems
                                );

                                // Update expiry list
                                expiringItemAdapter
                                        .notifyDataSetChanged();

                                updateExpiringItemsVisibility();
                            }

                            @Override
                            public void onCancelled(
                                    @NonNull DatabaseError error
                            ) {

                                Toast.makeText(
                                        MainActivity.this,
                                        "Could not load dashboard data: "
                                                + error.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }

    private boolean isExpiringSoon(
            String expiryDate
    ) {

        if (expiryDate == null
                || expiryDate.trim().isEmpty()) {

            return false;
        }

        SimpleDateFormat dateFormat =
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                );

        dateFormat.setLenient(false);

        try {

            Date expiry =
                    dateFormat.parse(expiryDate);

            if (expiry == null) {
                return false;
            }

            Calendar today =
                    Calendar.getInstance();

            today.set(
                    Calendar.HOUR_OF_DAY,
                    0
            );

            today.set(
                    Calendar.MINUTE,
                    0
            );

            today.set(
                    Calendar.SECOND,
                    0
            );

            today.set(
                    Calendar.MILLISECOND,
                    0
            );

            Calendar sevenDaysFromNow =
                    (Calendar) today.clone();

            sevenDaysFromNow.add(
                    Calendar.DAY_OF_YEAR,
                    7
            );

            return !expiry.before(
                    today.getTime()
            )
                    && !expiry.after(
                    sevenDaysFromNow.getTime()
            );

        } catch (ParseException e) {

            return false;
        }
    }

    private void sortExpiringItems() {

        SimpleDateFormat dateFormat =
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                );

        Collections.sort(
                expiringItems,
                new Comparator<PantryItem>() {

                    @Override
                    public int compare(
                            PantryItem first,
                            PantryItem second
                    ) {

                        try {

                            Date firstDate =
                                    dateFormat.parse(
                                            first.getExpiryDate()
                                    );

                            Date secondDate =
                                    dateFormat.parse(
                                            second.getExpiryDate()
                                    );

                            if (firstDate == null
                                    || secondDate == null) {

                                return 0;
                            }

                            return firstDate.compareTo(
                                    secondDate
                            );

                        } catch (ParseException e) {

                            return 0;
                        }
                    }
                }
        );
    }

    private void updateExpiringItemsVisibility() {

        /*
         * emptyExpiryCard is a MaterialCardView,
         * so we use View instead of TextView.
         */
        View emptyExpiryCard =
                findViewById(R.id.emptyExpiryCard);

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

    private void updateLowStockMessage(
            int lowStockItems
    ) {

        if (lowStockItems == 0) {

            lowStockMessage.setText(
                    "Everything is stocked"
            );

            lowStockDescription.setText(
                    "No low-stock items yet."
            );

        } else if (lowStockItems == 1) {

            lowStockMessage.setText(
                    "1 item needs attention"
            );

            lowStockDescription.setText(
                    "One pantry item is running low."
            );

        } else {

            lowStockMessage.setText(
                    lowStockItems
                            + " items need attention"
            );

            lowStockDescription.setText(
                    "Some pantry items are running low."
            );
        }
    }

    @Override
    protected void onResume() {

        super.onResume();

        // Firebase keeps the dashboard
        // automatically updated.
    }
}