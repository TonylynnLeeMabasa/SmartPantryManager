package com.example.smartpantrymanager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class EditItemActivity extends AppCompatActivity {

    private EditText itemNameInput;
    private EditText categoryInput;
    private EditText quantityInput;
    private EditText expiryDateInput;
    private EditText lowStockInput;
    private EditText locationInput;

    private DatabaseReference databaseReference;
    private String itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_item);

        itemNameInput = findViewById(R.id.editItemNameInput);
        categoryInput = findViewById(R.id.editCategoryInput);
        quantityInput = findViewById(R.id.editQuantityInput);
        expiryDateInput = findViewById(R.id.editExpiryDateInput);
        lowStockInput = findViewById(R.id.editLowStockInput);
        locationInput = findViewById(R.id.editLocationInput);

        databaseReference = FirebaseDatabase
                .getInstance(
                        "https://smart-pantry-manager-7e502-default-rtdb.europe-west1.firebasedatabase.app/"
                )
                .getReference();

        itemId = getIntent().getStringExtra("itemId");

        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        expiryDateInput.setOnClickListener(v -> showDatePicker());

        findViewById(R.id.updateItemButton)
                .setOnClickListener(v -> updatePantryItem());

        findViewById(R.id.deleteItemButton)
                .setOnClickListener(v -> confirmDelete());

        loadItem();
    }

    private void loadItem() {

        if (itemId == null || itemId.isEmpty()) {
            Toast.makeText(
                    this,
                    "Pantry item could not be found.",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        databaseReference
                .child("pantry_items")
                .child(itemId)
                .get()
                .addOnSuccessListener(snapshot -> {

                    PantryItem item =
                            snapshot.getValue(PantryItem.class);

                    if (item == null) {
                        Toast.makeText(
                                EditItemActivity.this,
                                "Pantry item could not be found.",
                                Toast.LENGTH_SHORT
                        ).show();

                        finish();
                        return;
                    }

                    itemNameInput.setText(item.getName());

                    categoryInput.setText(
                            item.getCategory()
                    );

                    quantityInput.setText(
                            String.valueOf(item.getQuantity())
                    );

                    expiryDateInput.setText(
                            item.getExpiryDate()
                    );

                    lowStockInput.setText(
                            String.valueOf(item.getLowStockLevel())
                    );

                    locationInput.setText(
                            item.getLocation()
                    );
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            EditItemActivity.this,
                            "Could not load item: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void showDatePicker() {

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {

                    String selectedDate =
                            String.format(
                                    "%04d-%02d-%02d",
                                    selectedYear,
                                    selectedMonth + 1,
                                    selectedDay
                            );

                    expiryDateInput.setText(selectedDate);
                },
                year,
                month,
                day
        );

        datePickerDialog.show();
    }

    private void updatePantryItem() {

        String itemName =
                itemNameInput.getText().toString().trim();

        String category =
                categoryInput.getText().toString().trim();

        String quantityText =
                quantityInput.getText().toString().trim();

        String expiryDate =
                expiryDateInput.getText().toString().trim();

        String lowStockText =
                lowStockInput.getText().toString().trim();

        String location =
                locationInput.getText().toString().trim();

        if (itemName.isEmpty()) {

            itemNameInput.setError(
                    "Enter the item name"
            );

            itemNameInput.requestFocus();
            return;
        }

        if (category.isEmpty()) {

            categoryInput.setError(
                    "Enter the category"
            );

            categoryInput.requestFocus();
            return;
        }

        if (quantityText.isEmpty()) {

            quantityInput.setError(
                    "Enter the quantity"
            );

            quantityInput.requestFocus();
            return;
        }

        if (expiryDate.isEmpty()) {

            expiryDateInput.setError(
                    "Select an expiry date"
            );

            expiryDateInput.requestFocus();
            return;
        }

        if (lowStockText.isEmpty()) {

            lowStockInput.setError(
                    "Enter the low-stock threshold"
            );

            lowStockInput.requestFocus();
            return;
        }

        if (location.isEmpty()) {

            locationInput.setError(
                    "Enter the storage location"
            );

            locationInput.requestFocus();
            return;
        }

        int quantity;
        int lowStockLevel;

        try {

            quantity =
                    Integer.parseInt(quantityText);

            lowStockLevel =
                    Integer.parseInt(lowStockText);

        } catch (NumberFormatException e) {

            Toast.makeText(
                    this,
                    "Please enter valid numbers for quantity and threshold.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        PantryItem updatedItem =
                new PantryItem(
                        itemId,
                        itemName,
                        category,
                        quantity,
                        expiryDate,
                        lowStockLevel,
                        location
                );

        databaseReference
                .child("pantry_items")
                .child(itemId)
                .setValue(updatedItem)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            EditItemActivity.this,
                            "Pantry item updated successfully!",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            EditItemActivity.this,
                            "Could not update item: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void confirmDelete() {

        new AlertDialog.Builder(this)
                .setTitle("Delete pantry item")
                .setMessage(
                        "Are you sure you want to delete this item? "
                                + "This action cannot be undone."
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> deletePantryItem()
                )
                .show();
    }

    private void deletePantryItem() {

        if (itemId == null || itemId.isEmpty()) {

            Toast.makeText(
                    this,
                    "Pantry item could not be found.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        databaseReference
                .child("pantry_items")
                .child(itemId)
                .removeValue()
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            EditItemActivity.this,
                            "Pantry item deleted successfully!",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            EditItemActivity.this,
                            "Could not delete item: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}