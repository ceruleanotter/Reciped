package com.example.android.reciped;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import butterknife.Bind;
import butterknife.ButterKnife;


public class RecipeListActivity extends ListActivity {


    /**
     * Views *
     */
    @Bind(R.id.login_button)
    Button mLoginButton;


    private User mUser;
    private FirebaseListAdapter<Recipe> mListAdapter;
    private Firebase.AuthStateListener mAuthStateListener;
    private ValueEventListener mUserEventListener;
    private Query mLastFirebase;


    public static final String LOG_TAG = RecipeListActivity.class.getSimpleName();


    public static final String FIREBASE_URL = "https://reciped.firebaseio.com/";
    private static Firebase FIREBASE_REF_FULL_RECIPE_LIST;
    private static Firebase FIREBASE_REF_FULL_USER_LIST;

    private static final int NEW_RECIPE_REQUEST = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        FIREBASE_REF_FULL_RECIPE_LIST = new Firebase(FIREBASE_URL).child(Recipe.FIREBASE_RECIPE_PATH);
        FIREBASE_REF_FULL_USER_LIST = new Firebase(FIREBASE_URL).child(User.FIREBASE_USER_PATH);
        setContentView(R.layout.activity_recipe_list);
        ButterKnife.bind(this);


        mAuthStateListener = new Firebase.AuthStateListener()

        {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                setAuthenticatedUser(authData);
            }
        };
        /* Check if the user is authenticated with Firebase already. If this is the case we can set the authenticated
         * user and hide hide any login buttons */
        FIREBASE_REF_FULL_RECIPE_LIST.addAuthStateListener(mAuthStateListener);
        

        /**Add the Recipe List Adapter**/
        changeRecipeAdapterQuery(FIREBASE_REF_FULL_RECIPE_LIST);

        /** Autocomplete **/
        setupAutoComplete();


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


    private void changeRecipeAdapterQuery(Query q) {
        if (!q.equals(mLastFirebase)) {
            mLastFirebase = q;
            if (mListAdapter != null) {
                mListAdapter.cleanup();
            }

            mListAdapter = new FirebaseListAdapter<Recipe>(q, Recipe.class,
                    R.layout.item_recipe, this) {
                @Override
                protected void populateView(View v, Recipe model) {
                    ((TextView) v.findViewById(R.id.recipe_instructions)).setText(model.getInstructions());
                    ((TextView) v.findViewById(R.id.recipe_name)).setText(model.getName());
                }
            };
            setListAdapter(mListAdapter);
        }
    }


    public void onAddNewRecipe(View v) {
        Intent i = new Intent(this, RecipeDetailActivity.class);
        i.putExtra(RecipeDetailActivity.USERNAME_EXTRA, mUser.getEmail());
        startActivityForResult(i, NEW_RECIPE_REQUEST);// get what's added and add it

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == NEW_RECIPE_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                String recipeId = data.getStringExtra(RecipeDetailActivity.RECIPE_ID_EXTRA);
                Firebase recipeBook = FIREBASE_REF_FULL_USER_LIST.child(mUser.getUid()).
                        child(User.FIREBASE_RECIPE_BOOK_PATH);

                HashMap<String, Object> recipeTag = new HashMap<>();
                recipeTag.put(recipeId, new Boolean(true));
                recipeBook.updateChildren(recipeTag);

                Log.e(LOG_TAG, "Just added a recipe with id " + recipeId);

                /*String id = data.getStringExtra(RecipeDetailActivity.RECIPE_ID_EXTRA);
                Firebase ref = new Firebase("https://reciped.firebaseio.com/");
                mUser.addOwnedRecipe();
                //we can grab from the db given the id...and then add to the user*/
            }
        } else if (requestCode == LoginActivity.LOGIN_REQUEST) {
            if (resultCode == RESULT_OK) {

            }
        }
    }


    private void setAuthenticatedUser(AuthData authData) {
        //This is only called once

        if (authData != null) {

            mUserEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Log.e(LOG_TAG, "Had data snapshot " + snapshot.toString());
                    boolean isLogin = (mUser == null);
                    mUser = snapshot.getValue(User.class);
                    if (isLogin) {
                        if (mUser != null) {
                            mLoginButton.setText(getString(R.string.logout)); //changes to logout if we're still tracking a user
                            Log.e(LOG_TAG, "Logged in user " + mUser.getEmail());
                        } else {
                            Log.e(LOG_TAG, "In strange state where mUser is null after getting the user");
                        }
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    System.out.println("The read failed: " + firebaseError.getMessage());
                }
            };
            FIREBASE_REF_FULL_USER_LIST.child(authData.getUid()).
                    addValueEventListener(mUserEventListener);
        } else {
            Log.e(LOG_TAG, "Logged out user");
            if (mUser != null) {
                Log.e(LOG_TAG, "The user was " + mUser.getEmail());
                FIREBASE_REF_FULL_USER_LIST.child(mUser.getUid()).removeEventListener(mUserEventListener);
                mUserEventListener = null;
            }
            assert mUserEventListener == null; //Crazy if this is not true
            mUser = null;

        }
    }

    private void login() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivityForResult(i, LoginActivity.LOGIN_REQUEST);
    }

    public void onLoginClicked(View view) {
        if (mLoginButton.getText().toString() == getString(R.string.login)) {
            assert mUser == null;
            login();
        } else {
            mLoginButton.setText(getString(R.string.login));
            FIREBASE_REF_FULL_RECIPE_LIST.unauth();

        }
    }

    private void setupAutoComplete() {
        final FirebaseAutocompleteAdapter<User> autocompleteFBAdapter = new FirebaseAutocompleteAdapter<User>(FIREBASE_REF_FULL_USER_LIST,
                User.class,
                android.R.layout.simple_dropdown_item_1line, this) {

            @Override
            protected String massageItemToString(User model) {
                return model.getEmail();
            }

            private final FirebaseAutocompleteAdapter<User> thisAdapter = this;


            @Override
            protected void populateView(View v, User model) {
                ((TextView) v.findViewById(android.R.id.text1)).setText(model.getEmail());

            }

            @Override
            public Filter getFilter() {
                return new Filter() {
                    @Override
                    protected FilterResults performFiltering(CharSequence charSequence) {
                        Iterator<User> filterIt = thisAdapter.getmModels().iterator();
                        FilterResults results = new FilterResults();
                        ArrayList<User> resultsList = new ArrayList<User>();

                        while (filterIt.hasNext()) {
                            User currentUser = filterIt.next();
                            if (currentUser.getEmail().contains(charSequence)) {
                                resultsList.add(currentUser);
                            }
                        }
                        results.values = resultsList;
                        results.count = resultsList.size();

                        return results;
                    }

                    @Override
                    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                        if (filterResults.count > 0) {

                            thisAdapter.mResults.clear();
                            thisAdapter.mResults.addAll((ArrayList<User>) filterResults.values);
                            notifyDataSetChanged();
                        } else {
                            notifyDataSetInvalidated();
                        }

                    }
                };
            }


        };


        AutoCompleteTextView textView = (AutoCompleteTextView)
                findViewById(R.id.search);
        textView.setAdapter(autocompleteFBAdapter);
        textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String email = autocompleteFBAdapter.getItem(i);

                changeRecipeAdapterQuery(FIREBASE_REF_FULL_RECIPE_LIST.
                        orderByChild("owner").equalTo(email));
            }
        });
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() < 2) {
                    changeRecipeAdapterQuery(FIREBASE_REF_FULL_RECIPE_LIST);
                }

            }
        });
    }


}
