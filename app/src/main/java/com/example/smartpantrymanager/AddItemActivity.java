package com.example.smartpantrymanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddItemActivity extends AppCompatActivity {

    private EditText itemNameInput;
    private EditText categoryInput;
    private EditText quantityInput;
    private EditText expiryDateInput;
    private EditText lowStockInput;
    private EditText locationInput;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_item);

        // Connect to the Smart Pantry Manager Firebase database
        databaseReference = FirebaseDatabase
                .getInstance(
                        "https://smart-pantry-manager-7e502-default-rtdb.europe-west1.firebasedatabase.app/"
                )
                .getReference();

        // Connect XML fields to Java
        itemNameInput = findViewById(R.id.itemNameInput);
        categoryInput = findViewById(R.id.categoryInput);
        quantityInput = findViewById(R.id.quantityInput);
        expiryDateInput = findViewById(R.id.expiryDateInput);
        lowStockInput = findViewById(R.id.lowStockInput);
        locationInput = findViewById(R.id.locationInput);

        // Back button
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        // Expiry date picker
        expiryDateInput.setOnClickListener(v -> showDatePicker());

        // Save pantry item
        findViewById(R.id.saveItemButton).setOnClickListener(v -> savePantryItem());
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

    private void savePantryItem() {

        String itemName = itemNameInput.getText().toString().trim();
        String category = categoryInput.getText().toString().trim();
        String quantityText = quantityInput.getText().toString().trim();
        String expiryDate = expiryDateInput.getText().toString().trim();
        String lowStockText = lowStockInput.getText().toString().trim();
        String location = locationInput.getText().toString().trim();

        // Check required fields
        if (itemName.isEmpty()) {
            itemNameInput.setError("Enter the item name");
            itemNameInput.requestFocus();
            return;
        }

        if (category.isEmpty()) {
            categoryInput.setError("Enter the category");
            categoryInput.requestFocus();
            return;
        }

        if (quantityText.isEmpty()) {
            quantityInput.setError("Enter the quantity");
            quantityInput.requestFocus();
            return;
        }

        if (expiryDate.isEmpty()) {
            expiryDateInput.setError("Select an expiry date");
            expiryDateInput.requestFocus();
            return;
        }

        if (lowStockText.isEmpty()) {
            lowStockInput.setError("Enter the low-stock threshold");
            lowStockInput.requestFocus();
            return;
        }

        if (location.isEmpty()) {
            locationInput.setError("Enter the storage location");
            locationInput.requestFocus();
            return;
        }

        int quantity;
        int lowStockLevel;

        try {
            quantity = Integer.parseInt(quantityText);
            lowStockLevel = Integer.parseInt(lowStockText);
        } catch (NumberFormatException e) {

            Toast.makeText(
                    this,
                    "Please enter valid numbers for quantity and threshold.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Create a unique Firebase ID
        String itemId = databaseReference
                .child("pantry_items")
                .push()
                .getKey();

        if (itemId == null) {
            Toast.makeText(
                    this,
                    "Unable to create pantry item.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Create pantry item data
        Map<String, Object> pantryItem = new HashMap<>();

        pantryItem.put("id", itemId);
        pantryItem.put("name", itemName);
        pantryItem.put("category", category);
        pantryItem.put("quantity", quantity);
        pantryItem.put("expiryDate", expiryDate);
        pantryItem.put("lowStockLevel", lowStockLevel);
        pantryItem.put("location", location);

        // Save to Firebase
        databaseReference
                .child("pantry_items")
                .child(itemId)
                .setValue(pantryItem)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            AddItemActivity.this,
                            "Pantry item saved successfully!",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            AddItemActivity.this,
                            "Could not save item: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}