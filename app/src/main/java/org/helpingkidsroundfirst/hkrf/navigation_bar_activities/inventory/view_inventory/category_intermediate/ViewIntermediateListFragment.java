package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory.category_intermediate;

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
import org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory.inventory_items.AddItemDialogFragment;

/**
 * Created by Alex on 2/5/2017.
 */

public class ViewIntermediateListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        AddItemDialogFragment.AddItemDialogListener {

    // columns for cursor
    public static final int COL_CATEGORY_ID = 1;
    public static final int COL_CATEGORY_CAT = 0;

    // keys for which type of item is expected
    public static final int EXPECTED_CURRENT_INVENTORY = 0;
    public static final int EXPECTED_PAST_INVENTORY = 1;
    public static final int EXPECTED_INVENTORY_ITEM = 2;
    public static final String EXPECTED_KEY = "expected_type";
    // info for activity
    private static final String SELECTED_KEY = "selected_position";
    private static final int INTERMEDIATE_LOADER = 9;

    // category columns
    private static final String[] CATEGORY_COLUMNS = {
            "DISTINCT " + InventoryContract.CategoryEntry.COLUMN_CATEGORY,
            InventoryContract.CategoryEntry.TABLE_NAME + "." + InventoryContract.CategoryEntry._ID
    };

    // internal vars
    private ViewIntermediateAdapter mViewIntermediateAdapter;
    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;
    private Uri mUri;
    private int mExpected;

    public ViewIntermediateListFragment() {
        // required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // setup adapter
        mViewIntermediateAdapter = new ViewIntermediateAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_view_intermediate_list, container, false);

        // make list view
        mListView = (ListView) rootView.findViewById(R.id.view_intermediate_list);
        mListView.setAdapter(mViewIntermediateAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);

                if (cursor != null) {
                    handleOnItemClickListener(cursor);
                }
                mPosition = position;
            }
        });

        // add fab
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(
                R.id.view_intermediate_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                AddItemDialogFragment addItemDialogFragment = new AddItemDialogFragment();
                addItemDialogFragment.setTargetFragment(ViewIntermediateListFragment.this, 300);
                addItemDialogFragment.show(fragmentManager, "open item dialog");
            }
        });

        // check for saved position
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        // get fragment arguments, build uri
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mExpected = bundle.getInt(EXPECTED_KEY);

            switch (mExpected) {
                case EXPECTED_CURRENT_INVENTORY:
                    mUri = InventoryContract.CurrentInventoryEntry.buildCurrentInventoryUri();
                    fab.setVisibility(View.GONE);
                    break;

                case EXPECTED_PAST_INVENTORY:
                    mUri = InventoryContract.PastInventoryEntry.buildPastInventoryUri();
                    fab.setVisibility(View.GONE);
                    break;

                case EXPECTED_INVENTORY_ITEM:
                    mUri = InventoryContract.ItemEntry.buildInventoryItemUri();
                    fab.setVisibility(View.VISIBLE);
                    break;

                default:
                    // oops
                    break;
            }
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(INTERMEDIATE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String sortOrder = InventoryContract.CategoryEntry.COLUMN_CATEGORY + " ASC";

        if (null != mUri) {
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    CATEGORY_COLUMNS,
                    null,
                    null,
                    sortOrder
            );
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            mViewIntermediateAdapter.swapCursor(data);
            if (mPosition != ListView.INVALID_POSITION) {
                mListView.smoothScrollToPosition(mPosition);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mViewIntermediateAdapter.swapCursor(null);
    }

    @Override
    public void onButtonOK() {
        getLoaderManager().restartLoader(INTERMEDIATE_LOADER, null, this);
    }

    private void handleOnItemClickListener(Cursor cursor) {
        Uri uri;
        Callback callback = (Callback) getActivity();

        switch (mExpected) {
            case EXPECTED_CURRENT_INVENTORY:
                uri = InventoryContract.CurrentInventoryEntry
                        .buildCurrentInventoryWithCategoryUri(cursor.getLong(COL_CATEGORY_ID));
                break;

            case EXPECTED_PAST_INVENTORY:
                uri = InventoryContract.PastInventoryEntry
                        .buildPastInventoryWithCategoryUri(cursor.getLong(COL_CATEGORY_ID));
                break;

            case EXPECTED_INVENTORY_ITEM:
                uri = InventoryContract.ItemEntry
                        .buildInventoryItemWithCategoryUri(cursor.getLong(COL_CATEGORY_ID));
                break;

            default:
                uri = InventoryContract.ItemEntry
                        .buildInventoryItemWithCategoryUri(cursor.getLong(COL_CATEGORY_ID));
                break;
        }

        callback.onIntermediateCategorySelected(uri, mExpected);
    }

    public interface Callback {
        void onIntermediateCategorySelected(Uri uri, int expected);
    }
}
