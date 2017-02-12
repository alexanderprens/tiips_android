package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.receive_inventory;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.helpingkidsroundfirst.hkrf.R;
import org.helpingkidsroundfirst.hkrf.Utility;
import org.helpingkidsroundfirst.hkrf.data.InventoryContract;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by alexa on 2/12/2017.
 */

public class ReceiveInventorySubmitFragment extends DialogFragment implements
        View.OnClickListener {

    private static final String[] RECEIVE_INV_COLUMNS = {
            InventoryContract.ReceiveInventoryEntry.TABLE_NAME + "." +
                    InventoryContract.ReceiveInventoryEntry._ID + " AS _id",
            InventoryContract.ReceiveInventoryEntry.COLUMN_ITEM_KEY,
            InventoryContract.ReceiveInventoryEntry.COLUMN_QTY,
            InventoryContract.ReceiveInventoryEntry.COLUMN_DONOR,
            InventoryContract.ReceiveInventoryEntry.COLUMN_WAREHOUSE,
            InventoryContract.ReceiveInventoryEntry.COLUMN_DATE_RECEIVED
    };
    private static final int COL_RECEIVE_ID = 0;
    private static final int COL_RECEIVE_ITEM_KEY = 1;
    private static final int COL_RECEIVE_QTY = 2;
    private static final int COL_RECEIVE_DONOR = 3;
    private static final int COL_RECEIVE_WAREHOUSE = 4;
    private static final int COL_RECEIVE_DATE = 5;
    // dialog inputs
    private String dateString;
    private String donorInput;
    private String warehouseInput;
    private SubmitReceiveListListener caller;
    private String error;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            caller = (SubmitReceiveListListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Must implement SubmitReceiveListListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // use builder class to construct dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_submit_receive_list, null);

        // set click listeners on buttons
        view.findViewById(R.id.submit_receive_dialog_button_ok).setOnClickListener(this);
        view.findViewById(R.id.submit_receive_dialog_button_cancel).setOnClickListener(this);

        // init inputs
        dateString = "";
        donorInput = "";
        warehouseInput = "";

        // listen to donor input
        EditText donorText = (EditText) view.findViewById(R.id.submit_receive_dialog_donor);
        donorText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // required stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                donorInput = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // required stub
            }
        });

        // listen to warehouse input
        EditText warehouseText = (EditText) view.findViewById(R.id.submit_receive_dialog_warehous);
        warehouseText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // required stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                warehouseInput = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // required stub
            }
        });

        // listen to date input
        final TextView dateChosen = (TextView) view.findViewById(R.id.submit_receive_dialog_date_string);

        Button pickDateButton = (Button) view.findViewById(R.id.submit_receive_dialog_date_button);
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
            case R.id.submit_receive_dialog_button_ok:
                if (addItemsToCurrentInventory()) {
                    Toast.makeText(getContext(), getContext()
                            .getString(R.string.message_submit_successful), Toast.LENGTH_SHORT).show();
                    caller.onSubmitButtonClicked();
                    this.dismiss();
                } else {
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.submit_receive_dialog_button_cancel:
                this.dismiss();
                break;
        }
    }

    private boolean addItemsToCurrentInventory() {
        boolean added = false;

        // validate inputs
        if (dialogValidation()) {

            // update receive inventory table with date, donor, warehouse
            if (updateReceiveInventory() != 0) {

                // insert items into current inventory
                if (completeDatabaseInsertion() != 0) {
                    added = true;

                    // delete everything from receive inventory
                    if (deleteReceiveInventory() != 0) {
                        // idk
                    } else {
                        error = getContext().getResources().getString(R.string.error_receive_not_deleted);
                    }
                } else {
                    error = getContext().getResources().getString(R.string.error_inserted_into_current);
                }
            } else {
                error = getContext().getResources().getString(R.string.error_receive_inventory_not_updated);
            }
        } else {
            error = getContext().getResources().getString(R.string.validation_error);
        }

        return added;
    }

    private boolean dialogValidation() {
        boolean check = true;

        if (donorInput.isEmpty()) {
            check = false;
            Toast.makeText(getContext(), getContext().getResources()
                    .getString(R.string.validation_donor_empty), Toast.LENGTH_SHORT).show();
        }

        if (warehouseInput.isEmpty()) {
            check = false;
            Toast.makeText(getContext(), getContext().getResources()
                    .getString(R.string.validation_warehouse_empty), Toast.LENGTH_SHORT).show();
        }

        if (dateString.isEmpty()) {
            check = false;
            Toast.makeText(getContext(), getContext().getResources()
                    .getString(R.string.validation_date_invalid), Toast.LENGTH_SHORT).show();
        }

        return check;
    }

    private int updateReceiveInventory() {
        int updatedRows;

        // get uri
        Uri uri = InventoryContract.ReceiveInventoryEntry.buildReceiveInventoryUri();

        // get new values
        ContentValues newValues = new ContentValues();
        newValues.put(InventoryContract.ReceiveInventoryEntry.COLUMN_DONOR, donorInput);
        newValues.put(InventoryContract.ReceiveInventoryEntry.COLUMN_WAREHOUSE, warehouseInput);
        newValues.put(InventoryContract.ReceiveInventoryEntry.COLUMN_DATE_RECEIVED, dateString);

        // execute update
        updatedRows = getContext().getContentResolver().update(
                uri,
                newValues,
                null,
                null
        );

        return updatedRows;
    }

    private int completeDatabaseInsertion() {
        int insertedRows = 0;

        // get uri
        Uri currentUri = InventoryContract.CurrentInventoryEntry.buildCurrentInventoryUri();
        Uri receiveUri = InventoryContract.ReceiveInventoryEntry.buildReceiveInventoryUri();

        // get values
        Cursor receiveCursor = getContext().getContentResolver().query(
                receiveUri,
                RECEIVE_INV_COLUMNS,
                null,
                null,
                null
        );

        // make content values
        if (receiveCursor != null && receiveCursor.moveToFirst()) {
            ArrayList<ContentValues> values = new ArrayList<>();
            do {
                ContentValues row = new ContentValues();
                DatabaseUtils.cursorLongToContentValues(receiveCursor,
                        InventoryContract.ReceiveInventoryEntry.COLUMN_ITEM_KEY,
                        row,
                        InventoryContract.CurrentInventoryEntry.COLUMN_ITEM_KEY);

                DatabaseUtils.cursorIntToContentValues(receiveCursor,
                        InventoryContract.ReceiveInventoryEntry.COLUMN_QTY,
                        row,
                        InventoryContract.CurrentInventoryEntry.COLUMN_QTY);

                DatabaseUtils.cursorStringToContentValues(receiveCursor,
                        InventoryContract.ReceiveInventoryEntry.COLUMN_DONOR,
                        row,
                        InventoryContract.CurrentInventoryEntry.COLUMN_DONOR);

                DatabaseUtils.cursorStringToContentValues(receiveCursor,
                        InventoryContract.ReceiveInventoryEntry.COLUMN_WAREHOUSE,
                        row,
                        InventoryContract.CurrentInventoryEntry.COLUMN_WAREHOUSE);

                DatabaseUtils.cursorStringToContentValues(receiveCursor,
                        InventoryContract.ReceiveInventoryEntry.COLUMN_DATE_RECEIVED,
                        row,
                        InventoryContract.CurrentInventoryEntry.COLUMN_DATE_RECEIVED);
                values.add(row);
            } while (receiveCursor.moveToNext());
            ContentValues[] cv = new ContentValues[values.size()];
            values.toArray(cv);

            //insert using bulk insert
            insertedRows = getContext().getContentResolver().bulkInsert(
                    currentUri,
                    cv
            );
        }

        return insertedRows;
    }

    private int deleteReceiveInventory() {
        int deleted = 0;

        // make uri
        Uri uri = InventoryContract.ReceiveInventoryEntry.buildReceiveInventoryUri();

        // attempt delete
        deleted = getContext().getContentResolver().delete(
                uri,
                null,
                null
        );

        return deleted;
    }

    public interface SubmitReceiveListListener {
        void onSubmitButtonClicked();
    }
}
