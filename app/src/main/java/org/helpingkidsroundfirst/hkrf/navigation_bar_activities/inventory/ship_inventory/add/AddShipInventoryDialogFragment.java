package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.ship_inventory.add;

import android.app.AlertDialog;
import android.app.Dialog;
import android.database.Cursor;
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
 * Created by alexa on 2/15/2017.
 */

public class AddShipInventoryDialogFragment extends DialogFragment implements
        View.OnClickListener {

    private static final String[] BARCODE_AUTOCOMPLETE = {
            InventoryContract.ItemEntry.COLUMN_BARCODE_ID
    };
    private static int[] BARCODE_TO_VIEW = {
            android.R.id.text1
    };
    private int quantity;
    private long itemId;
    private String qtyString;
    private String error;
    private AddShipDialogListener caller;
    private Spinner barcodeSpinner;
    private int quantityAvailable;


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

        // listen to barcode input
        barcodeSpinner = (Spinner) view.findViewById(R.id.add_ship_barcode_spinner);

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

                break;

            case R.id.add_ship_button_cancel:
                this.dismiss();
                break;
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

                    // attempt to add to ship inventoriy table
                    if (attemptAddToShipDb() != -1) {
                        added = true;

                    } else {
                        error = getContext().getResources().getString(R.string.error_adding_receive);
                    }
                } else {
                    error = getContext().getResources().getString(R.string.not_enough_qty);
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
        quantityAvailable = 0;


        return canAdd;
    }

    private long attemptAddToShipDb() {
        long shipId = -1;

        return shipId;
    }

    public interface AddShipDialogListener {
        void onButtonOK();
    }
}
