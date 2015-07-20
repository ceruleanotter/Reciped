package com.example.android.reciped;

/**
 * Created by lyla on 7/17/15.
 */
public class Recipe {
    private String owner;
    private String instructions;
    private String name;


    public Recipe() {
    }

    public Recipe(String instructions, String name, String owner) {
        this.instructions = instructions;
        this.name = name;
        this.owner = owner;
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
}
