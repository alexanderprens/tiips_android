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
import org.helpingkidsroundfirst.hkrf.data.InventoryContract;
import org.helpingkidsroundfirst.hkrf.data.InventoryContract.CurrentInventoryEntry;
import org.helpingkidsroundfirst.hkrf.data.InventoryContract.ItemEntry;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewCurrentInventoryListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    // Current inventory column indices
    public static final int COL_CURRENT_ID = 0;
    public static final int COL_CURRENT_QTY = 1;
    public static final int COL_CURRENT_DONOR = 2;
    public static final int COL_CURRENT_DATE_RECEIVED = 3;
    public static final int COL_CURRENT_WAREHOUSE = 4;
    public static final int COL_ITEM_NAME = 5;
    public static final int COL_ITEM_DESCRIPTION = 6;
    public static final int COL_ITEM_CATEGORY_KEY = 7;
    public static final int COL_ITEM_VALUE = 8;
    public static final int COL_CATEGORY_ID = 9;
    public static final int COL_CATEGORY_NAME = 10;
    public static final int COL_CATEGORY_BARCODE_PREFIX = 11;
    public static final int COL_BARCODE_ID = 12;
    public static final String CURRENT_URI_KEY = "current_uri_key";
    private static final String SELECTED_KEY = "selected_position";
    private static final int CURRENT_INVENTORY_LOADER = 1;
    // Current inventory columns
    private static final String[] CURRENT_INVENTORY_COLUMNS = {
            InventoryContract.CurrentInventoryEntry.TABLE_NAME + "." +
                    InventoryContract.CurrentInventoryEntry._ID + " AS _id",
            InventoryContract.CurrentInventoryEntry.COLUMN_QTY,
            InventoryContract.CurrentInventoryEntry.COLUMN_DONOR,
            InventoryContract.CurrentInventoryEntry.COLUMN_DATE_RECEIVED,
            InventoryContract.CurrentInventoryEntry.COLUMN_WAREHOUSE,
            InventoryContract.CurrentInventoryEntry.COLUMN_NAME,
            InventoryContract.CurrentInventoryEntry.COLUMN_DESCRIPTION,
            InventoryContract.CurrentInventoryEntry.COLUMN_CATEGORY_KEY,
            InventoryContract.CurrentInventoryEntry.COLUMN_VALUE,
            InventoryContract.CategoryEntry.TABLE_NAME + "." +
                    InventoryContract.CategoryEntry._ID,
            InventoryContract.CategoryEntry.COLUMN_CATEGORY,
            InventoryContract.CategoryEntry.COLUMN_BARCODE_PREFIX,
            CurrentInventoryEntry.COLUMN_BARCODE_ID
    };
    private ViewCurrentInventoryAdapter mViewCurrentInventoryAdapter;
    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;
    private Uri mUri;

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

                if(cursor != null) {
                    ((Callback) getActivity())
                            .onCurrentInventorySelected(CurrentInventoryEntry
                            .buildCurrentInventoryWithIdUri(cursor.getLong(COL_CURRENT_ID)));
                }
            }
        });

        // get arguments from bundle
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mUri = bundle.getParcelable(CURRENT_URI_KEY);
        }

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

        if (mUri != null) {
            String sortOrder = ItemEntry.COLUMN_NAME + " ASC";

            return new CursorLoader(getActivity(),
                    mUri,
                    CURRENT_INVENTORY_COLUMNS,
                    null,
                    null,
                    sortOrder
            );
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(null != data && data.moveToFirst()) {
            mViewCurrentInventoryAdapter.swapCursor(data);

            if (mPosition != ListView.INVALID_POSITION) {
                mListView.smoothScrollToPosition(mPosition);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mViewCurrentInventoryAdapter.swapCursor(null);
    }

    public interface Callback {
        void onCurrentInventorySelected(Uri currentItemURI);
    }
}
