package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory.inventory_items;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.helpingkidsroundfirst.hkrf.R;
import org.helpingkidsroundfirst.hkrf.data.InventoryContract;


/**
 * Created by Alex on 2/7/2017.
 */

public class UpdateItemDialogFragment extends DialogFragment implements
        View.OnClickListener {

    public static final String NAME_KEY = "name_string";
    public static final String DESCRIPTION_KEY = "description_string";
    public static final String VALUE_KEY = "value_int";
    public static final String ID_KEY = "item_id_long";
    private String nameInput;
    private String descriptionInput;
    private String valueInput;
    private int valueInt;
    private long itemId;
    private UpdateItemListener mListener;
    private String error;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            mListener = (UpdateItemListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Must implement UpdateItemListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // use builder at create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_update_inventory_item, null);

        // set click listeners on buttons
        view.findViewById(R.id.update_item_cancel).setOnClickListener(this);
        view.findViewById(R.id.update_item_ok).setOnClickListener(this);

        // get input arguments
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            nameInput = bundle.getString(NAME_KEY);
            descriptionInput = bundle.getString(DESCRIPTION_KEY);
            valueInt = bundle.getInt(VALUE_KEY);
            itemId = bundle.getLong(ID_KEY);
            valueInput = Integer.toString(valueInt);
        }

        // listen to name input
        final EditText nameText = (EditText) view.findViewById(R.id.update_item_name);
        nameText.setText(nameInput);
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

        // listen to description input
        final EditText descriptionText = (EditText) view.findViewById(R.id.update_item_description);
        descriptionText.setText(descriptionInput);
        descriptionText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // required stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                descriptionInput = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // required stub
            }
        });

        // listen to value input
        final EditText valueText = (EditText) view.findViewById(R.id.update_item_value);
        valueText.setText(valueInput);
        valueText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // required stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                valueInput = s.toString();
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
            case R.id.update_item_ok:
                if (updateItem()) {
                    mListener.onUpdateItemOKButton();
                    this.dismiss();
                } else {
                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.update_item_cancel:
                this.dismiss();
                break;
        }
    }

    private boolean updateItem() {
        boolean added = false;

        // validate inputs
        if (dialogValidation()) {

            // attempt to update item
            if (attemptUpdateItem() != 0) {
                added = true;
            } else {
                error = getContext().getResources().getString(R.string.error_updating_item);
            }

        } else {
            error = getContext().getResources().getString(R.string.validation_error);
        }

        return added;
    }

    private boolean dialogValidation() {
        boolean check = true;

        // check if name is empty
        if (nameInput.isEmpty()) {
            check = false;
            Toast.makeText(getContext(), getContext().getResources()
                    .getString(R.string.validation_name_empty), Toast.LENGTH_SHORT).show();
        }

        // check if value is above zero
        if (valueInput.isEmpty()) {
            valueInt = Integer.parseInt(valueInput);

            if (valueInt < 1) {
                check = false;
                Toast.makeText(getContext(), getContext().getResources()
                        .getString(R.string.validation_value_negative), Toast.LENGTH_SHORT).show();
            }
        }

        return check;
    }

    private int attemptUpdateItem() {
        int rowsUpdate;
        Uri itemUri = InventoryContract.ItemEntry.buildInventoryItemUri();
        String selection = InventoryContract.ItemEntry.TABLE_NAME + "." +
                InventoryContract.ItemEntry._ID + " = ? ";
        String[] selectionArgs = {Long.toString(itemId)};

        ContentValues updatedItems = new ContentValues();
        updatedItems.put(InventoryContract.ItemEntry.COLUMN_NAME, nameInput);
        updatedItems.put(InventoryContract.ItemEntry.COLUMN_DESCRIPTION, descriptionInput);
        updatedItems.put(InventoryContract.ItemEntry.COLUMN_VALUE, Long.parseLong(valueInput));

        rowsUpdate = getContext().getContentResolver().update(
                itemUri,
                updatedItems,
                selection,
                selectionArgs
        );

        return rowsUpdate;
    }

    public interface UpdateItemListener {
        void onUpdateItemOKButton();
    }
}
