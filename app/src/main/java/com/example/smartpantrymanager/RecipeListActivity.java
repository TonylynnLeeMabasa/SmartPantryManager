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

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RecipeListActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private DatabaseReference pantryReference;

    private RecyclerView recipeRecyclerView;
    private RecipeAdapter recipeAdapter;

    private MaterialCardView emptyRecipeCard;

    private TextView recipeCountText;

    private MaterialButton allRecipesButton;
    private MaterialButton canMakeButton;

    private final List<Recipe> recipes =
            new ArrayList<>();

    private final List<Recipe> displayedRecipes =
            new ArrayList<>();

    private final List<PantryItem> pantryItems =
            new ArrayList<>();

    private RecipeMatcher recipeMatcher;

    private boolean showingCanMakeOnly = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_recipe_list
        );

        recipeRecyclerView =
                findViewById(
                        R.id.recipeRecyclerView
                );

        recipeRecyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recipeRecyclerView.setNestedScrollingEnabled(
                false
        );

        emptyRecipeCard =
                findViewById(
                        R.id.emptyRecipeCard
                );

        recipeCountText =
                findViewById(
                        R.id.recipeCountText
                );

        allRecipesButton =
                findViewById(
                        R.id.allRecipesButton
                );

        canMakeButton =
                findViewById(
                        R.id.canMakeButton
                );

        recipeMatcher =
                new RecipeMatcher();

        recipeAdapter =
                new RecipeAdapter(
                        displayedRecipes,
                        pantryItems,
                        recipeMatcher
                );

        recipeRecyclerView.setAdapter(
                recipeAdapter
        );

        databaseReference =
                FirebaseDatabase
                        .getInstance(
                                "https://smart-pantry-manager-7e502-default-rtdb.europe-west1.firebasedatabase.app/"
                        )
                        .getReference()
                        .child("recipes");

        pantryReference =
                FirebaseDatabase
                        .getInstance(
                                "https://smart-pantry-manager-7e502-default-rtdb.europe-west1.firebasedatabase.app/"
                        )
                        .getReference()
                        .child("pantry_items");

        TextView backButton =
                findViewById(
                        R.id.backButton
                );

        backButton.setOnClickListener(
                v -> finish()
        );

        allRecipesButton.setOnClickListener(
                v -> {

                    showingCanMakeOnly = false;

                    updateDisplayedRecipes();

                    updateFilterButtons();
                }
        );

        canMakeButton.setOnClickListener(
                v -> {

                    showingCanMakeOnly = true;

                    updateDisplayedRecipes();

                    updateFilterButtons();
                }
        );

        /*
         * Load the preloaded recipes.
         */
        loadRecipes();

        /*
         * Load the current pantry so recipes
         * can be matched against real quantities.
         */
        loadPantryItems();
    }

    private void loadRecipes() {

        databaseReference.addValueEventListener(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot
                    ) {

                        recipes.clear();

                        for (
                                DataSnapshot recipeSnapshot :
                                snapshot.getChildren()
                        ) {

                            Recipe recipe =
                                    recipeSnapshot.getValue(
                                            Recipe.class
                                    );

                            if (recipe == null) {
                                continue;
                            }

                            if (
                                    recipe.getId() == null
                                            || recipe.getId().isEmpty()
                            ) {

                                recipe.setId(
                                        recipeSnapshot.getKey()
                                );
                            }

                            recipes.add(recipe);
                        }

                        updateDisplayedRecipes();
                    }

                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error
                    ) {

                        Toast.makeText(
                                RecipeListActivity.this,
                                "Could not load recipes: "
                                        + error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    private void loadPantryItems() {

        pantryReference.addValueEventListener(
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

                            if (
                                    item.getId() == null
                                            || item.getId().isEmpty()
                            ) {

                                item.setId(
                                        itemSnapshot.getKey()
                                );
                            }

                            pantryItems.add(item);
                        }

                        /*
                         * Recalculate the displayed recipes
                         * because pantry quantities may have changed.
                         */
                        updateDisplayedRecipes();
                    }

                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error
                    ) {

                        Toast.makeText(
                                RecipeListActivity.this,
                                "Could not load pantry data: "
                                        + error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    private void updateDisplayedRecipes() {

        displayedRecipes.clear();

        for (Recipe recipe : recipes) {

            RecipeMatcher.MatchResult result =
                    recipeMatcher.checkRecipe(
                            recipe,
                            pantryItems
                    );

            if (!showingCanMakeOnly
                    || result.canMake()) {

                displayedRecipes.add(
                        recipe
                );
            }
        }

        recipeAdapter.notifyDataSetChanged();

        updateRecipeVisibility();

        updateFilterButtons();
    }

    private void updateRecipeVisibility() {

        int recipeCount =
                displayedRecipes.size();

        recipeCountText.setText(
                recipeCount
                        + (recipeCount == 1
                        ? " recipe"
                        : " recipes")
        );

        if (displayedRecipes.isEmpty()) {

            recipeRecyclerView.setVisibility(
                    View.GONE
            );

            emptyRecipeCard.setVisibility(
                    View.VISIBLE
            );

        } else {

            recipeRecyclerView.setVisibility(
                    View.VISIBLE
            );

            emptyRecipeCard.setVisibility(
                    View.GONE
            );
        }
    }

    private void updateFilterButtons() {

        if (showingCanMakeOnly) {

            allRecipesButton.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            0xFFFFFFFF
                    )
            );

            allRecipesButton.setTextColor(
                    0xFF2F6B42
            );

            canMakeButton.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            0xFF2F6B42
                    )
            );

            canMakeButton.setTextColor(
                    0xFFFFFFFF
            );

        } else {

            allRecipesButton.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            0xFF2F6B42
                    )
            );

            allRecipesButton.setTextColor(
                    0xFFFFFFFF
            );

            canMakeButton.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            0xFFFFFFFF
                    )
            );

            canMakeButton.setTextColor(
                    0xFF2F6B42
            );
        }
    }
}