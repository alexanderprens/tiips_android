package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.ship_inventory.add;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

/**
 * Created by Alex on 2/14/2017.
 */

public class ShipInventoryListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    // column indices
    public static final int COL_SHIP_ID = 0;
    public static final int COL_SHIP_BARCODE = 1;
    public static final int COL_SHIP_DONOR = 2;
    public static final int COL_SHIP_DATE_SHIPPED = 3;
    public static final int COL_SHIP_NAME = 4;
    public static final int COL_SHIP_VALUE = 5;
    public static final int COL_SHIP_QTY = 6;
    public static final int COL_SHIP_DESCRIPTION = 7;
    public static final int COL_SHIP_CATEGORY_KEY = 8;
    public static final int COL_SHIP_CATEGORY_ID = 9;
    public static final int COL_SHIP_CATEGORY = 10;
    // Ship Inventory Columns
    private static final String[] SHIP_INVENTORY_COLUMNS = {
            InventoryContract.ShipInventoryEntry.TABLE_NAME +
                    "." + InventoryContract.ShipInventoryEntry._ID + " As _id",
            InventoryContract.ShipInventoryEntry.COLUMN_BARCODE_ID,
            InventoryContract.ShipInventoryEntry.COLUMN_DONOR,
            InventoryContract.ShipInventoryEntry.COLUMN_DATE_SHIPPED,
            InventoryContract.ShipInventoryEntry.COLUMN_NAME,
            InventoryContract.ShipInventoryEntry.COLUMN_VALUE,
            InventoryContract.ShipInventoryEntry.COLUMN_QTY,
            InventoryContract.ShipInventoryEntry.COLUMN_DESCRIPTION,
            InventoryContract.ShipInventoryEntry.COLUMN_CATEGORY_KEY,
            InventoryContract.CategoryEntry.TABLE_NAME + "." +
                    InventoryContract.CategoryEntry._ID,
            InventoryContract.CategoryEntry.COLUMN_CATEGORY
    };
    private static final String SELECTED_KEY = "ship_selected_position";
    private static final int SHIP_INVENTORY_LOADER = 11;
    private ShipInventoryAdapter shipInventoryAdapter;
    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;
    private ShipInventoryListListener mListener;

    public ShipInventoryListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // get Listener
        mListener = (ShipInventoryListListener) getActivity();

        // init adapter for listview
        shipInventoryAdapter = new ShipInventoryAdapter(getActivity(), null, 0);

        // inflate layout
        View rootView = inflater.inflate(R.layout.fragment_ship_inventory_list, container, false);
        mListView = (ListView) rootView.findViewById(R.id.ship_inventory_list);
        mListView.setAdapter(shipInventoryAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);

                if (cursor != null) {
                    Uri uri = InventoryContract.ShipInventoryEntry
                            .buildShipInventoryWithIdUri(cursor.getLong(COL_SHIP_ID));
                    mListener.onItemSelected(uri);
                }
            }
        });

        // get fab
        FloatingActionButton fab = (FloatingActionButton) rootView
                .findViewById(R.id.ship_inventory_list_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // implement add dialog
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(SHIP_INVENTORY_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String sortOrder = InventoryContract.ShipInventoryEntry.COLUMN_NAME + " ASC";
        Uri uri = InventoryContract.ShipInventoryEntry.buildShipInventoryUri();

        return new CursorLoader(
                getActivity(),
                uri,
                SHIP_INVENTORY_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            shipInventoryAdapter.swapCursor(data);

            if (mPosition != ListView.INVALID_POSITION) {
                mListView.smoothScrollToPosition(mPosition);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        shipInventoryAdapter.swapCursor(null);
    }

    public interface ShipInventoryListListener {
        void onItemSelected(Uri uri);
    }
}
