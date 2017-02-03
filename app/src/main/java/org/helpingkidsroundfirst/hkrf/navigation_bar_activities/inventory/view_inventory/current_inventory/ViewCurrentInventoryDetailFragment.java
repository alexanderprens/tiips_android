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
import android.widget.TextView;

import org.helpingkidsroundfirst.hkrf.R;
import org.helpingkidsroundfirst.hkrf.data.InventoryContract;

/**
 * Created by Alex on 2/2/2017.
 */

public class ViewCurrentInventoryDetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private TextView nameView;
    private TextView descriptionView;
    private TextView categoryView;
    private TextView valueView;
    private TextView barcodeView;
    private TextView qtyView;
    private TextView dateView;
    private TextView donorView;
    private TextView warehouseView;
    private Uri mUri;

    private static final String[] CURRENT_DETAIL_COLUMNS = {
            InventoryContract.CurrentInventoryEntry.TABLE_NAME + "." +
                    InventoryContract.CurrentInventoryEntry._ID,
            InventoryContract.CurrentInventoryEntry.COLUMN_ITEM_KEY,
            InventoryContract.CurrentInventoryEntry.COLUMN_QTY,
            InventoryContract.CurrentInventoryEntry.COLUMN_DONOR,
            InventoryContract.CurrentInventoryEntry.COLUMN_DATE_RECEIVED,
            InventoryContract.CurrentInventoryEntry.COLUMN_WAREHOUSE,
            InventoryContract.ItemEntry.TABLE_NAME + "." + InventoryContract.ItemEntry._ID,
            InventoryContract.ItemEntry.COLUMN_BARCODE_ID,
            InventoryContract.ItemEntry.COLUMN_NAME,
            InventoryContract.ItemEntry.COLUMN_DESCRIPTION,
            InventoryContract.ItemEntry.COLUMN_CATEGORY,
            InventoryContract.ItemEntry.COLUMN_VALUE
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

    public static final String DETAILED_CURRENT_KEY = "CURRENT_URI";
    public static final int CURRENT_DETAIL_LOADER = 5;

    public ViewCurrentInventoryDetailFragment() {
        //required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate view for this layout
        View rootView = inflater.inflate(R.layout.fragment_view_current_inventory_detail, container,
                false);

        // get current inventory id from bundle
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mUri = bundle.getParcelable(DETAILED_CURRENT_KEY);
        }

        //assign text view
        nameView = (TextView) rootView.findViewById(R.id.current_inventory_detail_text_name);
        descriptionView = (TextView) rootView.findViewById(
                R.id.current_inventory_detail_text_description);
        categoryView = (TextView) rootView.findViewById(R.id.current_inventory_detail_text_category);
        valueView = (TextView) rootView.findViewById(R.id.current_inventory_detail_text_value);
        barcodeView = (TextView) rootView.findViewById(R.id.current_inventory_detail_text_barcode);
        qtyView = (TextView) rootView.findViewById(R.id.current_inventory_detail_text_qty);
        dateView = (TextView) rootView.findViewById(R.id.current_inventory_detail_text_date);
        donorView = (TextView) rootView.findViewById(R.id.current_inventory_detail_text_donor);
        warehouseView = (TextView) rootView.findViewById(R.id.current_inventory_detail_text_warehouse);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(CURRENT_DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(null != mUri) {

            // get the detailed item form database
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    CURRENT_DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // if there's data load it
        if(data != null && data.moveToFirst()) {
            //read data from cursor
            String name = data.getString(COL_ITEM_NAME);
            String description = data.getString(COL_ITEM_DESCRIPTION);
            String category = data.getString(COL_ITEM_CATEGORY);
            int value = data.getInt(COL_ITEM_VALUE);
            String valueString = "" + value;
            String barcode = data.getString(COL_ITEM_BARCODE);
            int quantity = data.getInt(COL_CURRENT_QTY);
            String quantityString = "" + quantity;
            String date = data.getString(COL_CURRENT_DATE_RECEIVED);
            String donor = data.getString(COL_CURRENT_DONOR);
            String warehouse = data.getString(COL_CURRENT_WAREHOUSE);

            //place data into text views
            nameView.setText(name);
            descriptionView.setText(description);
            categoryView.setText(category);
            valueView.setText(valueString);
            barcodeView.setText(barcode);
            qtyView.setText(quantityString);
            dateView.setText(date);
            donorView.setText(donor);
            warehouseView.setText(warehouse);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}