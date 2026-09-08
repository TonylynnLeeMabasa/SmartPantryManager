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

    private final List<Recipe> recipes =
            new ArrayList<>();

    private final List<PantryItem> pantryItems =
            new ArrayList<>();

    private RecipeMatcher recipeMatcher;

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
                true
        );

        emptyRecipeCard =
                findViewById(
                        R.id.emptyRecipeCard
                );

        recipeCountText =
                findViewById(
                        R.id.recipeCountText
                );

        recipeMatcher =
                new RecipeMatcher();

        recipeAdapter =
                new RecipeAdapter(
                        recipes,
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

        loadRecipes();

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

                        recipeAdapter.notifyDataSetChanged();

                        updateRecipeVisibility();
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

                        recipeAdapter.notifyDataSetChanged();
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

    private void updateRecipeVisibility() {

        int recipeCount =
                recipes.size();

        recipeCountText.setText(
                recipeCount
                        + (recipeCount == 1
                        ? " recipe"
                        : " recipes")
        );

        if (recipes.isEmpty()) {

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
}