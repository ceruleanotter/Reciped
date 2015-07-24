package com.example.android.reciped;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import butterknife.Bind;
import butterknife.ButterKnife;


public class RecipeDetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = RecipeDetailActivity.class.getSimpleName();
    @Bind(R.id.recipe_instructions)
    TextView mInstructionsTextView;

    @Bind(R.id.recipe_name)
    TextView mNameTextview;

    @Bind(R.id.recipe_owner_email)
    TextView mOwnerEmailTextView;

    @Bind(R.id.list_ingredients)
    LinearLayout mIngredientsLinearLayout;


    public static final String USERNAME_EXTRA = "username_extra";
    public static final String RECIPE_ID_EXTRA = "recipe_id";
    public static final String HAS_EDIT_PERMISSION = "edit_permission";

    private Firebase mFirebaseRecipeRef;
    private Firebase mFirebaseRecipeRefSpecificRef;
    private LayoutInflater mInflater;
    private String mUserEmail;
    private String mKey = null;

    private int mIngredientLayout;

    private boolean mEditPermission;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** setting view to appropriate permissions **/
        mEditPermission = (getIntent().getBooleanExtra(HAS_EDIT_PERMISSION, false));

        if (mEditPermission) {
            mIngredientLayout = R.layout.item_ingredient;
            setContentView(R.layout.activity_recipe_detail);
        } else {
            mIngredientLayout = R.layout.item_ingredient_read_only;
            setContentView(R.layout.activity_recipe_detail_read_only);
        }

        ButterKnife.bind(this);
        mFirebaseRecipeRef = new Firebase(RecipeListActivity.FIREBASE_URL + Recipe.FIREBASE_RECIPE_PATH);
        mFirebaseRecipeRefSpecificRef = null;
        mInflater = LayoutInflater.from(this);
        mUserEmail = getIntent().getStringExtra(USERNAME_EXTRA);


        if (getIntent().hasExtra(RECIPE_ID_EXTRA)) {
            mKey = getIntent().getStringExtra(RECIPE_ID_EXTRA);
            populateViewFromData(mKey);
        } else {
            addIngredient(null);
        }







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
        addIngredient(null);

    }

    public void addIngredient(Ingredient i) {
        View inflatedLayout = mInflater.inflate(mIngredientLayout, null, false);
        if (i != null) {
            ((TextView) inflatedLayout.findViewById(R.id.ingredient_name)).setText(i.getName());
            ((TextView) inflatedLayout.findViewById(R.id.ingredient_amount)).setText(i.getAmount() + "");
        }
        mIngredientsLinearLayout.addView(inflatedLayout, mIngredientsLinearLayout.getChildCount() - 1);
    }


    public void onRemoveIngredient(View view) {
        mIngredientsLinearLayout.removeView((View) view.getParent().getParent());
        Log.e(LOG_TAG, "Removed the view " + view.toString());
    }

    private void saveData() {
        final Firebase thisRecipeRef;
        boolean saveSuccess = true;
        if (mKey == null) {
            thisRecipeRef = mFirebaseRecipeRef.push();
        } else {
            thisRecipeRef = mFirebaseRecipeRef.child(mKey);
        }

        Recipe currentRecipe = new Recipe(mInstructionsTextView.getText().toString(),
                mNameTextview.getText().toString(),
                mUserEmail, mFirebaseRecipeRef.getAuth().getUid());

        //Subtract 1 for the + button
        for (int i = 0; i < mIngredientsLinearLayout.getChildCount() - 1; i++) {
            View currentRow = mIngredientsLinearLayout.getChildAt(i);


            String currentName = getStringFromView(currentRow, R.id.ingredient_name);
            String currentAmountString = getStringFromView(currentRow, R.id.ingredient_amount);



            if (!currentName.isEmpty() && !currentAmountString.isEmpty()) {
                int currentAmount = 0;
                try {
                    currentAmount = Integer.parseInt(currentAmountString);
                } catch (NumberFormatException e) {
                    saveSuccess = false;
                    ((EditText)currentRow.findViewById(R.id.ingredient_amount)).setError("There's something wrong with this number. It might be too large or not a number.");
                }
                currentRecipe.addIngredient(currentName, currentAmount);
            } else {
                Log.e(LOG_TAG, "Current name: " + currentName + " currentAmount: " + currentAmountString + " is empty");
            }

        }
        if (saveSuccess) {
            thisRecipeRef.setValue(currentRecipe, new Firebase.CompletionListener() {
                @Override
                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                    if (firebaseError != null) {
                        switch (firebaseError.getCode()) {
                            case FirebaseError.PERMISSION_DENIED :
                                mNameTextview.setError("You don't have permission to edit this recipe");
                        }

                    } else {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra(RECIPE_ID_EXTRA, thisRecipeRef.getKey());
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    }
                }
            });

        }
    }


    private String getStringFromView(View v, int id) {
        return ((TextView) v.findViewById(id)).getText().toString().trim();
    }

    private void populateViewFromData(String key) {
        mFirebaseRecipeRefSpecificRef = mFirebaseRecipeRef.child(key);
        mFirebaseRecipeRefSpecificRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Recipe r = dataSnapshot.getValue(Recipe.class);
                mInstructionsTextView.setText(r.getInstructions());
                mOwnerEmailTextView.setText(r.getOwnerEmail());
                mNameTextview.setText(r.getName());
                for (Ingredient currentIngredient : r.getIngredients()) {
                    addIngredient(currentIngredient);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


}
