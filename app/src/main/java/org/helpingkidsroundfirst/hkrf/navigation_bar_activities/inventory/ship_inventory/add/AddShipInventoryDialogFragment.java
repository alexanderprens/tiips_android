package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.ship_inventory.add;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.zxing.client.android.CaptureActivity;

import org.helpingkidsroundfirst.hkrf.R;
import org.helpingkidsroundfirst.hkrf.data.InventoryContract;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by alexa on 2/15/2017.
 */

public class AddShipInventoryDialogFragment extends DialogFragment implements
        View.OnClickListener {

    private static final String[] BARCODE_AUTOCOMPLETE = {
            InventoryContract.ItemEntry.COLUMN_BARCODE_ID
    };
    private static final String[] projection = {InventoryContract.CurrentInventoryEntry.TABLE_NAME + "." +
            InventoryContract.CurrentInventoryEntry._ID + " AS _id",
            InventoryContract.CurrentInventoryEntry.COLUMN_QTY,
            InventoryContract.CurrentInventoryEntry.COLUMN_DONOR,
            InventoryContract.ItemEntry.COLUMN_BARCODE_ID,
            InventoryContract.ItemEntry.COLUMN_DESCRIPTION,
            InventoryContract.ItemEntry.COLUMN_NAME,
            InventoryContract.ItemEntry.COLUMN_VALUE,
            InventoryContract.ItemEntry.COLUMN_CATEGORY_KEY,
            InventoryContract.CurrentInventoryEntry.COLUMN_DATE_RECEIVED,
            InventoryContract.CurrentInventoryEntry.COLUMN_WAREHOUSE
    };
    private static final int COL_CURRENT_ID = 0;
    private static final int COL_CURRENT_QTY = 1;
    private static final int COL_CURRENT_DONOR = 2;
    private static final int COL_BARCODE_ID = 3;
    private static final int COL_DESCRIPTION = 4;
    private static final int COL_NAME = 5;
    private static final int COL_VALUE = 6;
    private static final int COL_CATEGORY_KEY = 7;
    private static int[] BARCODE_TO_VIEW = {
            android.R.id.text1
    };
    private int quantity;
    private long itemId;
    private String qtyString;
    private String error;
    private AddShipDialogListener caller;
    private Spinner barcodeSpinner;
    private boolean override;
    private Cursor cursor;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            caller = (AddShipDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Must implement AddShipDialogListener listener");
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use builder to create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_ship_inventory, null);

        // set click listeners on buttons
        view.findViewById(R.id.add_ship_button_ok).setOnClickListener(this);
        view.findViewById(R.id.add_ship_button_cancel).setOnClickListener(this);
        override = false;

        // get barcode scanner working
        Button buttonScan = (Button) view.findViewById(R.id.add_ship_scan_button);
        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // scan barcodes
                Intent intent = new Intent(getActivity(), CaptureActivity.class);
                intent.setAction("com.google.zxing.client.android.SCAN")
                        .putExtra("SAVE_HISTORY", false);
                startActivityForResult(intent, 0);
            }
        });

        // listen to barcode spinner
        barcodeSpinner = (Spinner) view.findViewById(R.id.add_ship_barcode_spinner);

        cursor = getContext().getContentResolver().query(
                InventoryContract.ItemEntry.CONTENT_URI,
                new String[]{InventoryContract.ItemEntry.TABLE_NAME + "." +
                        InventoryContract.ItemEntry._ID + " AS _id",
                        InventoryContract.ItemEntry.COLUMN_BARCODE_ID},
                null,
                null,
                InventoryContract.ItemEntry.COLUMN_BARCODE_ID
        );

        final SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                getContext(),
                android.R.layout.simple_spinner_item,
                cursor,
                BARCODE_AUTOCOMPLETE,
                BARCODE_TO_VIEW,
                0
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        barcodeSpinner.setAdapter(adapter);
        barcodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                itemId = barcodeSpinner.getSelectedItemId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // listen to qty input
        qtyString = "";
        final EditText qtyText = (EditText) view.findViewById(R.id.add_ship_qty);
        qtyText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // required stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                qtyString = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // required stub
            }
        });

        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_ship_button_ok:
                if (addShipInventory()) {
                    caller.onButtonOK();
                    // toast successful
                    Toast.makeText(getContext(), getContext().getResources()
                            .getString(R.string.add_ship_inventory_success), Toast.LENGTH_SHORT).show();
                    this.dismiss();

                } else {
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.add_ship_button_cancel:
                this.dismiss();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                // get barcode from scanner
                String contents = data.getStringExtra("SCAN_RESULT");

                // check if barcode exists
                long id = checkIfItemExistsGivenString(contents);
                if (id != -1) {

                    // if barcode exists, set new id
                    itemId = id;
                    int position = -1;

                    // find position in cursor
                    for (int i = 0; i < cursor.getCount(); i++) {
                        cursor.moveToPosition(i);
                        String temp = cursor.getString(1);
                        if (temp.contentEquals(contents)) {
                            position = i;
                            break;
                        }
                    }
                    if (position != -1) {
                        barcodeSpinner.setSelection(position);
                    }
                } else {
                    Toast.makeText(getContext(), getContext().getResources().getString(
                            R.string.error_barcode_non_existant), Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getContext(), "RESULT_CANCELED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean addShipInventory() {
        boolean added = false;

        // validates inputs
        if (dialogValidation()) {

            // check if item exists
            if (checkIfItemExists()) {

                // attempt to pull item from current inventory
                if (checkQtyInCurrentInventory()) {
                    added = true;

                } else {
                    error = getContext().getResources().getString(R.string.not_enough_qty);
                }
            } else {
                error = getContext().getResources().getString(R.string.error_barcode_non_existant);
            }
        } else {
            error = getContext().getResources().getString(R.string.validation_error);
        }

        return added;
    }

    private boolean dialogValidation() {
        boolean check = true;

        // check quantity
        if (qtyString.isEmpty()) {
            check = false;
            Toast.makeText(getContext(), getContext().getResources()
                    .getString(R.string.validation_qty_null), Toast.LENGTH_SHORT).show();
        } else {
            try {
                quantity = Integer.parseInt(qtyString);

                if (quantity < 1) {
                    check = false;
                    Toast.makeText(getContext(), getContext().getResources()
                            .getString(R.string.validation_qty_zero), Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                check = false;
                Toast.makeText(getContext(), getContext().getResources()
                        .getString(R.string.validation_qty_zero), Toast.LENGTH_SHORT).show();
                quantity = 1;
                qtyString = Integer.toString(quantity);
            }
        }

        return check;
    }

    // checks if item already exists by looking at the barcode string
    private boolean checkIfItemExists() {

        boolean exists;

        // Check if barcode id already exists in the db
        Cursor itemCursor = getContext().getContentResolver().query(
                InventoryContract.ItemEntry.CONTENT_URI,
                new String[]{InventoryContract.ItemEntry.TABLE_NAME + "." +
                        InventoryContract.ItemEntry._ID},
                InventoryContract.ItemEntry.TABLE_NAME + "." +
                        InventoryContract.ItemEntry._ID + " = ?",
                new String[]{Long.toString(itemId)},
                null
        );

        // if barcode exists, return true
        if (itemCursor != null) {
            exists = itemCursor.moveToFirst();

            if (exists) {
                itemId = itemCursor.getLong(0);
            }

            itemCursor.close();
        } else {
            exists = false;
        }

        return exists;
    }

    private boolean checkQtyInCurrentInventory() {
        boolean canAdd = false;
        int quantityAvailable;
        int quantityNeeded = quantity;

        // build query
        Uri uri = InventoryContract.CurrentInventoryEntry.buildCurrentInventoryUri();
        String selection = InventoryContract.CurrentInventoryEntry.COLUMN_ITEM_KEY + " = ? ";
        String[] selectionArgs = {Long.toString(itemId)};
        String sortOrder = InventoryContract.CurrentInventoryEntry.COLUMN_DATE_RECEIVED + " DESC";

        Cursor currentInventoryCursor = getContext().getContentResolver().query(
                uri,
                projection,
                selection,
                selectionArgs,
                sortOrder
        );

        // add up quantity available
        if (currentInventoryCursor != null && currentInventoryCursor.moveToFirst()) {
            quantityAvailable = 0;

            // loop through current inventory results
            do {
                // add quantity
                quantityAvailable += currentInventoryCursor.getInt(COL_CURRENT_QTY);

            } while (currentInventoryCursor.moveToNext());

            if (quantityAvailable < quantityNeeded) {

                // not enough quantity in current inventory
                if (override) {

                    // can pull quantity from current inventory
                    currentInventoryCursor.moveToFirst();

                    // pull quantity from current inventory
                    do {
                        int rowQty = currentInventoryCursor.getInt(COL_CURRENT_QTY);

                        if (!addCurrentInventoryRowToShip(currentInventoryCursor, rowQty)) {
                            return false;
                        }
                        quantityNeeded -= rowQty;

                    } while (currentInventoryCursor.moveToNext());

                    // add what's left of needed quantity in with blank donor
                    canAdd = addShipWithoutCurrentToDb(quantityNeeded);
                } else {
                    overrideDialog();
                }

            } else {
                // can pull quantity from current inventory
                currentInventoryCursor.moveToFirst();

                // pull quantity from current inventory
                do {
                    int rowQty = currentInventoryCursor.getInt(COL_CURRENT_QTY);

                    if (rowQty <= quantityNeeded) {
                        if (!addCurrentInventoryRowToShip(currentInventoryCursor, rowQty)) {
                            return false;
                        }
                        quantityNeeded -= rowQty;

                    } else {
                        int shipQty = rowQty - quantityNeeded;

                        if (!moveQtyFromCurrentToShip(currentInventoryCursor, shipQty, quantityNeeded)) {
                            return false;
                        }

                        quantityNeeded = 0;
                    }

                    if (quantityNeeded == 0) {
                        canAdd = true;
                        break;
                    }
                } while (currentInventoryCursor.moveToNext());
            }
            currentInventoryCursor.close();
        } else {

            // does not exist in current inventory
            if (override) {
                canAdd = addShipWithoutCurrentToDb(quantityNeeded);
            } else {
                overrideDialog();
            }
        }

        error = getContext().getResources().getString(R.string.error_adding_receive);
        return canAdd;
    }

    private boolean addCurrentInventoryRowToShip(Cursor cursor, int shipQty) {
        boolean shipped = false;

        // get content values for ship inventory
        ContentValues contentValues = new ContentValues();
        DatabaseUtils.cursorStringToContentValues(
                cursor,
                InventoryContract.ItemEntry.COLUMN_BARCODE_ID,
                contentValues,
                InventoryContract.ShipInventoryEntry.COLUMN_BARCODE_ID
        );

        DatabaseUtils.cursorStringToContentValues(
                cursor,
                InventoryContract.ItemEntry.COLUMN_NAME,
                contentValues,
                InventoryContract.ShipInventoryEntry.COLUMN_NAME
        );

        DatabaseUtils.cursorStringToContentValues(
                cursor,
                InventoryContract.ItemEntry.COLUMN_DESCRIPTION,
                contentValues,
                InventoryContract.ShipInventoryEntry.COLUMN_DESCRIPTION
        );

        DatabaseUtils.cursorLongToContentValues(
                cursor,
                InventoryContract.ItemEntry.COLUMN_CATEGORY_KEY,
                contentValues,
                InventoryContract.ShipInventoryEntry.COLUMN_CATEGORY_KEY
        );

        DatabaseUtils.cursorIntToContentValues(
                cursor,
                InventoryContract.ItemEntry.COLUMN_VALUE,
                contentValues,
                InventoryContract.ShipInventoryEntry.COLUMN_VALUE
        );

        DatabaseUtils.cursorStringToContentValues(
                cursor,
                InventoryContract.CurrentInventoryEntry.COLUMN_DONOR,
                contentValues,
                InventoryContract.ShipInventoryEntry.COLUMN_DONOR
        );

        contentValues.put(InventoryContract.ShipInventoryEntry.COLUMN_QTY, shipQty);

        // attempt to insert into ship inventory
        if (insertShipInventory(contentValues, shipQty)) {

            // now delete current inventory from database
            int rowsDeleted;
            Uri uri = InventoryContract.CurrentInventoryEntry.buildCurrentInventoryUri();
            String selection = InventoryContract.CurrentInventoryEntry.TABLE_NAME + "." +
                    InventoryContract.CurrentInventoryEntry._ID + " = ? ";
            String[] selectionArgs = {Long.toString(cursor.getLong(COL_CURRENT_ID))};

            rowsDeleted = getContext().getContentResolver().delete(
                    uri,
                    selection,
                    selectionArgs
            );

            if (rowsDeleted > 0) {
                shipped = true;
            }
        }

        return shipped;
    }

    private boolean moveQtyFromCurrentToShip(Cursor cursor, int currentQty, int shipQty) {
        boolean shipped = false;

        // get content values for ship inventory
        ContentValues contentValues = new ContentValues();
        DatabaseUtils.cursorStringToContentValues(
                cursor,
                InventoryContract.ItemEntry.COLUMN_BARCODE_ID,
                contentValues,
                InventoryContract.ShipInventoryEntry.COLUMN_BARCODE_ID
        );

        DatabaseUtils.cursorStringToContentValues(
                cursor,
                InventoryContract.ItemEntry.COLUMN_NAME,
                contentValues,
                InventoryContract.ShipInventoryEntry.COLUMN_NAME
        );

        DatabaseUtils.cursorStringToContentValues(
                cursor,
                InventoryContract.ItemEntry.COLUMN_DESCRIPTION,
                contentValues,
                InventoryContract.ShipInventoryEntry.COLUMN_DESCRIPTION
        );

        DatabaseUtils.cursorLongToContentValues(
                cursor,
                InventoryContract.ItemEntry.COLUMN_CATEGORY_KEY,
                contentValues,
                InventoryContract.ShipInventoryEntry.COLUMN_CATEGORY_KEY
        );

        DatabaseUtils.cursorIntToContentValues(
                cursor,
                InventoryContract.ItemEntry.COLUMN_VALUE,
                contentValues,
                InventoryContract.ShipInventoryEntry.COLUMN_VALUE
        );

        DatabaseUtils.cursorStringToContentValues(
                cursor,
                InventoryContract.CurrentInventoryEntry.COLUMN_DONOR,
                contentValues,
                InventoryContract.ShipInventoryEntry.COLUMN_DONOR
        );

        DatabaseUtils.cursorStringToContentValues(
                cursor,
                InventoryContract.CurrentInventoryEntry.COLUMN_DATE_RECEIVED,
                contentValues,
                InventoryContract.ShipInventoryEntry.COLUMN_DATE_RECEIVED
        );

        DatabaseUtils.cursorStringToContentValues(
                cursor,
                InventoryContract.CurrentInventoryEntry.COLUMN_WAREHOUSE,
                contentValues,
                InventoryContract.ShipInventoryEntry.COLUMN_WAREHOUSE
        );

        contentValues.put(InventoryContract.ShipInventoryEntry.COLUMN_QTY, shipQty);

        // attempt to ship inventory
        if (insertShipInventory(contentValues, shipQty)) {

            // now delete current inventory from database
            int rowsUpdated;
            Uri uri = InventoryContract.CurrentInventoryEntry.buildCurrentInventoryUri();
            String selection = InventoryContract.CurrentInventoryEntry.TABLE_NAME + "." +
                    InventoryContract.CurrentInventoryEntry._ID + " = ? ";
            String[] selectionArgs = {Long.toString(cursor.getLong(COL_CURRENT_ID))};

            // new quantity
            ContentValues currentValues = new ContentValues();
            currentValues.put(InventoryContract.CurrentInventoryEntry.COLUMN_QTY, currentQty);

            rowsUpdated = getContext().getContentResolver().update(
                    uri,
                    currentValues,
                    selection,
                    selectionArgs
            );

            if (rowsUpdated > 0) {
                shipped = true;
            }
        }

        return shipped;
    }

    private boolean addShipWithoutCurrentToDb(int shipQty) {
        boolean added = false;

        // get cursor of item
        Uri itemUri = InventoryContract.ItemEntry.buildInventoryItemUri();
        String selection = InventoryContract.ItemEntry.TABLE_NAME + "." +
                InventoryContract.ItemEntry._ID + " = ? ";
        String[] selectionArgs = {Long.toString(itemId)};

        Cursor itemCursor = getContext().getContentResolver().query(
                itemUri,
                null,
                selection,
                selectionArgs,
                null
        );

        if (itemCursor != null && itemCursor.moveToFirst()) {

            // get values from item
            ContentValues contentValues = new ContentValues();
            DatabaseUtils.cursorStringToContentValues(
                    itemCursor,
                    InventoryContract.ItemEntry.COLUMN_BARCODE_ID,
                    contentValues,
                    InventoryContract.ShipInventoryEntry.COLUMN_BARCODE_ID
            );

            DatabaseUtils.cursorStringToContentValues(
                    itemCursor,
                    InventoryContract.ItemEntry.COLUMN_NAME,
                    contentValues,
                    InventoryContract.ShipInventoryEntry.COLUMN_NAME
            );

            DatabaseUtils.cursorStringToContentValues(
                    itemCursor,
                    InventoryContract.ItemEntry.COLUMN_DESCRIPTION,
                    contentValues,
                    InventoryContract.ShipInventoryEntry.COLUMN_DESCRIPTION
            );

            DatabaseUtils.cursorLongToContentValues(
                    itemCursor,
                    InventoryContract.ItemEntry.COLUMN_CATEGORY_KEY,
                    contentValues,
                    InventoryContract.ShipInventoryEntry.COLUMN_CATEGORY_KEY
            );

            DatabaseUtils.cursorIntToContentValues(
                    itemCursor,
                    InventoryContract.ItemEntry.COLUMN_VALUE,
                    contentValues,
                    InventoryContract.ShipInventoryEntry.COLUMN_VALUE
            );

            contentValues.put(InventoryContract.ShipInventoryEntry.COLUMN_QTY, shipQty);
            contentValues.put(InventoryContract.ShipInventoryEntry.COLUMN_DONOR, "Unknown");

            // attempt to insert into ship inventory
            if (insertShipInventory(contentValues, shipQty)) {
                added = true;
            }

            itemCursor.close();
        }

        return added;
    }

    private boolean insertShipInventory(ContentValues inputValues, int shipQty) {
        boolean inserted = false;

        // check if already in current inventory
        Uri shipUri = InventoryContract.ShipInventoryEntry.buildShipInventoryUri();
        String[] projection = {InventoryContract.ShipInventoryEntry.TABLE_NAME + "." +
                InventoryContract.ShipInventoryEntry._ID + " AS _id",
                InventoryContract.ShipInventoryEntry.COLUMN_QTY
        };
        String selection = InventoryContract.ShipInventoryEntry.COLUMN_BARCODE_ID + " = ? " +
                "AND " + InventoryContract.ShipInventoryEntry.COLUMN_DONOR + " = ? ";
        String[] selectionArgs = {
                inputValues.getAsString(InventoryContract.ShipInventoryEntry.COLUMN_BARCODE_ID),
                inputValues.getAsString(InventoryContract.ShipInventoryEntry.COLUMN_DONOR)
        };

        Cursor shipCursor = getContext().getContentResolver().query(
                shipUri,
                projection,
                selection,
                selectionArgs,
                null
        );

        if (shipCursor != null && shipCursor.moveToFirst()) {

            // get old qty
            int newQty = shipCursor.getInt(1) + shipQty;

            // make new qty
            ContentValues newValues = new ContentValues();
            newValues.put(InventoryContract.ShipInventoryEntry.COLUMN_QTY, newQty);

            // update ship entry
            selection = InventoryContract.ShipInventoryEntry.TABLE_NAME + "." +
                    InventoryContract.ShipInventoryEntry._ID + " = ? ";
            String[] selectionArgs2 = {Long.toString(shipCursor.getLong(0))};

            // attempt insert
            int rowsUpdated = getContext().getContentResolver().update(
                    shipUri,
                    newValues,
                    selection,
                    selectionArgs2
            );

            // check if updated
            if (rowsUpdated > 0) {
                inserted = true;
            }

            shipCursor.close();

        } else {

            // attempt to insert into ship inventory
            Uri uri = InventoryContract.ShipInventoryEntry.buildShipInventoryUri();
            Uri insertedUri;

            insertedUri = getContext().getContentResolver().insert(
                    uri,
                    inputValues
            );

            if (ContentUris.parseId(insertedUri) != -1) {
                inserted = true;
            }
        }

        return inserted;
    }

    private void overrideDialog() {

        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        setOverride(true);
                        //dialog.dismiss();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        setOverride(false);
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.override_current_inventory)
                .setPositiveButton(R.string.are_you_sure_yes, dialogClickListener)
                .setNegativeButton(R.string.are_you_sure_no, dialogClickListener)
                .show();
    }

    // checks if item already exists by looking at the barcode string
    private long checkIfItemExistsGivenString(String barcodeString) {

        long barcodeId = -1;

        // Check if barcode id already exists in the db
        Cursor itemCursor = getContext().getContentResolver().query(
                InventoryContract.ItemEntry.CONTENT_URI,
                new String[]{InventoryContract.ItemEntry.TABLE_NAME + "." +
                        InventoryContract.ItemEntry._ID},
                InventoryContract.ItemEntry.COLUMN_BARCODE_ID + " = ?",
                new String[]{barcodeString},
                null
        );

        // if barcode exists, return true
        if (itemCursor != null && itemCursor.moveToFirst()) {

            barcodeId = itemCursor.getLong(0);
            itemCursor.close();
        }

        return barcodeId;
    }

    private void setOverride(boolean bool) {
        override = bool;
    }

    public interface AddShipDialogListener {
        void onButtonOK();
    }
}
