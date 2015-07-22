package com.example.android.reciped;

/**
 * Created by lyla on 7/22/15.
 */
public class Ingredient {
    String name;
    int amount;

    public Ingredient() {
    }

    public Ingredient(String name, int amount) {
        this.name = name;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }
}
