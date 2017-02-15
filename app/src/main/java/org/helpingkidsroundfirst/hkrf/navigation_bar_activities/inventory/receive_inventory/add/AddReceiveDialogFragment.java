package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.receive_inventory.add;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.helpingkidsroundfirst.hkrf.R;
import org.helpingkidsroundfirst.hkrf.data.InventoryContract;

/**
 * Created by Alex on 2/8/2017.
 */

public class AddReceiveDialogFragment extends DialogFragment implements
        View.OnClickListener {

    private static final String[] BARCODE_AUTOCOMPLETE = {
            InventoryContract.ItemEntry.COLUMN_BARCODE_ID
    };
    private static int[] BARCODE_TO_VIEW = {
            android.R.id.text1
    };

    // dialog inputs
    private String qtyString;
    private int qty;
    private long itemId;
    private AddReceiveDialogListener caller;
    private Spinner barcodeView;
    private String error;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            caller = (AddReceiveDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Must implement AddItemDialogFragment listener");
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // use builder to create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_receive_inventory, null);

        // set click listeners on button
        view.findViewById(R.id.add_receive_button_ok).setOnClickListener(this);
        view.findViewById(R.id.add_receive_button_cancel).setOnClickListener(this);

        // init inputs
        qty = 0;
        qtyString = "";
        itemId = -1;

        // listen to barcodeInput input
        barcodeView = (Spinner) view.findViewById(R.id.add_receive_spinner);

        Cursor cursor = getContext().getContentResolver().query(
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
        barcodeView.setAdapter(adapter);
        barcodeView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                itemId = barcodeView.getSelectedItemId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // listen to quantity input
        final EditText qtyText = (EditText) view.findViewById(R.id.add_receive_qty);
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
            case R.id.add_receive_button_ok:
                if (addReceiveInventory()) {
                    caller.onButtonOK();
                    this.dismiss();
                } else {
                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.add_receive_button_cancel:
                this.dismiss();
                break;
        }
    }

    private boolean addReceiveInventory() {
        boolean added = false;

        // validate inputs
        if (dialogValidation()) {

            // check if barcode exists
            if (checkIfItemExists()) {

                // check if barcode already in table
                if (checkIfBarcodeExistsInReceiveTable()) {
                    added = true;
                } else {

                    // attempt to add to receive inventory table
                    if (addReceiveToDb() != -1) {
                        added = true;
                    } else {
                        error = getContext().getResources().getString(R.string.error_adding_receive);
                    }
                }
            } else {
                error = getContext().getResources().getString(R.string.error_barcode_nonexistant);
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
            qty = Integer.parseInt(qtyString);

            if (qty < 1) {
                check = false;
                Toast.makeText(getContext(), getContext().getResources()
                        .getString(R.string.validation_qty_zero), Toast.LENGTH_SHORT).show();
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
        exists = itemCursor.moveToFirst();

        if (exists) {
            itemId = itemCursor.getLong(0);
        }

        itemCursor.close();
        return exists;
    }

    private long addReceiveToDb() {
        long receiveId;
        Uri insertedUri;

        // get values together
        qty = Integer.parseInt(qtyString);
        ContentValues receiveValues = new ContentValues();

        // put qty into content values
        receiveValues.put(InventoryContract.ReceiveInventoryEntry.COLUMN_QTY, qty);
        receiveValues.put(InventoryContract.ReceiveInventoryEntry.COLUMN_ITEM_KEY, itemId);

        // insert item into receive
        insertedUri = getContext().getContentResolver().insert(
                InventoryContract.ReceiveInventoryEntry.CONTENT_URI,
                receiveValues
        );

        receiveId = ContentUris.parseId(insertedUri);

        return receiveId;
    }

    private boolean checkIfBarcodeExistsInReceiveTable() {
        boolean exists = false;
        long receiveId;
        int oldQty;
        int rowsUpdated;

        Uri uri = InventoryContract.ReceiveInventoryEntry.buildReceiveInventoryUri();
        String[] projection = {InventoryContract.ReceiveInventoryEntry.TABLE_NAME +
                "." + InventoryContract.ReceiveInventoryEntry._ID,
                InventoryContract.ReceiveInventoryEntry.COLUMN_QTY};
        String selection = InventoryContract.ReceiveInventoryEntry.COLUMN_ITEM_KEY + " = ?";
        String[] selectionArgs = {Long.toString(itemId)};

        Cursor cursor = getContext().getContentResolver().query(
                uri,
                projection,
                selection,
                selectionArgs,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            exists = true;
            receiveId = cursor.getLong(0);
            oldQty = cursor.getInt(1);
            String updateSelection = InventoryContract.ReceiveInventoryEntry.TABLE_NAME + "."
                    + InventoryContract.ReceiveInventoryEntry._ID + " = ? ";

            ContentValues updateQty = new ContentValues();
            updateQty.put(InventoryContract.ReceiveInventoryEntry.COLUMN_QTY, qty + oldQty);

            rowsUpdated = getContext().getContentResolver().update(
                    uri,
                    updateQty,
                    updateSelection,
                    new String[]{Long.toString(receiveId)}
            );

            if (rowsUpdated != 0) {
                Toast.makeText(getContext(), getContext().getResources()
                        .getString(R.string.updated_qty), Toast.LENGTH_SHORT).show();
            }

            cursor.close();
        }

        return exists;
    }

    public interface AddReceiveDialogListener {
        void onButtonOK();
    }
}
