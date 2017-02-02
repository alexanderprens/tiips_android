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
import org.helpingkidsroundfirst.hkrf.data.InventoryContract.ItemEntry;
import org.helpingkidsroundfirst.hkrf.data.InventoryContract.PastInventoryEntry;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewPastInventoryListFragment extends Fragment
    implements LoaderManager.LoaderCallbacks<Cursor> {

    private ViewPastInventoryAdapter mViewPastInventoryAdapter;
    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;
    private static final String SELECTEC_KEY = "selected_position";
    private static final int PAST_INVENTORY_LOADER = 2;

    // Past inventory columns
    private static final String[] PAST_INVENTORY_COLUMNS = {
            PastInventoryEntry.TABLE_NAME + "." + PastInventoryEntry._ID,
            PastInventoryEntry.COLUMN_ITEM_KEY,
            PastInventoryEntry.COLUMN_QTY,
            PastInventoryEntry.COLUMN_DATE_SHIPPED,
            PastInventoryEntry.COLUMN_DONOR,
            ItemEntry.TABLE_NAME + "." + ItemEntry._ID,
            ItemEntry.COLUMN_BARCODE_ID,
            ItemEntry.COLUMN_NAME,
            ItemEntry.COLUMN_DESCRIPTION,
            ItemEntry.COLUMN_CATEGORY,
            ItemEntry.COLUMN_VALUE
    };

    public static final int COL_PAST_ID = 0;
    public static final int COL_PAST_ITEM_KEY = 1;
    public static final int COL_PAST_QTY = 2;
    public static final int COL_PAST_DATE_SHIPPED = 3;
    public static final int COL_PAST_DONOR = 4;
    public static final int COL_ITEM_ID = 5;
    public static final int COL_ITEM_BARCODE = 6;
    public static final int COL_ITEM_NAME = 7;
    public static final int COL_ITEM_DESCRIPTION = 8;
    public static final int COL_ITEM_CATEGORY = 9;
    public static final int COL_ITEM_VALUE = 10;

    public interface Callback {
        void onItemSelected(Uri pastItemURI);
    }

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

                // TODO: 2/1/2017 implement on item click
            }
        });

        if(savedInstanceState != null && savedInstanceState.containsKey(SELECTEC_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTEC_KEY);
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
            outState.putInt(SELECTEC_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = ItemEntry.COLUMN_NAME + " ASC";

        Uri pastInventoryUri = PastInventoryEntry.buildPastInventoryUri();

        return new CursorLoader(getActivity(),
                pastInventoryUri,
                PAST_INVENTORY_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mViewPastInventoryAdapter.swapCursor(data);

        if(mPosition != ListView.INVALID_POSITION) {
            mListView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mViewPastInventoryAdapter.swapCursor(null);
    }
}
