package com.example.smartpantrymanager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecipeMatcher {

    public static class MatchResult {

        private final boolean canMake;
        private final List<String> missingIngredients;

        public MatchResult(
                boolean canMake,
                List<String> missingIngredients
        ) {
            this.canMake = canMake;
            this.missingIngredients = missingIngredients;
        }

        public boolean canMake() {
            return canMake;
        }

        public List<String> getMissingIngredients() {
            return missingIngredients;
        }
    }

    public MatchResult checkRecipe(
            Recipe recipe,
            List<PantryItem> pantryItems
    ) {

        List<String> missingIngredients =
                new ArrayList<>();

        if (recipe == null) {
            return new MatchResult(
                    false,
                    missingIngredients
            );
        }

        List<String> ingredients =
                recipe.getIngredients();

        List<Integer> quantities =
                recipe.getQuantities();

        if (ingredients == null
                || ingredients.isEmpty()) {

            return new MatchResult(
                    true,
                    missingIngredients
            );
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
                            requiredIngredient,
                            pantryItems
                    );

            int availableQuantity = 0;

            if (pantryItem != null) {
                availableQuantity =
                        pantryItem.getQuantity();
            }

            // The recipe can only be made when enough
            // of every required ingredient is available.
            if (availableQuantity
                    < requiredQuantity) {

                if (availableQuantity == 0) {

                    missingIngredients.add(
                            requiredIngredient
                                    + " — Need "
                                    + requiredQuantity
                                    + ", have 0"
                    );

                } else {

                    int shortage =
                            requiredQuantity
                                    - availableQuantity;

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

        return new MatchResult(
                missingIngredients.isEmpty(),
                missingIngredients
        );
    }

    private PantryItem findPantryItem(
            String ingredientName,
            List<PantryItem> pantryItems
    ) {

        if (ingredientName == null
                || pantryItems == null) {

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

            if (areIngredientNamesEquivalent(
                    pantryName,
                    requiredName
            )) {
                return pantryItem;
            }
        }

        return null;
    }

    private boolean areIngredientNamesEquivalent(
            String pantryName,
            String requiredName
    ) {

        if (pantryName.equals(requiredName)) {
            return true;
        }

        // Handle common singular and plural forms.
        return removePluralEnding(pantryName)
                .equals(
                        removePluralEnding(requiredName)
                );
    }

    private String removePluralEnding(
            String name
    ) {

        if (name.length() <= 3) {
            return name;
        }

        // Handle words ending in "ies", such as berries.
        if (name.endsWith("ies")
                && name.length() > 3) {

            return name.substring(
                    0,
                    name.length() - 3
            ) + "y";
        }

        // Handle common words ending in "es".
        if (name.endsWith("es")
                && name.length() > 4) {

            return name.substring(
                    0,
                    name.length() - 2
            );
        }

        // Handle normal plural words ending in "s".
        if (name.endsWith("s")
                && name.length() > 3) {

            return name.substring(
                    0,
                    name.length() - 1
            );
        }

        return name;
    }

    private String normalizeName(
            String name
    ) {

        return name
                .trim()
                .toLowerCase(Locale.getDefault());
    }
}