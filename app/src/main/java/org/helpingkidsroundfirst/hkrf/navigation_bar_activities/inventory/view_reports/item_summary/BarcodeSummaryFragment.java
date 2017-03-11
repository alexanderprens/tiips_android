package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_reports.item_summary;


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
public class BarcodeSummaryFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String ITEM_URI = "item_uri";
    public static final int ITEM_SUMMARY_LOADER = 15;
    // Inventory item column indices
    private static final int COL_ITEM_ID = 0;
    private static final int COL_ITEM_BARCODE = 1;
    private static final int COL_ITEM_NAME = 2;
    private static final int COL_ITEM_DESC = 3;
    private static final int COL_ITEM_CAT_KEY = 4;
    private static final int COL_ITEM_VALUE = 5;
    private static final int COL_CATEGORY_ID = 6;
    private static final int COL_CATEGORY_NAME = 7;
    private static final int COL_CATEGORY_BARCODE = 8;
    private static final String[] ITEM_DETAIL_COLUMNS = {
            InventoryContract.ItemEntry.TABLE_NAME + "." + InventoryContract.ItemEntry._ID,
            InventoryContract.ItemEntry.COLUMN_BARCODE_ID,
            InventoryContract.ItemEntry.COLUMN_NAME,
            InventoryContract.ItemEntry.COLUMN_DESCRIPTION,
            InventoryContract.ItemEntry.COLUMN_CATEGORY_KEY,
            InventoryContract.ItemEntry.COLUMN_VALUE,
            InventoryContract.CategoryEntry.TABLE_NAME + "." + InventoryContract.CategoryEntry._ID,
            InventoryContract.CategoryEntry.COLUMN_CATEGORY,
            InventoryContract.CategoryEntry.COLUMN_BARCODE_PREFIX
    };
    private TextView nameView;
    private TextView descriptionView;
    private TextView categoryView;
    private TextView valueView;
    private TextView barcodeView;
    private Uri mUri;

    public BarcodeSummaryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_barcode_summary, container, false);

        final Bundle bundle = this.getArguments();
        if (bundle != null) {
            mUri = bundle.getParcelable(ITEM_URI);
        }

        // assign current text views

        // assign past text views

        // assign detail text views
        nameView = (TextView) rootView.findViewById(R.id.item_detail_text_name);
        descriptionView = (TextView) rootView.findViewById(R.id.item_detail_text_description);
        categoryView = (TextView) rootView.findViewById(R.id.item_detail_text_category);
        valueView = (TextView) rootView.findViewById(R.id.item_detail_text_value);
        barcodeView = (TextView) rootView.findViewById(R.id.item_detail_text_barcode);

        // run current and past data gathering

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(ITEM_SUMMARY_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (mUri != null) {

            return new CursorLoader(
                    getActivity(),
                    mUri,
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
        if (data != null && data.moveToFirst()) {
            //read data from cursor
            String name = data.getString(COL_ITEM_NAME);
            String description = data.getString(COL_ITEM_DESC);
            String category = data.getString(COL_CATEGORY_NAME);
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
