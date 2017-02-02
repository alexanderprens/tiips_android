package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory.current_inventory;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.helpingkidsroundfirst.hkrf.R;
import org.helpingkidsroundfirst.hkrf.data.InventoryContract.CurrentInventoryEntry;
import org.helpingkidsroundfirst.hkrf.data.InventoryContract.ItemEntry;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewCurrentInventoryListFragment extends Fragment
    implements LoaderManager.LoaderCallbacks<Cursor> {

    private ViewCurrentInventoryAdapter mViewCurrentInventoryAdapter;
    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";
    private static final int CURRENT_INVENTORY_LOADER = 1;

    // Current inventory columns
    private static final String[] CURRENT_INVENTORY_COLUMNS = {
            CurrentInventoryEntry.TABLE_NAME + "." + CurrentInventoryEntry._ID,
            CurrentInventoryEntry.COLUMN_ITEM_KEY,
            CurrentInventoryEntry.COLUMN_QTY,
            CurrentInventoryEntry.COLUMN_DONOR,
            CurrentInventoryEntry.COLUMN_DATE_RECEIVED,
            CurrentInventoryEntry.COLUMN_WAREHOUSE,
            ItemEntry.TABLE_NAME + "." + ItemEntry._ID,
            ItemEntry.COLUMN_BARCODE_ID,
            ItemEntry.COLUMN_NAME,
            ItemEntry.COLUMN_DESCRIPTION,
            ItemEntry.COLUMN_CATEGORY,
            ItemEntry.COLUMN_VALUE
    };

    // Current inventory column indices
    public static final int COL_CURRENT_ID = 0;
    public static final int COL_CURRENT_ITEM_KEY = 1;
    public static final int COL_CURRENT_QTY = 2;
    public static final int COL_CURRENT_DATE_RECEIVED = 3;
    public static final int COL_CURRENT_DONOR = 4;
    public static final int COL_CURRENT_WAREHOUSE = 5;
    public static final int COL_ITEM_ID = 6;
    public static final int COL_ITEM_BARCODE = 7;
    public static final int COL_ITEM_NAME = 8;
    public static final int COL_ITEM_DESCRIPTION = 9;
    public static final int COL_ITEM_CATEGORY = 10;
    public static final int COL_ITEM_VALUE = 11;

    public interface Callback {
        void onItemSelected(Uri currentItemURI);
    }

    public ViewCurrentInventoryListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // use adapter to take data from source and populate list
        mViewCurrentInventoryAdapter = new ViewCurrentInventoryAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_view_current_inventory_list, container, false);

        // get list view
        mListView = (ListView) rootView.findViewById(R.id.current_inventory_list);
        mListView.setAdapter(mViewCurrentInventoryAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);

                // TODO: 2/1/2017 implement on item click
            }
        });

        if(savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    // get saved instance on create
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(CURRENT_INVENTORY_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    // save state on close
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    // when a new loader is created for the view
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = ItemEntry.COLUMN_NAME + " ASC";

        Uri currentInventoryUri = CurrentInventoryEntry.buildCurrentInventoryUri();

        return new CursorLoader(getActivity(),
                currentInventoryUri,
                CURRENT_INVENTORY_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mViewCurrentInventoryAdapter.swapCursor(data);

        if(mPosition != ListView.INVALID_POSITION) {
            mListView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mViewCurrentInventoryAdapter.swapCursor(null);
    }
}