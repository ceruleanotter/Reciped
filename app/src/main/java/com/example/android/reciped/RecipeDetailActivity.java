package com.example.android.reciped;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.Firebase;

import butterknife.Bind;
import butterknife.ButterKnife;


public class RecipeDetailActivity extends AppCompatActivity {

    @Bind(R.id.recipe_instructions)
    TextView mInstructionsTextView;

    @Bind(R.id.recipe_name)
    TextView mNameTextview;


    public static final String USERNAME_EXTRA = "username_extra";
    public static final String RECIPE_ID_EXTRA = "recipe_id";



    Firebase mFirebaseRecipeRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        ButterKnife.bind(this);
        mFirebaseRecipeRef = new Firebase(RecipeListActivity.FIREBASE_URL + RecipeListActivity.FIREBASE_RECIPE_PATH);

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

    private void saveData() {
        Firebase thisRecipeRef = mFirebaseRecipeRef.push();

        thisRecipeRef.setValue(
                new Recipe(mInstructionsTextView.getText().toString(),
                        mNameTextview.getText().toString(),
                        "idlyla")
        );

        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", thisRecipeRef.getKey() );
        setResult(RESULT_OK,returnIntent);
        finish();
    }
}
