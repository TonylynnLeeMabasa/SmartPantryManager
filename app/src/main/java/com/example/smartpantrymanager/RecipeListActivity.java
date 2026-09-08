package com.example.smartpantrymanager;

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

    private RecyclerView recipeRecyclerView;
    private RecipeAdapter recipeAdapter;

    private MaterialCardView emptyRecipeCard;

    private TextView recipeCountText;

    private final List<Recipe> recipes =
            new ArrayList<>();

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

        recipeAdapter =
                new RecipeAdapter(recipes);

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

        TextView backButton =
                findViewById(
                        R.id.backButton
                );

        backButton.setOnClickListener(
                v -> finish()
        );

        /*
         * Seed the database with the 20
         * preloaded recipes if the recipes
         * collection is empty.
         */
        RecipeSeeder recipeSeeder =
                new RecipeSeeder();

        recipeSeeder.seedRecipes(this);

        loadRecipes();
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