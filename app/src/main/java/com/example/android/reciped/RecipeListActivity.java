package com.example.android.reciped;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.Firebase;

import java.util.HashMap;


public class RecipeListActivity extends ListActivity {

    private Firebase mFirebaseRef;
    private User mUser;
    private FirebaseListAdapter mListAdapter;


    public static final String LOG_TAG = RecipeListActivity.class.getSimpleName();


    public static final String FIREBASE_URL = "https://reciped.firebaseio.com/";
    public static final String FIREBASE_RECIPE_PATH = "recipe";




    private static final int NEW_RECIPE_REQUEST = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_recipe_list);
        mFirebaseRef = new Firebase(FIREBASE_URL);
        mUser = new User("lyla.fujiwara@gmail.com", new HashMap<String, Boolean>(), "-1");

        mListAdapter = new FirebaseListAdapter<Recipe>(mFirebaseRef.child(FIREBASE_RECIPE_PATH), Recipe.class,
                R.layout.item_recipe, this) {
            @Override
            protected void populateView(View v, Recipe model) {
                ((TextView)v.findViewById(R.id.recipe_instructions)).setText(model.getInstructions());
                ((TextView)v.findViewById(R.id.recipe_name)).setText(model.getName());
            }
        };
        setListAdapter(mListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void addNewRecipe(View v) {
        Intent i = new Intent(this, RecipeDetailActivity.class);
        i.putExtra(RecipeDetailActivity.USERNAME_EXTRA, mUser.getUid());
        startActivityForResult(i, NEW_RECIPE_REQUEST);// get what's added and add it

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == NEW_RECIPE_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                Log.e(LOG_TAG, "Just added a recipe with id " + data.getStringExtra("result"));
                

                /*String id = data.getStringExtra(RecipeDetailActivity.RECIPE_ID_EXTRA);
                Firebase ref = new Firebase("https://reciped.firebaseio.com/");
                mUser.addOwnedRecipe();
                //we can grab from the db given the id...and then add to the user*/
            }
        }
    }


}
