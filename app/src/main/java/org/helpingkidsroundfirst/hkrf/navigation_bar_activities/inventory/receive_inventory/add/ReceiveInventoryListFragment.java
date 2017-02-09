package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.receive_inventory.add;


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
 * A simple {@link Fragment} subclass.
 */
public class ReceiveInventoryListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        AddReceiveDialogFragment.AddReceiveDialogListener {

    // column indices
    public static final int COL_RECEIVE_ID = 0;
    public static final int COL_RECEIVE_ITEM_KEY = 1;
    public static final int COL_RECEIVE_QTY = 2;
    public static final int COL_RECEIVE_DATE_RECEIVED = 3;
    public static final int COL_RECEIVE_DONOR = 4;
    public static final int COL_RECEIVE_WAREHOUSE = 5;
    public static final int COL_ITEM_BARCODE = 6;
    public static final int COL_ITEM_NAME = 7;
    public static final int COL_ITEM_DESCRIPTION = 8;
    public static final int COL_ITEM_CATEGORY_KEY = 9;
    public static final int COL_ITEM_VALUE = 10;
    public static final int COL_CATEGORY_NAME = 11;
    public static final int COL_CATEGORY_BARCODE_PREFIX = 12;
    // Current inventory columns
    private static final String[] RECEIVE_INVENTORY_COLUMNS = {
            InventoryContract.ReceiveInventoryEntry.TABLE_NAME + "." +
                    InventoryContract.ReceiveInventoryEntry._ID + " AS _id",
            InventoryContract.ReceiveInventoryEntry.COLUMN_ITEM_KEY,
            InventoryContract.ReceiveInventoryEntry.COLUMN_QTY,
            InventoryContract.ReceiveInventoryEntry.COLUMN_DONOR,
            InventoryContract.ReceiveInventoryEntry.COLUMN_DATE_RECEIVED,
            InventoryContract.ReceiveInventoryEntry.COLUMN_WAREHOUSE,
            InventoryContract.ItemEntry.COLUMN_BARCODE_ID,
            InventoryContract.ItemEntry.COLUMN_NAME,
            InventoryContract.ItemEntry.COLUMN_DESCRIPTION,
            InventoryContract.ItemEntry.COLUMN_CATEGORY_KEY,
            InventoryContract.ItemEntry.COLUMN_VALUE,
            InventoryContract.CategoryEntry.COLUMN_CATEGORY,
            InventoryContract.CategoryEntry.COLUMN_BARCODE_PREFIX
    };
    private static final String SELECTED_KEY = "receive_selected_position";
    private static final int RECEIVE_INVENTORY_LOADER = 9;
    private ReceiveInventoryAdapter mReceiveInventoryAdapter;
    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;
    private ReceiveInventoryListListener mListener;

    public ReceiveInventoryListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // get listener
        mListener = (ReceiveInventoryListListener) getActivity();

        // init adapter for listview
        mReceiveInventoryAdapter = new ReceiveInventoryAdapter(getActivity(), null, 0);

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_receive_inventory_list, container, false);

        // get list view
        mListView = (ListView) rootView.findViewById(R.id.receive_inventory_list);
        mListView.setAdapter(mReceiveInventoryAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);

                if (cursor != null) {
                    Uri uri = InventoryContract.ReceiveInventoryEntry
                            .buildReceiveInventoryWithIdUri(cursor.getLong(COL_RECEIVE_ID));
                    mListener.onItemSelected(uri);
                }
            }
        });

        // get fab
        FloatingActionButton fab = (FloatingActionButton) rootView
                .findViewById(R.id.receive_inventory_list_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // implement dialog
                FragmentManager fragmentManager = getFragmentManager();
                AddReceiveDialogFragment dialog = new AddReceiveDialogFragment();
                dialog.setTargetFragment(ReceiveInventoryListFragment.this, 300);
                dialog.show(fragmentManager, "open add receive dialog");
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    // get saved instance on create
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(RECEIVE_INVENTORY_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    // save state on close
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String sortOrder = InventoryContract.ItemEntry.COLUMN_NAME + " ASC";
        Uri uri = InventoryContract.ReceiveInventoryEntry.buildReceiveInventoryUri();

        return new CursorLoader(
                getActivity(),
                uri,
                RECEIVE_INVENTORY_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (null != data && data.moveToFirst()) {
            mReceiveInventoryAdapter.swapCursor(data);

            if (mPosition != ListView.INVALID_POSITION) {
                mListView.smoothScrollToPosition(mPosition);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mReceiveInventoryAdapter.swapCursor(null);
    }

    @Override
    public void onButtonOK() {
        getLoaderManager().restartLoader(RECEIVE_INVENTORY_LOADER, null, this);
    }

    public interface ReceiveInventoryListListener {
        void onItemSelected(Uri uri);
    }
}
