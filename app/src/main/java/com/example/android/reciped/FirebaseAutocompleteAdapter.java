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
 * Created by lyla on 7/23/15.
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


    protected abstract String massageItemToString(T model);
    protected abstract boolean shouldAddToList(T currentObject, CharSequence enteredString);
}