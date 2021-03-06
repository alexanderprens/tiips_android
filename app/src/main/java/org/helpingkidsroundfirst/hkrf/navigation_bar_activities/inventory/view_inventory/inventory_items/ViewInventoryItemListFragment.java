package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory.inventory_items;

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
public class ViewInventoryItemListFragment extends Fragment
    implements LoaderManager.LoaderCallbacks<Cursor>,
        AddItemDialogFragment.AddItemDialogListener {

    // Inventory item column indices
    public static final int COL_ITEM_ID = 0;
    public static final int COL_ITEM_BARCODE = 1;
    public static final int COL_ITEM_NAME = 2;
    public static final int COL_ITEM_DESC = 3;
    public static final int COL_CATEGORY_NAME = 4;
    public static final String ITEM_URI_KEY = "item_uri_key";
    private static final String SELECTED_KEY = "selected_position";
    private static final int INVENTORY_ITEM_LOADER = 0;

    // Inventory item columns
    private static final String[] INVENTORY_ITEM_COLUMNS = {
            InventoryContract.ItemEntry.TABLE_NAME + "." + InventoryContract.ItemEntry._ID,
            InventoryContract.ItemEntry.COLUMN_BARCODE_ID,
            InventoryContract.ItemEntry.COLUMN_NAME,
            InventoryContract.ItemEntry.COLUMN_DESCRIPTION,
            InventoryContract.CategoryEntry.COLUMN_CATEGORY,
    };
    private ViewInventoryItemAdapter mViewInventoryItemAdapter;
    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;
    private Uri mUri;

    public ViewInventoryItemListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // use adapter to take data from a source and populate list
        mViewInventoryItemAdapter = new ViewInventoryItemAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_view_inventory_item_list, container, false);

        // add floating action button
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(
                R.id.inventory_item_list_fab);
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
        mListView.setAdapter(mViewInventoryItemAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Adapter returns a cursor at the correct position for
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);

                // gets the item id from the selected item
                if(cursor != null){
                    ((Callback) getActivity())
                            .onItemSelected(InventoryContract.ItemEntry
                                    .buildInventoryItemWithIdUri(
                                            cursor.getLong(COL_ITEM_ID)
                                    )
                            );
                }
                mPosition = position;
            }
        });

        // get uri from arguments
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mUri = bundle.getParcelable(ITEM_URI_KEY);
        }

        // Check for instance state
        if(savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)){
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    // get saved instance on create
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        getLoaderManager().initLoader(INVENTORY_ITEM_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    // save state on close
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
        if (mUri != null) {
            String sortOrder = InventoryContract.ItemEntry.COLUMN_NAME + " ASC";

            return new CursorLoader(getActivity(),
                    mUri,
                    INVENTORY_ITEM_COLUMNS,
                    null,
                    null,
                    sortOrder
            );
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data){
        mViewInventoryItemAdapter.swapCursor(data);
        if(mPosition != ListView.INVALID_POSITION){
            mListView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader){
        mViewInventoryItemAdapter.swapCursor(null);
    }

    // Dialog click listeners
    @Override
    public void onButtonOK() {
        // restart loader to include new item
        getLoaderManager().restartLoader(INVENTORY_ITEM_LOADER, null, this);
    }

    // Callback for when item is selected
    public interface Callback {
        void onItemSelected(Uri invItemURI);
    }
}
