package com.example.android.reciped;

/**
 * @author Lyla
 * @since 7/22/15
 *
 * This is a POJO to represent ingreidents in the app
 *
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
