package com.example.android.reciped;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lyla on 7/17/15.
 */
public class Recipe {
    private String owner;
    private String instructions;
    private String name;
    private List<Ingredient> ingredients = new ArrayList<>();
    public static final String FIREBASE_RECIPE_PATH = "recipe";


    public Recipe() {
    }

    public Recipe(String instructions, String name, String owner) {
        this.instructions = instructions;
        this.name = name;
        this.owner = owner;
    }

    public void addIngredient(String name, int amount) {
        ingredients.add(new Ingredient(name, amount));
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getInstructions() {
        return instructions;
    }

    public String getName() {
        return name;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }
}
