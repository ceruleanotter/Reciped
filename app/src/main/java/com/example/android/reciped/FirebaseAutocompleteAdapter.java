package com.example.android.reciped;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.firebase.client.Query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Lyla
 * @since 7/22/15
 *
 * This class is a subclass of the generic FirebaseListAdapter that allows for autocomplete by
 * implemting Filterable. It also requires implementations of two methods:
 *
 * shouldAddToList - defines what should or should not end up in the autocomplete list
 * massageItemToString - takes the firebase object and creates an appropriate string to put into
 * the autocomplete list.
 *
 * @param <T> The class type to use as a model for the data contained in the children of the given Firebase location
 */
abstract public class FirebaseAutocompleteAdapter<T> extends FirebaseListAdapter<T> implements Filterable {

    /**
     * @param mRef        The Firebase location to watch for data changes. Can also be a slice of a location, using some
     *                    combination of <code>limit()</code>, <code>startAt()</code>, and <code>endAt()</code>,
     * @param mModelClass Firebase will marshall the data at a location into an instance of a class that you provide
     * @param mLayout     This is the mLayout used to represent a single list item. You will be responsible for populating an
     *                    instance of the corresponding view with the data from an instance of mModelClass.
     * @param activity    The activity containing the ListView
     *
     */

    protected List<T> mResults; //This is the actual list of displayable results
    protected FirebaseListAdapter<T> mThisAdapter;

    public FirebaseAutocompleteAdapter(Query mRef, Class<T> mModelClass, int mLayout, Activity activity) {
        super(mRef, mModelClass, mLayout, activity);
        mResults = new ArrayList<T>();
        mThisAdapter = this;
    }

    @Override
    public int getCount() {
        return mResults.size();
    }

    @Override
    public String getItem(int i) {
        return massageItemToString(mResults.get(i));
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = mInflater.inflate(mLayout, viewGroup, false);
        }

        T model = mResults.get(i);
        // Call out to subclass to marshall this model into the provided view
        populateView(view, model);
        return view;
    }


    //TODO Everything below here is the meat of what was added to make autocomplete a reality
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                Iterator<T> filterIt = mThisAdapter.getmModels().iterator();
                FilterResults results = new FilterResults();
                ArrayList<T> resultsList = new ArrayList<T>();

                while (filterIt.hasNext()) {
                    T current = filterIt.next();
                    if (shouldAddToList(current, charSequence)) {
                        resultsList.add(current);
                    }
                }
                results.values = resultsList;
                results.count = resultsList.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if (filterResults.count > 0) {
                    mResults.clear();
                    mResults.addAll((ArrayList<T>) filterResults.values);
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }

            }
        };
    }


    /**
     * Instead of directly returning the item (a string is required), this method is called to
     * generate a string from the current item.
     *
     * @param model The object containing the data used to populate the view
     * @return The String to show in the autocomplete list
     */

    protected abstract String massageItemToString(T model);

    /**
     * This method takes the current object and the entered String and decides whether it should be
     * added to the autocomplete list.
     *
     * @param currentObject The object that we're testing to see if it should be added to the auto
     *                      complete list
     * @param enteredString The string that was entered in the autocomplete textbox
     * @return This returns true if the item should be added to the autocomplete list and false
     * otherwise.
     */
    protected abstract boolean shouldAddToList(T currentObject, CharSequence enteredString);
}