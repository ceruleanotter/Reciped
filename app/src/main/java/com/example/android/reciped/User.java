package com.example.android.reciped;

import java.util.HashMap;

/**
 * Created by lyla on 7/17/15.
 */
public class User {
    private String email;
    private String uid;
    private HashMap<String, Boolean> ownedRecipes;


    public User() {
    }

    public User(String email, HashMap<String, Boolean> ownedRecipes, String uid) {
        this.email = email;
        this.ownedRecipes = ownedRecipes;
        this.uid = uid;
    }

    public void addOwnedRecipe(String recipeId){
        //r.setOwner(uid);
        ownedRecipes.put(recipeId, Boolean.TRUE);
    }


    public String getEmail() {
        return email;
    }

    public HashMap<String, Boolean> getOwnedRecipes() {
        return ownedRecipes;
    }

    public String getUid() {
        return uid;
    }
}
