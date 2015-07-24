package com.example.android.reciped;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lyla on 7/17/15.
 */
public class Recipe {
    private String ownerEmail;
    private String ownerUid;
    private String instructions;
    private String name;
    private String lastViewed;
    private List<Ingredient> ingredients = new ArrayList<>();
    private HashMap<String, Boolean> canView;

    public static final String OWNER_EMAIL_PATH = "ownerEmail";
    public static final String LAST_VIEWED_PATH = "lastViewed";
    public static final String FIREBASE_RECIPE_PATH = "recipe";
    public static final String FIREBASE_CAN_VIEW_PATH = "canView";


    public Recipe() {
    }

    public Recipe(String instructions, String name, String ownerEmail, String ownerUid) {
        this.instructions = instructions;
        this.name = name;
        this.ownerEmail = ownerEmail;
        this.ownerUid = ownerUid;
        this.canView = new HashMap<String, Boolean>();
    }

    public void addIngredient(String name, int amount) {
        ingredients.add(new Ingredient(name, amount));
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public String getOwnerUid() {
        return ownerUid;
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

    public String getLastViewed() {
        return lastViewed;
    }

    public HashMap<String, Boolean> getCanView() {
        return canView;
    }
}
