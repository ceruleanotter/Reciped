package com.example.android.reciped;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.client.Firebase;

import butterknife.Bind;
import butterknife.ButterKnife;


public class RecipeDetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = RecipeDetailActivity.class.getSimpleName();
    @Bind(R.id.recipe_instructions)
    TextView mInstructionsTextView;

    @Bind(R.id.recipe_name)
    TextView mNameTextview;

    @Bind(R.id.list_ingredients)
    LinearLayout mIngredientsLinearLayout;


    public static final String USERNAME_EXTRA = "username_extra";
    public static final String RECIPE_ID_EXTRA = "recipe_id";



    Firebase mFirebaseRecipeRef;
    LayoutInflater mInflater;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        ButterKnife.bind(this);
        mFirebaseRecipeRef = new Firebase(RecipeListActivity.FIREBASE_URL + Recipe.FIREBASE_RECIPE_PATH);
        mInflater = LayoutInflater.from(this);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipe_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onSaveClicked(View view) {
        saveData();
    }

    public void onAddNewIngredient(View view) {
        View inflatedLayout= mInflater.inflate(R.layout.item_ingredient, null, false);
        mIngredientsLinearLayout.addView(inflatedLayout, mIngredientsLinearLayout.indexOfChild(view));

    }

    public void onRemoveIngredient(View view) {
        mIngredientsLinearLayout.removeView((View)view.getParent().getParent());
        Log.e(LOG_TAG, "Removed the view " + view.toString());
    }

    private void saveData() {
        Firebase thisRecipeRef = mFirebaseRecipeRef.push();


        Recipe currentRecipe = new Recipe(mInstructionsTextView.getText().toString(),
                mNameTextview.getText().toString(),
                mFirebaseRecipeRef.getAuth().getUid());

        //Subtract 1 for the + button
        for (int i = 0; i < mIngredientsLinearLayout.getChildCount()-1; i++) {
            View currentRow =mIngredientsLinearLayout.getChildAt(i);



            String currentName = getStringFromView(currentRow, R.id.ingredient_name);
            String currentAmountString = getStringFromView(currentRow, R.id.ingredient_amount);
            if (!currentName.isEmpty() && ! currentAmountString.isEmpty()) {
                int currentAmount = Integer.parseInt(currentAmountString);
                currentRecipe.addIngredient(currentName, currentAmount);
            } else {
                Log.e(LOG_TAG, "Current name: " + currentName + " currentAmount: " + currentAmountString + " is empty");
            }

        }

        thisRecipeRef.setValue(currentRecipe);

        Intent returnIntent = new Intent();
        returnIntent.putExtra(RECIPE_ID_EXTRA, thisRecipeRef.getKey());
        setResult(RESULT_OK, returnIntent);
        finish();
    }


    private String getStringFromView(View v, int id) {
        return ((TextView) v.findViewById(id)).getText().toString().trim();
    }



}
