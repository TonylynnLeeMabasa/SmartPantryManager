package com.example.smartpantrymanager;

import java.util.List;

public class Recipe {

    private String id;
    private String name;
    private String description;
    private List<String> ingredients;
    private List<Integer> quantities;
    private List<String> instructions;

    public Recipe() {
        // Required empty constructor for Firebase
    }

    public Recipe(
            String id,
            String name,
            String description,
            List<String> ingredients,
            List<Integer> quantities,
            List<String> instructions
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ingredients = ingredients;
        this.quantities = quantities;
        this.instructions = instructions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<Integer> getQuantities() {
        return quantities;
    }

    public void setQuantities(List<Integer> quantities) {
        this.quantities = quantities;
    }

    public List<String> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<String> instructions) {
        this.instructions = instructions;
    }
}