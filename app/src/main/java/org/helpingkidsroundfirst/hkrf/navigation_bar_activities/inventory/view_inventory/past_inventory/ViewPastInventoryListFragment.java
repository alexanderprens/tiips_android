package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory.past_inventory;


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
import org.helpingkidsroundfirst.hkrf.data.InventoryContract.PastInventoryEntry;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewPastInventoryListFragment extends Fragment
    implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int COL_PAST_ID = 0;
    public static final int COL_PAST_QTY = 1;
    public static final int COL_PAST_DATE_SHIPPED = 2;
    public static final int COL_PAST_DONOR = 3;
    public static final int COL_ITEM_NAME = 4;
    public static final int COL_ITEM_DESCRIPTION = 5;
    public static final int COL_ITEM_CATEGORY_KEY = 6;
    public static final int COL_ITEM_VALUE = 7;
    public static final int COL_CATEGORY_ID = 8;
    public static final int COL_CATEGORY_NAME = 9;
    public static final int COL_CATEGORY_BARCODE = 10;
    public static final int COL_BARCODE_ID = 11;
    public static final String PAST_URI_KEY = "past_uri_key";
    private static final String SELECTED_KEY = "selected_position";
    private static final int PAST_INVENTORY_LOADER = 2;
    // Past inventory columns
    private static final String[] PAST_INVENTORY_COLUMNS = {
            InventoryContract.PastInventoryEntry.TABLE_NAME + "." + InventoryContract.PastInventoryEntry._ID,
            InventoryContract.PastInventoryEntry.COLUMN_QTY,
            InventoryContract.PastInventoryEntry.COLUMN_DATE_SHIPPED,
            InventoryContract.PastInventoryEntry.COLUMN_DONOR,
            InventoryContract.PastInventoryEntry.COLUMN_NAME,
            InventoryContract.PastInventoryEntry.COLUMN_DESCRIPTION,
            InventoryContract.PastInventoryEntry.COLUMN_CATEGORY_KEY,
            InventoryContract.PastInventoryEntry.COLUMN_VALUE,
            InventoryContract.CategoryEntry.TABLE_NAME + "." +
                    InventoryContract.CategoryEntry._ID,
            InventoryContract.CategoryEntry.COLUMN_CATEGORY,
            InventoryContract.CategoryEntry.COLUMN_BARCODE_PREFIX,
            PastInventoryEntry.COLUMN_BARCODE_ID
    };
    private ViewPastInventoryAdapter mViewPastInventoryAdapter;
    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;
    private Uri mUri;

    public ViewPastInventoryListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mViewPastInventoryAdapter = new ViewPastInventoryAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_view_past_inventory_list, container, false);

        mListView = (ListView) rootView.findViewById(R.id.view_past_inventory_list);
        mListView.setAdapter(mViewPastInventoryAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);

                if(cursor != null) {
                    ((Callback) getActivity())
                            .onPastInventorySelected(PastInventoryEntry
                                    .buildPastInventoryWithIdUri(cursor.getLong(COL_PAST_ID)));
                }
            }
        });

        // get uri from arguments
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mUri = bundle.getParcelable(PAST_URI_KEY);
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(PAST_INVENTORY_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (mUri != null) {
            String sortOrder = PastInventoryEntry.COLUMN_NAME + " ASC";

            return new CursorLoader(getActivity(),
                    mUri,
                    PAST_INVENTORY_COLUMNS,
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
            mViewPastInventoryAdapter.swapCursor(data);

            if (mPosition != ListView.INVALID_POSITION) {
                mListView.smoothScrollToPosition(mPosition);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mViewPastInventoryAdapter.swapCursor(null);
    }

    public interface Callback {
        void onPastInventorySelected(Uri pastItemURI);
    }
}
