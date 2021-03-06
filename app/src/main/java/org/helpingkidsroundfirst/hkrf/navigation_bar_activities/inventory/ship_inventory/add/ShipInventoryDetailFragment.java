package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.ship_inventory.add;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
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
 * Created by alexa on 2/17/2017.
 */

public class ShipInventoryDetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String DETAILED_SHIP_KEY = "ship_uri";
    public static final int SHIP_DETAIL_LOADER = 12;
    private static final String[] SHIP_COLUMNS = {
            InventoryContract.ShipInventoryEntry.TABLE_NAME + "." +
                    InventoryContract.ShipInventoryEntry._ID + " AS _id",
            InventoryContract.ShipInventoryEntry.COLUMN_NAME,
            InventoryContract.ShipInventoryEntry.COLUMN_DESCRIPTION,
            InventoryContract.ShipInventoryEntry.COLUMN_BARCODE_ID,
            InventoryContract.ShipInventoryEntry.COLUMN_VALUE,
            InventoryContract.ShipInventoryEntry.COLUMN_QTY,
            InventoryContract.CategoryEntry.COLUMN_CATEGORY,
            InventoryContract.ShipInventoryEntry.COLUMN_DONOR,
            InventoryContract.ShipInventoryEntry.COLUMN_DATE_RECEIVED,
            InventoryContract.ShipInventoryEntry.COLUMN_WAREHOUSE
    };
    private static final int COL_SHIP_ID = 0;
    private static final int COL_NAME = 1;
    private static final int COL_DESC = 2;
    private static final int COL_BARCODE = 3;
    private static final int COL_VALUE = 4;
    private static final int COL_QTY = 5;
    private static final int COL_CATEGORY = 6;
    private static final int COL_DONOR = 7;
    private TextView nameView;
    private TextView descriptionView;
    private TextView categoryView;
    private TextView valueView;
    private TextView barcodeView;
    private TextView qtyView;
    private TextView donorView;
    private Uri mUri;
    private long shipInventoryId;

    public ShipInventoryDetailFragment() {
        // required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_ship_inventory_detail,
                container, false);

        // get id
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mUri = bundle.getParcelable(DETAILED_SHIP_KEY);
        }

        // assign text views
        nameView = (TextView) rootView.findViewById(R.id.ship_inventory_detail_name);
        descriptionView = (TextView) rootView.findViewById(R.id.ship_inventory_detail_description);
        categoryView = (TextView) rootView.findViewById(R.id.ship_inventory_detail_category);
        valueView = (TextView) rootView.findViewById(R.id.ship_inventory_detail_value);
        barcodeView = (TextView) rootView.findViewById(R.id.ship_inventory_detail_barcode);
        qtyView = (TextView) rootView.findViewById(R.id.ship_inventory_detail_qty);
        donorView = (TextView) rootView.findViewById(R.id.ship_inventory_detail_donor);

        // implement delete button
        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        if (handleShipItemDelete()) {
                            FragmentManager manager = getActivity().getSupportFragmentManager();
                            manager.popBackStack();
                            manager.popBackStack();
                        }
                        break;
                }
            }
        };

        Button delete = (Button) rootView.findViewById(R.id.ship_inventory_detail_delete);
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
        getLoaderManager().initLoader(SHIP_DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mUri != null) {

            // get cursor of item
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    SHIP_COLUMNS,
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
            // read data from cursor
            String name = data.getString(COL_NAME);
            String description = data.getString(COL_DESC);
            String category = data.getString(COL_CATEGORY);
            int value = data.getInt(COL_VALUE);
            String valueString = "" + value;
            String barcode = data.getString(COL_BARCODE);
            int currentQty = data.getInt(COL_QTY);
            String quantityString = "" + currentQty;
            shipInventoryId = data.getLong(COL_SHIP_ID);
            String donor = data.getString(COL_DONOR);

            // place data into text views
            nameView.setText(name);
            descriptionView.setText(description);
            categoryView.setText(category);
            valueView.setText(valueString);
            barcodeView.setText(barcode);
            qtyView.setText(quantityString);
            donorView.setText(donor);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private boolean handleShipItemDelete() {
        boolean deleted = false;

        // delete overriden rows
        int rowsDeleted = 0;
        Uri shipInventoryUri = InventoryContract.ShipInventoryEntry.buildShipInventoryUri();
        String selection = InventoryContract.ShipInventoryEntry.TABLE_NAME + "." +
                InventoryContract.ShipInventoryEntry._ID + " = ? AND (" +
                InventoryContract.ShipInventoryEntry.COLUMN_DONOR + " = ? OR " +
                InventoryContract.ShipInventoryEntry.COLUMN_DONOR + " IS NULL OR " +
                InventoryContract.ShipInventoryEntry.COLUMN_DONOR + " = ? ) ";
        String[] selectionArgs = {Long.toString(shipInventoryId),
                "Unknown",
                "Test"
        };
        String message = getContext().getResources().getString(R.string.ship_inv_delete_fail);

        if (shipInventoryId != -1) {
            rowsDeleted = getContext().getContentResolver().delete(
                    shipInventoryUri,
                    selection,
                    selectionArgs
            );
        }

        if (rowsDeleted != 0) {
            deleted = true;
        }

        // move not-overridden rows back to current inventory
        selection = InventoryContract.ShipInventoryEntry.TABLE_NAME + "." +
                InventoryContract.ShipInventoryEntry._ID + " = ? AND " +
                InventoryContract.ShipInventoryEntry.COLUMN_DONOR + " IS NOT NULL";
        String[] selectionArgs2 = {Long.toString(shipInventoryId)};

        Cursor shipCursor = getContext().getContentResolver().query(
                shipInventoryUri,
                SHIP_COLUMNS,
                selection,
                selectionArgs2,
                null
        );

        if (shipCursor != null && shipCursor.moveToFirst()) {

            // get content values
            ContentValues shipValues = new ContentValues();

            DatabaseUtils.cursorStringToContentValues(
                    shipCursor,
                    InventoryContract.ShipInventoryEntry.COLUMN_DONOR,
                    shipValues,
                    InventoryContract.CurrentInventoryEntry.COLUMN_DONOR
            );

            DatabaseUtils.cursorStringToContentValues(
                    shipCursor,
                    InventoryContract.ShipInventoryEntry.COLUMN_DATE_RECEIVED,
                    shipValues,
                    InventoryContract.CurrentInventoryEntry.COLUMN_DATE_RECEIVED
            );

            DatabaseUtils.cursorStringToContentValues(
                    shipCursor,
                    InventoryContract.ShipInventoryEntry.COLUMN_WAREHOUSE,
                    shipValues,
                    InventoryContract.CurrentInventoryEntry.COLUMN_WAREHOUSE
            );

            DatabaseUtils.cursorIntToContentValues(
                    shipCursor,
                    InventoryContract.ShipInventoryEntry.COLUMN_QTY,
                    shipValues,
                    InventoryContract.CurrentInventoryEntry.COLUMN_QTY
            );

            long itemKey = getItemKeyFromBarcode(shipCursor.getString(COL_BARCODE));

            if (itemKey != -1) {

                // insert/update shipment into current
                shipValues.put(InventoryContract.CurrentInventoryEntry.COLUMN_ITEM_KEY,
                        itemKey);

                deleted = addShipBackToCurrent(shipValues);

            } else {
                deleted = false;
            }

            shipCursor.close();
        }

        if (deleted) {
            message = getContext().getResources().getString(R.string.ship_inv_delete_success);
        }

        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        return deleted;
    }

    private long getItemKeyFromBarcode(String barcodeId) {

        long itemKey = -1;

        // Check if barcode id already exists in the db
        Cursor itemCursor = getContext().getContentResolver().query(
                InventoryContract.ItemEntry.CONTENT_URI,
                new String[]{InventoryContract.ItemEntry.TABLE_NAME + "." +
                        InventoryContract.ItemEntry._ID},
                InventoryContract.ItemEntry.COLUMN_BARCODE_ID + " = ?",
                new String[]{barcodeId},
                null
        );

        // if barcode exists, return true
        if (itemCursor != null && itemCursor.moveToFirst()) {

            itemKey = itemCursor.getLong(0);
            itemCursor.close();
        }

        return itemKey;
    }

    private boolean addShipBackToCurrent(ContentValues contentValues) {
        boolean added;
        int updated = 0;
        long inserted = 0;

        // query if item exists in current inventory
        String[] projection = {InventoryContract.CurrentInventoryEntry.TABLE_NAME +
                "." + InventoryContract.CurrentInventoryEntry._ID + " AS _id",
                InventoryContract.CurrentInventoryEntry.COLUMN_QTY
        };
        String selection = InventoryContract.CurrentInventoryEntry.COLUMN_ITEM_KEY + " = ? AND " +
                InventoryContract.CurrentInventoryEntry.COLUMN_DONOR + " = ? AND " +
                InventoryContract.CurrentInventoryEntry.COLUMN_WAREHOUSE + " = ? AND " +
                InventoryContract.CurrentInventoryEntry.COLUMN_DATE_RECEIVED + " = ? ";
        String[] selectionArgs = {contentValues
                .getAsString(InventoryContract.CurrentInventoryEntry.COLUMN_ITEM_KEY),
                contentValues.getAsString(InventoryContract.CurrentInventoryEntry.COLUMN_DONOR),
                contentValues.getAsString(InventoryContract.CurrentInventoryEntry.COLUMN_WAREHOUSE),
                contentValues.getAsString(InventoryContract.CurrentInventoryEntry.COLUMN_DATE_RECEIVED)
        };
        Uri currentUri = InventoryContract.CurrentInventoryEntry.buildCurrentInventoryUri();

        Cursor currentCursor = getContext().getContentResolver().query(
                currentUri,
                projection,
                selection,
                selectionArgs,
                null
        );

        if (currentCursor != null && currentCursor.moveToFirst()) {

            // exists in current inventory, update quantity
            int quantity = contentValues.getAsInteger(
                    InventoryContract.CurrentInventoryEntry.COLUMN_QTY) +
                    currentCursor.getInt(1);

            ContentValues updateValues = new ContentValues();
            updateValues.put(InventoryContract.CurrentInventoryEntry.COLUMN_QTY, quantity);

            updated = getContext().getContentResolver().update(
                    currentUri,
                    updateValues,
                    selection,
                    selectionArgs
            );

            currentCursor.close();
        } else {

            // does not exist in current inventory, insert
            Uri insertedUri = getContext().getContentResolver().insert(
                    currentUri,
                    contentValues
            );

            inserted = ContentUris.parseId(insertedUri);

        }

        added = inserted != 0 | updated != 0;

        if (added) {
            int rowsDeleted = 0;
            Uri shipInventoryUri = InventoryContract.ShipInventoryEntry.buildShipInventoryUri();
            selection = InventoryContract.ShipInventoryEntry.TABLE_NAME + "." +
                    InventoryContract.ShipInventoryEntry._ID + " = ? ";
            String[] selectionArgs2 = {Long.toString(shipInventoryId)};

            if (shipInventoryId != -1) {
                rowsDeleted = getContext().getContentResolver().delete(
                        shipInventoryUri,
                        selection,
                        selectionArgs2
                );
            }

            added = rowsDeleted != 0;
        }

        return added;
    }
}
