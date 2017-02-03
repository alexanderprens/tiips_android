package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory.inventory_items;


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
import android.widget.TextView;

import org.helpingkidsroundfirst.hkrf.R;
import org.helpingkidsroundfirst.hkrf.data.InventoryContract;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewInventoryItemDetailFragment extends Fragment
    implements LoaderManager.LoaderCallbacks<Cursor> {

    private TextView nameView;
    private TextView descriptionView;
    private TextView categoryView;
    private TextView valueView;
    private TextView barcodeView;
    private Uri inventoryItemDetailUri;

    private static final String[] ITEM_DETAIL_COLUMNS = {
            InventoryContract.ItemEntry.TABLE_NAME + "." + InventoryContract.ItemEntry._ID,
            InventoryContract.ItemEntry.COLUMN_BARCODE_ID,
            InventoryContract.ItemEntry.COLUMN_NAME,
            InventoryContract.ItemEntry.COLUMN_DESCRIPTION,
            InventoryContract.ItemEntry.COLUMN_CATEGORY,
            InventoryContract.ItemEntry.COLUMN_VALUE
    };

    // Inventory item column indices
    public static final int COL_ITEM_ID = 0;
    public static final int COL_ITEM_BARCODE = 1;
    public static final int COL_ITEM_NAME = 2;
    public static final int COL_ITEM_DESC = 3;
    public static final int COL_ITEM_CAT = 4;
    public static final int COL_ITEM_VALUE = 5;

    public static final String DETAILED_ITEM_KEY = "ITEM_URI";
    public static final int ITEM_DETAIL_LOADER = 4;

    public ViewInventoryItemDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_view_inventory_item_detail, container,
                false);

        // get item uri from bundle
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            inventoryItemDetailUri = bundle.getParcelable(DETAILED_ITEM_KEY);
        }

        // assign text views
        nameView = (TextView) rootView.findViewById(R.id.inventory_item_detail_text_name);
        descriptionView = (TextView) rootView.findViewById(
                R.id.inventory_item_detail_text_description);
        categoryView = (TextView) rootView.findViewById(R.id.inventory_item_detail_text_category);
        valueView = (TextView) rootView.findViewById(R.id.inventory_item_detail_text_value);
        barcodeView = (TextView) rootView.findViewById(R.id.inventory_item_detail_text_barcode);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(ITEM_DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if( null != inventoryItemDetailUri) {

            // get the detailed inventory item from database
            return  new CursorLoader(
                    getActivity(),
                    inventoryItemDetailUri,
                    ITEM_DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data != null && data.moveToFirst()) {
            //read data from cursor
            String name = data.getString(COL_ITEM_NAME);
            String description = data.getString(COL_ITEM_DESC);
            String category = data.getString(COL_ITEM_CAT);
            int value = data.getInt(COL_ITEM_VALUE);
            String valueString = "" + value;
            String barcode = data.getString(COL_ITEM_BARCODE);

            //place data into text views
            nameView.setText(name);
            descriptionView.setText(description);
            categoryView.setText(category);
            valueView.setText(valueString);
            barcodeView.setText(barcode);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
