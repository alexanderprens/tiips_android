package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory.categories;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.helpingkidsroundfirst.hkrf.R;
import org.helpingkidsroundfirst.hkrf.data.InventoryContract;

/**
 * Created by Alex on 2/4/2017.
 */

public class ViewCategoryListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        AddCategoryDialogFragment.AddCategoryListener {

    // category column indices
    public static final int COL_CATEGORY_ID = 0;
    public static final int COL_CATEGORY_NAME = 1;
    public static final int COL_CATEGORY_BARCODE = 2;
    private static final String SELECTED_KEY = "selected_position";
    private static final int CATEGORY_LOADER = 7;
    // category columns
    private static final String[] CATEGORY_COLUMNS = {
            InventoryContract.CategoryEntry.TABLE_NAME + "." + InventoryContract.CategoryEntry._ID,
            InventoryContract.CategoryEntry.COLUMN_CATEGORY,
            InventoryContract.CategoryEntry.COLUMN_BARCODE_PREFIX
    };
    private ViewCategoryAdapter mViewCategoryAdapter;
    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;


    public ViewCategoryListFragment() {
        // required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // use adapter to take from a source and populate list
        mViewCategoryAdapter = new ViewCategoryAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_view_category_list, container, false);

        // add fab
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(
                R.id.view_category_list_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                AddCategoryDialogFragment addCategoryDialogFragment = new AddCategoryDialogFragment();
                addCategoryDialogFragment.setTargetFragment(ViewCategoryListFragment.this, 300);
                addCategoryDialogFragment.show(fragmentManager, "open cateogory dialog");
            }
        });

        // get list view
        mListView = (ListView) rootView.findViewById(R.id.view_category_list);
        mListView.setAdapter(mViewCategoryAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Adapter returns a cursor at the correct position for
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);

                // gets the item id from the selected item
                if (cursor != null) {
                    ((ViewCategoryListFragment.Callback) getActivity())
                            .onCategorySelected(InventoryContract.CategoryEntry
                                    .buildCategoryWithIdUri(
                                            cursor.getLong(COL_CATEGORY_ID)
                                    )
                            );
                }
                mPosition = position;
            }
        });

        // check for saved instance
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(CATEGORY_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String sortOrder = InventoryContract.CategoryEntry.COLUMN_CATEGORY + " ASC";

        Uri categoryUri = InventoryContract.CategoryEntry.buildCategoryUri();

        return new CursorLoader(getActivity(),
                categoryUri,
                CATEGORY_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mViewCategoryAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            mListView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mViewCategoryAdapter.swapCursor(null);
    }

    @Override
    public void onButtonOK() {
        // restart loader to include new item
        getLoaderManager().restartLoader(CATEGORY_LOADER, null, this);
    }

    // callback for when item is selected
    public interface Callback {
        void onCategorySelected(Uri categoryUri);
    }
}
