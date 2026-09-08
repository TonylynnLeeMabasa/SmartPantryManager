package com.example.smartpantrymanager;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecipeAdapter
        extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private final List<Recipe> recipes;

    public RecipeAdapter(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.item_recipe,
                        parent,
                        false
                );

        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull RecipeViewHolder holder,
            int position
    ) {

        Recipe recipe = recipes.get(position);

        holder.recipeName.setText(
                recipe.getName()
        );

        holder.recipeDescription.setText(
                recipe.getDescription()
        );

        holder.recipeMatchStatus.setText(
                "Tap to view recipe"
        );

        holder.itemView.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            v.getContext(),
                            RecipeDetailActivity.class
                    );

            intent.putExtra(
                    "recipeId",
                    recipe.getId()
            );

            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public static class RecipeViewHolder
            extends RecyclerView.ViewHolder {

        TextView recipeName;
        TextView recipeDescription;
        TextView recipeMatchStatus;

        public RecipeViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);

            recipeName =
                    itemView.findViewById(
                            R.id.recipeName
                    );

            recipeDescription =
                    itemView.findViewById(
                            R.id.recipeDescription
                    );

            recipeMatchStatus =
                    itemView.findViewById(
                            R.id.recipeMatchStatus
                    );
        }
    }
}