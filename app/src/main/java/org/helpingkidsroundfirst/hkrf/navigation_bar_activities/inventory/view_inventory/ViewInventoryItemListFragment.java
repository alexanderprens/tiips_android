package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
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
public class ViewInventoryItemListFragment extends Fragment
    implements LoaderManager.LoaderCallbacks<Cursor>,
        AddItemDialogFragment.AddItemDialogListener {

    private ViewInventoryItemAdapter mViewInventorItemAdapter;
    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";
    private static final int INVENTORY_ITEM_LOADER = 0;

    // Inventory item columns
    private static final String[] INVENTORY_ITEM_COLUMNS = {
            InventoryContract.ItemEntry.TABLE_NAME,
            InventoryContract.ItemEntry.COLUMN_BARCODE_ID,
            InventoryContract.ItemEntry.COLUMN_NAME,
            InventoryContract.ItemEntry.COLUMN_DESCRIPTION,
            InventoryContract.ItemEntry.COLUMN_CATEGORY
    };

    // Inventory item column indices
    public static final int COL_ITEM_BARCODE = 1;
    public static final int COL_ITEM_NAME = 2;
    public static final int COL_ITEM_DESC = 3;
    public static final int COL_ITEM_CAT = 4;

    // Callback for when item is selected
    public interface Callback {
        void onItemSelected(Uri invItemURI);
    }

    public ViewInventoryItemListFragment() {
        // Required empty public constructor
    }

    // TODO: 1/24/2017 add menus


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // use adapter to take data from a source and populate list
        mViewInventorItemAdapter = new ViewInventoryItemAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_view_inventory_item_list, container, false);

        // add floating action button
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                AddItemDialogFragment addItemDialogFragment = new AddItemDialogFragment();
                addItemDialogFragment.setTargetFragment(ViewInventoryItemListFragment.this, 300);
                addItemDialogFragment.show(fragmentManager, "open item dialog");
            }
        });

        // get list view
        mListView = (ListView) rootView.findViewById(R.id.inventory_item_list);
        mListView.setAdapter(mViewInventorItemAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Adapter returns a cursor at the correct position for
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if(cursor != null){
                    ((Callback) getParentFragment())
                            .onItemSelected(InventoryContract.ItemEntry.buildInventoryItemWithID(
                                    cursor.getString(COL_ITEM_BARCODE)
                            ));
                }
                mPosition = position;
            }
        });

        // Check for instance state
        if(savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)){
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        getLoaderManager().initLoader(INVENTORY_ITEM_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        if(mPosition != ListView.INVALID_POSITION){
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle){
        // called when a new loader is created

        String sortOrder = InventoryContract.ItemEntry.COLUMN_NAME + " ASC";

        Uri itemUri = InventoryContract.ItemEntry.buildInventoryItemList();

        return new android.support.v4.content.CursorLoader(getActivity(),
                itemUri,
                INVENTORY_ITEM_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data){
        mViewInventorItemAdapter.swapCursor(data);
        if(mPosition != ListView.INVALID_POSITION){
            mListView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader){
        mViewInventorItemAdapter.swapCursor(null);
    }

    // Dialog click listeners
    @Override
    public void onButtonOK() {
        // TODO: 1/24/2017 implement method
    }

    @Override
    public void onButtonCancel() {
        // TODO: 1/24/2017 implement method
    }
}
