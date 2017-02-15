package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory.inventory_items;

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
import android.widget.TextView;
import android.widget.Toast;

import org.helpingkidsroundfirst.hkrf.R;
import org.helpingkidsroundfirst.hkrf.data.InventoryContract;

/**
 * Created by Alex on 1/16/2017.
 */

public class AddItemDialogFragment extends DialogFragment implements
        View.OnClickListener {

    // category columns
    private static final String[] CATEGORY_COLUMNS = {
            InventoryContract.CategoryEntry.COLUMN_CATEGORY
    };
    private static int[] TO_VIEWS = {
            android.R.id.text1
    };
    // dialog inputs
    private String nameInput;
    private String descInput;
    private long categoryInput;
    private String valueInString;
    private int valueInput;
    private String barcodeInput;
    private String error;
    private AddItemDialogListener caller;
    private Spinner categorySpinner;
    private String barcodePrefix;
    private TextView prefixView;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        try {
            caller = (AddItemDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Must implement AddItemDialogFragment listener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // use builder class to construct dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_inventory_item, null);

        // set click listeners on buttons
        view.findViewById(R.id.new_item_ok).setOnClickListener(this);
        view.findViewById(R.id.new_item_cancel).setOnClickListener(this);

        //init inputs
        nameInput = "";
        descInput = "";
        categoryInput = -1;
        valueInput = 0;
        valueInString = "";
        barcodeInput = "";

        // listen to name input
        final EditText nameText = (EditText) view.findViewById(R.id.new_item_name);
        nameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // required stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nameInput = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // required stub
            }
        });

        //listen to description input
        final EditText descText = (EditText) view.findViewById(R.id.new_item_desc);
        descText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // required stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                descInput = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // required stub
            }
        });

        // listen to category input
        categorySpinner = (Spinner) view.findViewById(R.id.new_item_category);

        Cursor cursor = getContext().getContentResolver().query(
                InventoryContract.CategoryEntry.CONTENT_URI,
                null,
                null,
                null,
                InventoryContract.CategoryEntry.COLUMN_CATEGORY
        );

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                getContext(),
                android.R.layout.simple_spinner_item,
                cursor,
                CATEGORY_COLUMNS,
                TO_VIEWS,
                0
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getBarcodePrefix();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // listen to value input
        final EditText valueText = (EditText) view.findViewById(R.id.new_item_value);
        valueText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // required stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                valueInString = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // required stub
            }
        });

        // listen to barcode input
        final EditText barcodeText = (EditText) view.findViewById(R.id.new_item_barcode);
        barcodeText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // required stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                barcodeInput = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // required stub
            }
        });

        prefixView = (TextView) view.findViewById(R.id.new_item_prefix);
        getBarcodePrefix();

        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.new_item_ok:
                if(addInventoryItem()){
                    caller.onButtonOK();
                    this.dismiss();
                } else {
                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.new_item_cancel:
                this.dismiss();
                break;
            default:
                // oops
                break;
        }
    }

    // function to add item to list from dialog
    private boolean addInventoryItem() {
        boolean added = false;

        // validate inputs
        if(dialogValidation()) {

            // check if item already exists
            if (!checkIfItemExists()) {

                // attempt to add item
                if (addInventoryItemToDB() != -1) {
                    added = true;
                } else {
                    error = getContext().getResources().getString(R.string.error_adding_item);
                }
            } else {
                error = getContext().getResources().getString(R.string.validation_barcode_exists);
            }
        } else {
            error = getContext().getResources().getString(R.string.validation_error);
        }

        return added;
    }

    private boolean dialogValidation(){
        boolean check = true;

        if(nameInput.isEmpty()){
            check = false;
            Toast.makeText(getContext(), getContext().getResources()
                    .getString(R.string.validation_name_empty), Toast.LENGTH_SHORT).show();
        }

        if(!valueInString.isEmpty()) {

            try {
                valueInput = Integer.parseInt(valueInString);

                if (valueInput < 1) {
                    check = false;
                    Toast.makeText(getContext(), getContext().getResources()
                            .getString(R.string.validation_value_negative), Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                check = false;
                Toast.makeText(getContext(), getContext().getResources()
                        .getString(R.string.validation_value_negative), Toast.LENGTH_SHORT).show();
                valueInput = 1;
                valueInString = Integer.toString(valueInput);
            }
        }

        if(barcodeInput.isEmpty()) {
            check = false;
            Toast.makeText(getActivity(), getContext().getResources()
                    .getString(R.string.validation_barcode_empty), Toast.LENGTH_SHORT).show();
        } else {

            if (barcodeInput.length() != 4) {
                check = false;
                Toast.makeText(getActivity(), getContext().getResources()
                        .getString(R.string.validation_barcode_long), Toast.LENGTH_SHORT).show();
            }
        }

        return check;
    }

    // checks if item already exists by looking at the barcode string
    private boolean checkIfItemExists() {

        boolean exists;

        // get barcode prefix
        getBarcodePrefix();
        String barcodeFull = "HKRF-" + barcodePrefix + "-" + barcodeInput;

        // Check if barcode id already exists in the db
        Cursor itemCursor = getContext().getContentResolver().query(
                InventoryContract.ItemEntry.CONTENT_URI,
                new String[]{InventoryContract.ItemEntry.TABLE_NAME + "." +
                        InventoryContract.ItemEntry._ID},
                InventoryContract.ItemEntry.COLUMN_BARCODE_ID + " = ?",
                new String[]{barcodeFull},
                null
        );

        // if barcode exists, return true
        exists = itemCursor.moveToFirst();

        itemCursor.close();
        return exists;
    }

    private long addInventoryItemToDB() {
        long itemId;
        String barcodeFull = "HKRF-" + barcodePrefix + "-" + barcodeInput;

        ContentValues itemValues = new ContentValues();

        // get category id
        categoryInput = categorySpinner.getSelectedItemId();

        // make content values of inventory item data
        itemValues.put(InventoryContract.ItemEntry.COLUMN_NAME, nameInput);
        itemValues.put(InventoryContract.ItemEntry.COLUMN_DESCRIPTION, descInput);
        itemValues.put(InventoryContract.ItemEntry.COLUMN_CATEGORY_KEY, categoryInput);
        itemValues.put(InventoryContract.ItemEntry.COLUMN_BARCODE_ID, barcodeFull);
        itemValues.put(InventoryContract.ItemEntry.COLUMN_VALUE, valueInString);

        // insert item into database
        Uri insertedUri = getContext().getContentResolver().insert(
                InventoryContract.ItemEntry.CONTENT_URI,
                itemValues
        );

        itemId = ContentUris.parseId(insertedUri);

        return itemId;
    }

    private void getBarcodePrefix() {

        String barcodeFull;

        Uri prefixUri = InventoryContract.CategoryEntry.buildCategoryWithIdUri(
                categorySpinner.getSelectedItemId());
        Cursor prefixCursor = getContext().getContentResolver().query(
                prefixUri,
                null,
                null,
                null,
                null
        );

        if (null != prefixCursor && prefixCursor.moveToFirst()) {
            barcodePrefix = prefixCursor.getString(2);
        } else {
            barcodePrefix = "XX";
        }
        prefixCursor.close();

        barcodeFull = "HKRF-" + barcodePrefix + "-" + barcodeInput;
        prefixView.setText(barcodeFull);
    }

    public interface AddItemDialogListener {
        void onButtonOK();
    }
}
