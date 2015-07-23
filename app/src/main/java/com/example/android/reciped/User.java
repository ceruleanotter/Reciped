package com.example.android.reciped;

import java.util.HashMap;

/**
 * Created by lyla on 7/17/15.
 */
public class User {
    private String email;
    private String uid;
    private HashMap<String, Boolean> recipeBook;
    public static final String FIREBASE_USER_PATH = "user";
    public static final String FIREBASE_RECIPE_BOOK_PATH = "recipeBook";

    public User() {
    }

    public User(String email,  String uid) {
        this.email = email;
        this.recipeBook = new HashMap<String, Boolean>();
        this.uid = uid;
    }

    /*public void addOwnedRecipe(String recipeId){
        //r.setOwner(uid);
        ownedRecipes.put(recipeId, Boolean.TRUE);
    }*/


    public String getEmail() {
        return email;
    }

    public HashMap<String, Boolean> getRecipeBook() {
        return recipeBook;
    }

    public String getUid() {
        return uid;
    }
}
