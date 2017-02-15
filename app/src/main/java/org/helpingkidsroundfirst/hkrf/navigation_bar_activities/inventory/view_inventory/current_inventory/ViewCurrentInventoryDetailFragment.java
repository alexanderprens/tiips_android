package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory.current_inventory;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.helpingkidsroundfirst.hkrf.R;
import org.helpingkidsroundfirst.hkrf.data.InventoryContract;

/**
 * Created by Alex on 2/2/2017.
 */

public class ViewCurrentInventoryDetailFragment extends Fragment implements
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
    public static final int COL_BARCODE_COMPLETE = 12;
    public static final String DETAILED_CURRENT_KEY = "CURRENT_URI";
    public static final int CURRENT_DETAIL_LOADER = 5;
    private static final String[] CURRENT_DETAIL_COLUMNS = {
            InventoryContract.CurrentInventoryEntry.TABLE_NAME + "." +
                    InventoryContract.CurrentInventoryEntry._ID + " AS _id",
            InventoryContract.CurrentInventoryEntry.COLUMN_QTY,
            InventoryContract.CurrentInventoryEntry.COLUMN_DONOR,
            InventoryContract.CurrentInventoryEntry.COLUMN_DATE_RECEIVED,
            InventoryContract.CurrentInventoryEntry.COLUMN_WAREHOUSE,
            InventoryContract.ItemEntry.COLUMN_NAME,
            InventoryContract.ItemEntry.COLUMN_DESCRIPTION,
            InventoryContract.ItemEntry.COLUMN_CATEGORY_KEY,
            InventoryContract.ItemEntry.COLUMN_VALUE,
            InventoryContract.CategoryEntry.TABLE_NAME + "." +
                    InventoryContract.CategoryEntry._ID,
            InventoryContract.CategoryEntry.COLUMN_CATEGORY,
            InventoryContract.CategoryEntry.COLUMN_BARCODE_PREFIX,
            InventoryContract.ItemEntry.TABLE_NAME + "." + InventoryContract.ItemEntry._ID
    };
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
    private long currentInventoryId;
    private int quantity;
    private String date;
    private String warehouse;
    private String donor;

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

        // assign text view
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

        // implement fab
        FloatingActionButton fab = (FloatingActionButton) rootView
                .findViewById(R.id.view_current_inventor_detail_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                UpdateCurrentDialogFragment dialog = new UpdateCurrentDialogFragment();
                Bundle dialogInputs = new Bundle();
                dialogInputs.putInt(UpdateCurrentDialogFragment.QTY_KEY, quantity);
                dialogInputs.putString(UpdateCurrentDialogFragment.WAREHOUSE_KEY, warehouse);
                dialogInputs.putString(UpdateCurrentDialogFragment.DATE_KEY, date);
                dialogInputs.putString(UpdateCurrentDialogFragment.DONOR_KEY, donor);
                dialogInputs.putLong(UpdateCurrentDialogFragment.ID_KEY, currentInventoryId);
                dialog.setArguments(dialogInputs);
                dialog.setTargetFragment(ViewCurrentInventoryDetailFragment.this, 300);
                dialog.show(fragmentManager, "open update current dialog");
            }
        });

        // implement delete button
        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        if (handleCurrentInventoryDeletion()) {
                            FragmentManager manager = getActivity().getSupportFragmentManager();
                            manager.popBackStack();
                            manager.popBackStack();
                            manager.popBackStack();
                        }
                        break;
                }
            }
        };

        Button delete = (Button) rootView.findViewById(R.id.view_current_inventory_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // call dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage(R.string.are_you_sure_message)
                        .setPositiveButton(R.string.are_you_sure_yes, dialogClickListener)
                        .setNegativeButton(R.string.are_you_sure_no, dialogClickListener)
                        .show();
            }
        });

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
            String category = data.getString(COL_CATEGORY_NAME);
            int value = data.getInt(COL_ITEM_VALUE);
            String valueString = "" + value;
            String barcode = data.getString(COL_BARCODE_COMPLETE);
            quantity = data.getInt(COL_CURRENT_QTY);
            String quantityString = "" + quantity;
            date = data.getString(COL_CURRENT_DATE_RECEIVED);
            donor = data.getString(COL_CURRENT_DONOR);
            warehouse = data.getString(COL_CURRENT_WAREHOUSE);
            currentInventoryId = data.getLong(COL_CURRENT_ID);

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

    private boolean handleCurrentInventoryDeletion() {
        boolean deleted = false;
        int rowDeleted = -1;
        Uri currentInventoryUri = InventoryContract.CurrentInventoryEntry.buildCurrentInventoryUri();
        String selection = InventoryContract.CurrentInventoryEntry.TABLE_NAME + "." +
                InventoryContract.CurrentInventoryEntry._ID + " = ? ";
        String[] selectionArgs = {Long.toString(currentInventoryId)};
        String message = getContext().getResources().getString(R.string.current_inventory_delete_fail);

        if (currentInventoryId != -1) {
            rowDeleted = getContext().getContentResolver().delete(
                    currentInventoryUri,
                    selection,
                    selectionArgs
            );
        }

        if (rowDeleted != 0) {
            message = getContext().getResources().getString(R.string.current_inventory_delete_success);
            deleted = true;
        }

        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        return deleted;
    }
}