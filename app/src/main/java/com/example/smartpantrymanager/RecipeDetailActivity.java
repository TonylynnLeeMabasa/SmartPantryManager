package com.example.smartpantrymanager;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecipeDetailActivity extends AppCompatActivity {

    private DatabaseReference recipeReference;
    private DatabaseReference pantryReference;
    private DatabaseReference shoppingReference;

    private TextView recipeName;
    private TextView recipeDescription;
    private TextView recipeStatusText;
    private TextView ingredientsSummary;

    private LinearLayout ingredientsContainer;
    private LinearLayout missingIngredientsContainer;
    private LinearLayout instructionsContainer;

    private MaterialCardView missingIngredientsCard;
    private MaterialButton addMissingToShoppingListButton;

    private Recipe recipe;

    private final List<PantryItem> pantryItems =
            new ArrayList<>();

    private final List<String> missingIngredients =
            new ArrayList<>();

    private final List<String> missingIngredientNames =
            new ArrayList<>();

    private final List<Integer> missingIngredientQuantities =
            new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_recipe_detail
        );

        recipeName =
                findViewById(R.id.recipeName);

        recipeDescription =
                findViewById(R.id.recipeDescription);

        recipeStatusText =
                findViewById(R.id.recipeStatusText);

        ingredientsSummary =
                findViewById(R.id.ingredientsSummary);

        ingredientsContainer =
                findViewById(R.id.ingredientsContainer);

        missingIngredientsContainer =
                findViewById(
                        R.id.missingIngredientsContainer
                );

        instructionsContainer =
                findViewById(R.id.instructionsContainer);

        missingIngredientsCard =
                findViewById(
                        R.id.missingIngredientsCard
                );

        addMissingToShoppingListButton =
                findViewById(
                        R.id.addMissingToShoppingListButton
                );

        TextView backButton =
                findViewById(R.id.backButton);

        backButton.setOnClickListener(
                v -> finish()
        );

        addMissingToShoppingListButton.setOnClickListener(
                v -> addMissingIngredientsToShoppingList()
        );

        String recipeId =
                getIntent().getStringExtra(
                        "recipeId"
                );

        if (recipeId == null
                || recipeId.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Recipe could not be found.",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        FirebaseDatabase database =
                FirebaseDatabase.getInstance(
                        "https://smart-pantry-manager-7e502-default-rtdb.europe-west1.firebasedatabase.app/"
                );

        recipeReference =
                database
                        .getReference()
                        .child("recipes")
                        .child(recipeId);

        pantryReference =
                database
                        .getReference()
                        .child("pantry_items");

        shoppingReference =
                database
                        .getReference()
                        .child("shopping_items");

        loadRecipe();
    }

    private void loadRecipe() {

        recipeReference.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot
                    ) {

                        recipe =
                                snapshot.getValue(
                                        Recipe.class
                                );

                        if (recipe == null) {

                            Toast.makeText(
                                    RecipeDetailActivity.this,
                                    "Recipe could not be loaded.",
                                    Toast.LENGTH_LONG
                            ).show();

                            finish();
                            return;
                        }

                        if (recipe.getId() == null
                                || recipe.getId().isEmpty()) {

                            recipe.setId(
                                    snapshot.getKey()
                            );
                        }

                        displayRecipe();

                        loadPantryItems();
                    }

                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error
                    ) {

                        Toast.makeText(
                                RecipeDetailActivity.this,
                                "Could not load recipe: "
                                        + error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    private void displayRecipe() {

        recipeName.setText(
                recipe.getName()
        );

        recipeDescription.setText(
                recipe.getDescription()
        );

        displayIngredients();

        displayInstructions();
    }

    private void displayIngredients() {

        ingredientsContainer.removeAllViews();

        List<String> ingredients =
                recipe.getIngredients();

        List<Integer> quantities =
                recipe.getQuantities();

        if (ingredients == null
                || ingredients.isEmpty()) {

            ingredientsSummary.setText(
                    "No ingredients listed."
            );

            return;
        }

        ingredientsSummary.setText(
                ingredients.size()
                        + (ingredients.size() == 1
                        ? " ingredient required"
                        : " ingredients required")
        );

        for (int i = 0;
             i < ingredients.size();
             i++) {

            String ingredient =
                    ingredients.get(i);

            int quantity = 1;

            if (quantities != null
                    && i < quantities.size()
                    && quantities.get(i) != null) {

                quantity =
                        quantities.get(i);
            }

            TextView ingredientText =
                    createIngredientTextView(
                            ingredient
                                    + "  •  Required: "
                                    + quantity
                    );

            ingredientsContainer.addView(
                    ingredientText
            );
        }
    }

    private void displayInstructions() {

        instructionsContainer.removeAllViews();

        List<String> instructions =
                recipe.getInstructions();

        if (instructions == null
                || instructions.isEmpty()) {

            TextView noInstructions =
                    createBodyTextView(
                            "No cooking instructions available."
                    );

            instructionsContainer.addView(
                    noInstructions
            );

            return;
        }

        for (int i = 0;
             i < instructions.size();
             i++) {

            String instruction =
                    instructions.get(i);

            TextView instructionText =
                    createInstructionTextView(
                            (i + 1)
                                    + ". "
                                    + instruction
                    );

            instructionsContainer.addView(
                    instructionText
            );
        }
    }

    private void loadPantryItems() {

        pantryReference.addListenerForSingleValueEvent(
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

                            pantryItems.add(item);
                        }

                        checkRecipeAvailability();
                    }

                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error
                    ) {

                        Toast.makeText(
                                RecipeDetailActivity.this,
                                "Could not check pantry: "
                                        + error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    private void checkRecipeAvailability() {

        missingIngredients.clear();

        missingIngredientNames.clear();

        missingIngredientQuantities.clear();

        List<String> ingredients =
                recipe.getIngredients();

        List<Integer> quantities =
                recipe.getQuantities();

        if (ingredients == null
                || ingredients.isEmpty()) {

            updateAvailabilityDisplay(
                    true
            );

            return;
        }

        for (int i = 0;
             i < ingredients.size();
             i++) {

            String requiredIngredient =
                    ingredients.get(i);

            int requiredQuantity = 1;

            if (quantities != null
                    && i < quantities.size()
                    && quantities.get(i) != null) {

                requiredQuantity =
                        quantities.get(i);
            }

            PantryItem pantryItem =
                    findPantryItem(
                            requiredIngredient
                    );

            int availableQuantity = 0;

            if (pantryItem != null) {

                availableQuantity =
                        pantryItem.getQuantity();
            }

            if (availableQuantity
                    < requiredQuantity) {

                int shortage =
                        requiredQuantity
                                - availableQuantity;

                missingIngredientNames.add(
                        requiredIngredient
                );

                missingIngredientQuantities.add(
                        shortage
                );

                if (availableQuantity == 0) {

                    missingIngredients.add(
                            requiredIngredient
                                    + " — Need "
                                    + requiredQuantity
                                    + ", have 0"
                    );

                } else {

                    missingIngredients.add(
                            requiredIngredient
                                    + " — Need "
                                    + requiredQuantity
                                    + ", have "
                                    + availableQuantity
                                    + " ("
                                    + shortage
                                    + " more needed)"
                    );
                }
            }
        }

        updateAvailabilityDisplay(
                missingIngredients.isEmpty()
        );
    }

    private PantryItem findPantryItem(
            String ingredientName
    ) {

        if (ingredientName == null) {
            return null;
        }

        String requiredName =
                normalizeName(
                        ingredientName
                );

        for (PantryItem pantryItem :
                pantryItems) {

            if (pantryItem == null
                    || pantryItem.getName() == null) {
                continue;
            }

            String pantryName =
                    normalizeName(
                            pantryItem.getName()
                    );

            if (pantryName.equals(requiredName)) {
                return pantryItem;
            }
        }

        return null;
    }

    private String normalizeName(
            String name
    ) {

        return name
                .trim()
                .toLowerCase(Locale.getDefault());
    }

    private void updateAvailabilityDisplay(
            boolean canMakeRecipe
    ) {

        missingIngredientsContainer
                .removeAllViews();

        if (canMakeRecipe) {

            recipeStatusText.setText(
                    "✓ You have everything needed"
            );

            recipeStatusText.setTextColor(
                    0xFF2F6B42
            );

            ingredientsSummary.setText(
                    "All required ingredients are available."
            );

            missingIngredientsCard.setVisibility(
                    View.GONE
            );

            addMissingToShoppingListButton
                    .setVisibility(
                            View.GONE
                    );

        } else {

            recipeStatusText.setText(
                    "✕ You do not have everything needed"
            );

            recipeStatusText.setTextColor(
                    0xFF8A3B3B
            );

            ingredientsSummary.setText(
                    "Some ingredients are missing or have insufficient quantity."
            );

            missingIngredientsCard.setVisibility(
                    View.VISIBLE
            );

            addMissingToShoppingListButton
                    .setVisibility(
                            View.VISIBLE
                    );

            for (String missing :
                    missingIngredients) {

                TextView missingText =
                        createMissingIngredientTextView(
                                missing
                        );

                missingIngredientsContainer
                        .addView(
                                missingText
                        );
            }
        }
    }

    private void addMissingIngredientsToShoppingList() {

        if (missingIngredientNames.isEmpty()) {

            Toast.makeText(
                    this,
                    "There are no missing ingredients to add.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        addMissingToShoppingListButton
                .setEnabled(false);

        addShoppingItemAtIndex(0);
    }

    private void addShoppingItemAtIndex(
            int index
    ) {

        if (index >= missingIngredientNames.size()) {

            addMissingToShoppingListButton
                    .setEnabled(true);

            Toast.makeText(
                    this,
                    "Missing ingredients added to shopping list.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        String ingredientName =
                missingIngredientNames.get(index);

        int quantity =
                missingIngredientQuantities.get(index);

        String shoppingItemId =
                shoppingReference.push().getKey();

        if (shoppingItemId == null) {

            addMissingToShoppingListButton
                    .setEnabled(true);

            Toast.makeText(
                    this,
                    "Could not create shopping list item.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        ShoppingItem shoppingItem =
                new ShoppingItem(
                        shoppingItemId,
                        ingredientName,
                        quantity,
                        false
                );

        shoppingReference
                .child(shoppingItemId)
                .setValue(shoppingItem)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        addShoppingItemAtIndex(
                                index + 1
                        );

                    } else {

                        addMissingToShoppingListButton
                                .setEnabled(true);

                        Toast.makeText(
                                RecipeDetailActivity.this,
                                "Could not add "
                                        + ingredientName
                                        + " to shopping list.",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private TextView createIngredientTextView(
            String text
    ) {

        TextView textView =
                createBodyTextView(text);

        textView.setPadding(
                0,
                10,
                0,
                10
        );

        return textView;
    }

    private TextView createMissingIngredientTextView(
            String text
    ) {

        TextView textView =
                createBodyTextView(
                        "• " + text
                );

        textView.setTextColor(
                0xFF8A3B3B
        );

        textView.setTypeface(
                null,
                Typeface.BOLD
        );

        textView.setPadding(
                0,
                8,
                0,
                8
        );

        return textView;
    }

    private TextView createInstructionTextView(
            String text
    ) {

        TextView textView =
                createBodyTextView(text);

        textView.setPadding(
                0,
                10,
                0,
                10
        );

        return textView;
    }

    private TextView createBodyTextView(
            String text
    ) {

        TextView textView =
                new TextView(this);

        textView.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                )
        );

        textView.setText(
                text
        );

        textView.setTextColor(
                0xFF27352B
        );

        textView.setTextSize(
                14
        );

        return textView;
    }
}