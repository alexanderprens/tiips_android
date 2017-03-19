package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.ship_inventory;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import org.helpingkidsroundfirst.hkrf.R;
import org.helpingkidsroundfirst.hkrf.data.InventoryContract;
import org.helpingkidsroundfirst.hkrf.helper_classes.Utility;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by alexa on 2/17/2017.
 */

public class ShipInventorySubmitDialogFragment extends DialogFragment implements
        View.OnClickListener {
    private static final String[] SHIP_INVENTORY_COLUMNS = {
            InventoryContract.ShipInventoryEntry.TABLE_NAME + "." +
                    InventoryContract.ShipInventoryEntry._ID + " AS _id",
            InventoryContract.ShipInventoryEntry.COLUMN_NAME,
            InventoryContract.ShipInventoryEntry.COLUMN_DESCRIPTION,
            InventoryContract.ShipInventoryEntry.COLUMN_VALUE,
            InventoryContract.ShipInventoryEntry.COLUMN_CATEGORY_KEY,
            InventoryContract.ShipInventoryEntry.COLUMN_BARCODE_ID,
            InventoryContract.ShipInventoryEntry.COLUMN_QTY,
            InventoryContract.ShipInventoryEntry.COLUMN_DONOR,
            InventoryContract.ShipInventoryEntry.COLUMN_DATE_SHIPPED
    };
    // dialog inputs
    private String dateString;
    private String error;
    private SubmitShipListListener caller;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            caller = (SubmitShipListListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Must implement SubmitShipListListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // use builder class to construct dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_ship_inventory_submit, null);

        // set click listeners on buttons
        view.findViewById(R.id.ship_inventory_submit_cancel).setOnClickListener(this);
        view.findViewById(R.id.ship_inventory_submit_ok).setOnClickListener(this);

        // init inputs
        dateString = "";

        // listen to date input
        final TextView dateChosen = (TextView) view.findViewById(R.id.ship_inventory_submit_date_string);

        Button pickDateButton = (Button) view.findViewById(R.id.ship_inventory_submit_date_button);
        pickDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                dateString = Utility.getDatePickerString(year, month, dayOfMonth);
                                dateChosen.setText(dateString);
                            }
                        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
                datePickerDialog.show();
            }
        });

        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ship_inventory_submit_ok:
                if (addItemsToPastInventory()) {
                    Toast.makeText(getContext(), getContext().getResources()
                            .getString(R.string.ship_inventory_submit_success), Toast.LENGTH_SHORT).show();
                    caller.onSubmitButtonClick();
                    this.dismiss();
                } else {
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.ship_inventory_submit_cancel:
                this.dismiss();
                break;
        }
    }

    private boolean addItemsToPastInventory() {
        boolean added = false;

        // check if date has been picked
        if (!dateString.isEmpty()) {

            // update ship table with date
            if (updateShipInventory() != 0) {

                // insert items into past inventory
                if (insertShipmentToPastInventory() != 0) {
                    added = true;

                    // delete new shipment
                    if (deleteShipInventory() != 0) {
                        // idk
                    } else {
                        error = getContext().getResources().getString(R.string.error_deleting_ship);
                    }
                } else {
                    error = getContext().getResources().getString(R.string.error_inserting_past);
                }
            } else {
                error = getContext().getResources().getString(R.string.error_updating_shipment);
            }
        } else {
            error = getContext().getResources().getString(R.string.validation_date_invalid);
        }

        return added;
    }

    private int updateShipInventory() {
        int updatedRows;

        // get uri
        Uri uri = InventoryContract.ShipInventoryEntry.buildShipInventoryUri();

        // get new values
        ContentValues newValues = new ContentValues();
        newValues.put(InventoryContract.ShipInventoryEntry.COLUMN_DATE_SHIPPED, dateString);

        // execute update
        updatedRows = getContext().getContentResolver().update(
                uri,
                newValues,
                null,
                null
        );

        return updatedRows;
    }

    private int insertShipmentToPastInventory() {
        int insertedRows = 0;

        // get uris
        Uri pastUri = InventoryContract.PastInventoryEntry.buildPastInventoryUri();
        Uri shipUri = InventoryContract.ShipInventoryEntry.buildShipInventoryUri();

        // get values
        Cursor shipCursor = getContext().getContentResolver().query(
                shipUri,
                SHIP_INVENTORY_COLUMNS,
                null,
                null,
                null
        );

        // make content values
        if (shipCursor != null && shipCursor.moveToFirst()) {
            ArrayList<ContentValues> values = new ArrayList<>();
            do {
                ContentValues row = new ContentValues();

                // get values for row
                DatabaseUtils.cursorStringToContentValues(
                        shipCursor,
                        InventoryContract.ShipInventoryEntry.COLUMN_BARCODE_ID,
                        row,
                        InventoryContract.PastInventoryEntry.COLUMN_BARCODE_ID
                );

                DatabaseUtils.cursorStringToContentValues(
                        shipCursor,
                        InventoryContract.ShipInventoryEntry.COLUMN_NAME,
                        row,
                        InventoryContract.PastInventoryEntry.COLUMN_NAME
                );

                DatabaseUtils.cursorStringToContentValues(
                        shipCursor,
                        InventoryContract.ShipInventoryEntry.COLUMN_DESCRIPTION,
                        row,
                        InventoryContract.PastInventoryEntry.COLUMN_DESCRIPTION
                );

                DatabaseUtils.cursorLongToContentValues(
                        shipCursor,
                        InventoryContract.ShipInventoryEntry.COLUMN_CATEGORY_KEY,
                        row,
                        InventoryContract.PastInventoryEntry.COLUMN_CATEGORY_KEY
                );

                DatabaseUtils.cursorIntToContentValues(
                        shipCursor,
                        InventoryContract.ShipInventoryEntry.COLUMN_VALUE,
                        row,
                        InventoryContract.PastInventoryEntry.COLUMN_VALUE
                );

                DatabaseUtils.cursorIntToContentValues(
                        shipCursor,
                        InventoryContract.ShipInventoryEntry.COLUMN_QTY,
                        row,
                        InventoryContract.PastInventoryEntry.COLUMN_QTY
                );

                DatabaseUtils.cursorStringToContentValues(
                        shipCursor,
                        InventoryContract.ShipInventoryEntry.COLUMN_DONOR,
                        row,
                        InventoryContract.PastInventoryEntry.COLUMN_DONOR
                );

                DatabaseUtils.cursorStringToContentValues(
                        shipCursor,
                        InventoryContract.ShipInventoryEntry.COLUMN_DATE_SHIPPED,
                        row,
                        InventoryContract.PastInventoryEntry.COLUMN_DATE_SHIPPED
                );

                values.add(row);
            } while (shipCursor.moveToNext());

            ContentValues[] cv = new ContentValues[values.size()];
            values.toArray(cv);

            // insert using bulk insert
            insertedRows = getContext().getContentResolver().bulkInsert(
                    pastUri,
                    cv
            );
        }

        return insertedRows;
    }

    private int deleteShipInventory() {
        int deleted;

        // make uri
        Uri uri = InventoryContract.ShipInventoryEntry.buildShipInventoryUri();

        // attempt delete
        deleted = getContext().getContentResolver().delete(
                uri,
                null,
                null
        );

        return deleted;
    }

    public interface SubmitShipListListener {
        void onSubmitButtonClick();
    }
}
