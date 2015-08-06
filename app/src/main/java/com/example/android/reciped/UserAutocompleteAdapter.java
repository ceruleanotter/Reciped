package com.example.android.reciped;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.Firebase;

/**
 * @author Lyla
 * @since 7/24/15
 *
 * This is a concrete class of FirebaseAutocompleteAdapter specifically for dealing with User objects.
 * It will show the user's emails in the autocomplete.
 *
 */
public class UserAutocompleteAdapter extends FirebaseAutocompleteAdapter<User> {

    public UserAutocompleteAdapter(Activity activity) {
        super(new Firebase(RecipeListActivity.FIREBASE_URL).child(User.FIREBASE_USER_PATH), User.class, android.R.layout.simple_dropdown_item_1line, activity);
    }


    @Override
    protected String massageItemToString(User model) {
        return model.getEmail();
    }


    @Override
    protected void populateView(View v, User model) {
        ((TextView) v.findViewById(android.R.id.text1)).setText(model.getEmail());

    }

    @Override
    protected boolean shouldAddToList(User currentObject, CharSequence enteredString) {
        return currentObject.getEmail().contains(enteredString);
    }

}
