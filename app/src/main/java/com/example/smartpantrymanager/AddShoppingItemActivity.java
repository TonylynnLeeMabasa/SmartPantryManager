package com.example.smartpantrymanager;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddShoppingItemActivity extends AppCompatActivity {

    private TextInputEditText itemNameInput;
    private TextInputEditText quantityInput;

    private TextInputLayout itemNameInputLayout;
    private TextInputLayout quantityInputLayout;

    private MaterialButton saveShoppingItemButton;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_shopping_item);

        // Input fields
        itemNameInput =
                findViewById(R.id.itemNameInput);

        quantityInput =
                findViewById(R.id.quantityInput);

        // Input layouts
        itemNameInputLayout =
                findViewById(R.id.itemNameInputLayout);

        quantityInputLayout =
                findViewById(R.id.quantityInputLayout);

        // Save button
        saveShoppingItemButton =
                findViewById(R.id.saveShoppingItemButton);

        // Firebase
        databaseReference = FirebaseDatabase
                .getInstance(
                        "https://smart-pantry-manager-7e502-default-rtdb.europe-west1.firebasedatabase.app/"
                )
                .getReference()
                .child("shopping_items");

        // Back button
        TextView backButton =
                findViewById(R.id.backButton);

        backButton.setOnClickListener(v ->
                finish()
        );

        // Save shopping item
        saveShoppingItemButton.setOnClickListener(v ->
                saveShoppingItem()
        );
    }

    private void saveShoppingItem() {

        String itemName =
                itemNameInput.getText()
                        .toString()
                        .trim();

        String quantityText =
                quantityInput.getText()
                        .toString()
                        .trim();

        // Clear previous errors
        itemNameInputLayout.setError(null);
        quantityInputLayout.setError(null);

        // Validate item name
        if (TextUtils.isEmpty(itemName)) {

            itemNameInputLayout.setError(
                    "Please enter an item name."
            );

            itemNameInput.requestFocus();

            return;
        }

        // Validate quantity
        if (TextUtils.isEmpty(quantityText)) {

            quantityInputLayout.setError(
                    "Please enter a quantity."
            );

            quantityInput.requestFocus();

            return;
        }

        int quantity;

        try {

            quantity =
                    Integer.parseInt(quantityText);

        } catch (NumberFormatException e) {

            quantityInputLayout.setError(
                    "Please enter a valid quantity."
            );

            quantityInput.requestFocus();

            return;
        }

        // Quantity must be greater than zero
        if (quantity <= 0) {

            quantityInputLayout.setError(
                    "Quantity must be greater than 0."
            );

            quantityInput.requestFocus();

            return;
        }

        // Create unique Firebase ID
        String itemId =
                databaseReference
                        .push()
                        .getKey();

        if (itemId == null) {

            Toast.makeText(
                    this,
                    "Could not create shopping item.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Create shopping item
        ShoppingItem shoppingItem =
                new ShoppingItem(
                        itemId,
                        itemName,
                        quantity,
                        false
                );

        // Save to Firebase
        databaseReference
                .child(itemId)
                .setValue(shoppingItem)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            AddShoppingItemActivity.this,
                            "Shopping item added.",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
                })
                .addOnFailureListener(error -> {

                    Toast.makeText(
                            AddShoppingItemActivity.this,
                            "Could not save item: "
                                    + error.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}