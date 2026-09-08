package com.example.smartpantrymanager;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class RecipeSeeder {

    private final DatabaseReference databaseReference;

    public RecipeSeeder() {

        databaseReference = FirebaseDatabase
                .getInstance(
                        "https://smart-pantry-manager-7e502-default-rtdb.europe-west1.firebasedatabase.app/"
                )
                .getReference()
                .child("recipes");
    }

    public void seedRecipes(Context context) {

        databaseReference.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(
                            DataSnapshot snapshot
                    ) {

                        if (snapshot.exists()
                                && snapshot.getChildrenCount() > 0) {

                            return;
                        }

                        addRecipes(context);
                    }

                    @Override
                    public void onCancelled(
                            DatabaseError error
                    ) {

                        Toast.makeText(
                                context,
                                "Could not check recipes: "
                                        + error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    private void addRecipes(Context context) {

        List<Recipe> recipes = Arrays.asList(

                new Recipe(
                        "recipe_01",
                        "Chicken Fried Rice",
                        "A quick and tasty rice meal.",
                        Arrays.asList(
                                "Rice",
                                "Chicken",
                                "Eggs",
                                "Carrots"
                        ),
                        Arrays.asList(
                                2,
                                1,
                                2,
                                1
                        ),
                        Arrays.asList(
                                "Cook the rice and allow it to cool.",
                                "Cook the chicken until fully cooked.",
                                "Scramble the eggs in a pan.",
                                "Add rice, chicken and carrots.",
                                "Stir-fry everything together and serve."
                        )
                ),

                new Recipe(
                        "recipe_02",
                        "Spaghetti Bolognese",
                        "Classic spaghetti with a rich meat sauce.",
                        Arrays.asList(
                                "Spaghetti",
                                "Minced Meat",
                                "Tomato Sauce",
                                "Onion"
                        ),
                        Arrays.asList(
                                1,
                                1,
                                1,
                                1
                        ),
                        Arrays.asList(
                                "Cook the spaghetti according to the package instructions.",
                                "Fry the onion until soft.",
                                "Add the minced meat and cook thoroughly.",
                                "Add tomato sauce and simmer.",
                                "Serve the sauce over the spaghetti."
                        )
                ),

                new Recipe(
                        "recipe_03",
                        "Chicken Sandwich",
                        "A simple chicken sandwich for a quick meal.",
                        Arrays.asList(
                                "Bread",
                                "Chicken",
                                "Tomato",
                                "Lettuce"
                        ),
                        Arrays.asList(
                                2,
                                1,
                                1,
                                1
                        ),
                        Arrays.asList(
                                "Cook the chicken thoroughly.",
                                "Slice the tomato and lettuce.",
                                "Place chicken and vegetables between bread.",
                                "Serve immediately."
                        )
                ),

                new Recipe(
                        "recipe_04",
                        "Vegetable Rice",
                        "A simple rice dish packed with vegetables.",
                        Arrays.asList(
                                "Rice",
                                "Carrots",
                                "Peas",
                                "Onion"
                        ),
                        Arrays.asList(
                                2,
                                1,
                                1,
                                1
                        ),
                        Arrays.asList(
                                "Cook the rice.",
                                "Chop the vegetables.",
                                "Fry the onion and vegetables.",
                                "Add the cooked rice.",
                                "Mix well and serve."
                        )
                ),

                new Recipe(
                        "recipe_05",
                        "Beef Burger",
                        "A homemade burger with beef and fresh vegetables.",
                        Arrays.asList(
                                "Burger Buns",
                                "Minced Meat",
                                "Tomato",
                                "Lettuce"
                        ),
                        Arrays.asList(
                                1,
                                1,
                                1,
                                1
                        ),
                        Arrays.asList(
                                "Shape the minced meat into a burger patty.",
                                "Cook the patty thoroughly.",
                                "Slice the vegetables.",
                                "Place everything inside the burger bun.",
                                "Serve."
                        )
                ),

                new Recipe(
                        "recipe_06",
                        "Egg Toast",
                        "Crispy toast topped with scrambled eggs.",
                        Arrays.asList(
                                "Bread",
                                "Eggs",
                                "Butter"
                        ),
                        Arrays.asList(
                                2,
                                2,
                                1
                        ),
                        Arrays.asList(
                                "Toast the bread.",
                                "Melt butter in a pan.",
                                "Scramble the eggs.",
                                "Place the eggs on the toast.",
                                "Serve warm."
                        )
                ),

                new Recipe(
                        "recipe_07",
                        "Chicken Pasta",
                        "Creamy and filling chicken pasta.",
                        Arrays.asList(
                                "Pasta",
                                "Chicken",
                                "Milk",
                                "Cheese"
                        ),
                        Arrays.asList(
                                1,
                                1,
                                1,
                                1
                        ),
                        Arrays.asList(
                                "Cook the pasta.",
                                "Cook the chicken thoroughly.",
                                "Heat the milk and add cheese.",
                                "Add chicken and pasta.",
                                "Mix well and serve."
                        )
                ),

                new Recipe(
                        "recipe_08",
                        "Tuna Sandwich",
                        "A quick tuna sandwich for lunch.",
                        Arrays.asList(
                                "Bread",
                                "Tuna",
                                "Mayonnaise",
                                "Lettuce"
                        ),
                        Arrays.asList(
                                2,
                                1,
                                1,
                                1
                        ),
                        Arrays.asList(
                                "Drain the tuna.",
                                "Mix tuna with mayonnaise.",
                                "Add lettuce.",
                                "Spread the mixture onto bread.",
                                "Serve."
                        )
                ),

                new Recipe(
                        "recipe_09",
                        "Pancakes",
                        "Soft homemade pancakes for breakfast.",
                        Arrays.asList(
                                "Flour",
                                "Milk",
                                "Eggs",
                                "Sugar"
                        ),
                        Arrays.asList(
                                2,
                                1,
                                2,
                                1
                        ),
                        Arrays.asList(
                                "Mix flour, milk, eggs and sugar.",
                                "Whisk until smooth.",
                                "Heat a lightly greased pan.",
                                "Pour batter into the pan.",
                                "Cook both sides and serve."
                        )
                ),

                new Recipe(
                        "recipe_10",
                        "Cheese Omelette",
                        "A fluffy omelette filled with cheese.",
                        Arrays.asList(
                                "Eggs",
                                "Cheese",
                                "Butter"
                        ),
                        Arrays.asList(
                                3,
                                1,
                                1
                        ),
                        Arrays.asList(
                                "Beat the eggs.",
                                "Melt butter in a pan.",
                                "Pour in the eggs.",
                                "Add cheese.",
                                "Fold the omelette and serve."
                        )
                ),

                new Recipe(
                        "recipe_11",
                        "Chicken Salad",
                        "A fresh salad with cooked chicken.",
                        Arrays.asList(
                                "Chicken",
                                "Lettuce",
                                "Tomato",
                                "Cucumber"
                        ),
                        Arrays.asList(
                                1,
                                1,
                                1,
                                1
                        ),
                        Arrays.asList(
                                "Cook the chicken thoroughly.",
                                "Chop the vegetables.",
                                "Combine chicken and vegetables.",
                                "Mix well and serve."
                        )
                ),

                new Recipe(
                        "recipe_12",
                        "Tomato Pasta",
                        "Simple pasta with a fresh tomato sauce.",
                        Arrays.asList(
                                "Pasta",
                                "Tomato",
                                "Onion",
                                "Tomato Sauce"
                        ),
                        Arrays.asList(
                                1,
                                2,
                                1,
                                1
                        ),
                        Arrays.asList(
                                "Cook the pasta.",
                                "Chop the tomato and onion.",
                                "Cook the onion and tomato.",
                                "Add tomato sauce.",
                                "Mix with pasta and serve."
                        )
                ),

                new Recipe(
                        "recipe_13",
                        "Beef Rice Bowl",
                        "A filling beef and rice meal.",
                        Arrays.asList(
                                "Rice",
                                "Minced Meat",
                                "Onion",
                                "Carrots"
                        ),
                        Arrays.asList(
                                2,
                                1,
                                1,
                                1
                        ),
                        Arrays.asList(
                                "Cook the rice.",
                                "Fry the onion.",
                                "Add minced meat and cook thoroughly.",
                                "Add carrots.",
                                "Serve over rice."
                        )
                ),

                new Recipe(
                        "recipe_14",
                        "French Toast",
                        "Golden bread dipped in an egg mixture.",
                        Arrays.asList(
                                "Bread",
                                "Eggs",
                                "Milk",
                                "Sugar"
                        ),
                        Arrays.asList(
                                2,
                                2,
                                1,
                                1
                        ),
                        Arrays.asList(
                                "Whisk eggs, milk and sugar.",
                                "Dip the bread into the mixture.",
                                "Heat a pan.",
                                "Cook both sides until golden.",
                                "Serve warm."
                        )
                ),

                new Recipe(
                        "recipe_15",
                        "Chicken Wrap",
                        "A tasty chicken wrap with fresh vegetables.",
                        Arrays.asList(
                                "Wraps",
                                "Chicken",
                                "Lettuce",
                                "Tomato"
                        ),
                        Arrays.asList(
                                1,
                                1,
                                1,
                                1
                        ),
                        Arrays.asList(
                                "Cook the chicken thoroughly.",
                                "Slice the vegetables.",
                                "Place everything inside a wrap.",
                                "Roll the wrap tightly.",
                                "Serve."
                        )
                ),

                new Recipe(
                        "recipe_16",
                        "Vegetable Omelette",
                        "Egg omelette filled with fresh vegetables.",
                        Arrays.asList(
                                "Eggs",
                                "Carrots",
                                "Onion",
                                "Tomato"
                        ),
                        Arrays.asList(
                                3,
                                1,
                                1,
                                1
                        ),
                        Arrays.asList(
                                "Beat the eggs.",
                                "Chop the vegetables.",
                                "Fry the vegetables lightly.",
                                "Add the eggs.",
                                "Cook until set and serve."
                        )
                ),

                new Recipe(
                        "recipe_17",
                        "Cheesy Pasta",
                        "Creamy pasta loaded with cheese.",
                        Arrays.asList(
                                "Pasta",
                                "Cheese",
                                "Milk",
                                "Butter"
                        ),
                        Arrays.asList(
                                1,
                                2,
                                1,
                                1
                        ),
                        Arrays.asList(
                                "Cook the pasta.",
                                "Melt butter in a pan.",
                                "Add milk and cheese.",
                                "Stir until creamy.",
                                "Mix in pasta and serve."
                        )
                ),

                new Recipe(
                        "recipe_18",
                        "Tuna Pasta",
                        "Easy pasta with tuna and tomato sauce.",
                        Arrays.asList(
                                "Pasta",
                                "Tuna",
                                "Tomato Sauce",
                                "Onion"
                        ),
                        Arrays.asList(
                                1,
                                1,
                                1,
                                1
                        ),
                        Arrays.asList(
                                "Cook the pasta.",
                                "Fry the onion.",
                                "Add tuna and tomato sauce.",
                                "Add the cooked pasta.",
                                "Mix well and serve."
                        )
                ),

                new Recipe(
                        "recipe_19",
                        "Chicken Rice Bowl",
                        "A simple bowl of chicken, rice and vegetables.",
                        Arrays.asList(
                                "Rice",
                                "Chicken",
                                "Carrots",
                                "Peas"
                        ),
                        Arrays.asList(
                                2,
                                1,
                                1,
                                1
                        ),
                        Arrays.asList(
                                "Cook the rice.",
                                "Cook the chicken thoroughly.",
                                "Cook the vegetables.",
                                "Combine all ingredients.",
                                "Serve warm."
                        )
                ),

                new Recipe(
                        "recipe_20",
                        "Breakfast Scramble",
                        "A hearty breakfast made with eggs and vegetables.",
                        Arrays.asList(
                                "Eggs",
                                "Bread",
                                "Tomato",
                                "Onion"
                        ),
                        Arrays.asList(
                                3,
                                2,
                                1,
                                1
                        ),
                        Arrays.asList(
                                "Chop the tomato and onion.",
                                "Fry the vegetables.",
                                "Add beaten eggs.",
                                "Cook until the eggs are set.",
                                "Serve with toasted bread."
                        )
                )
        );

        for (Recipe recipe : recipes) {

            databaseReference
                    .child(recipe.getId())
                    .setValue(recipe);
        }

        Toast.makeText(
                context,
                "20 recipes added.",
                Toast.LENGTH_SHORT
        ).show();
    }
}