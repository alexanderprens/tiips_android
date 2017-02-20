package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory.current_inventory;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
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

import java.util.Calendar;

/**
 * Created by alexa on 2/12/2017.
 */

public class UpdateCurrentDialogFragment extends DialogFragment implements
        View.OnClickListener {

    public static final String QTY_KEY = "current_qty";
    public static final String DONOR_KEY = "current_donor";
    public static final String DATE_KEY = "current_data_received";
    public static final String WAREHOUSE_KEY = "current_warehouse";
    public static final String ID_KEY = "current_id";

    private int quantityInput;
    private String donorInput;
    private String dateInput;
    private String warehouseInput;
    private long currentId;
    private String error;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // use builder to create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_update_current, null);

        // set click listeners on buttons
        view.findViewById(R.id.update_current_button_ok).setOnClickListener(this);
        view.findViewById(R.id.update_current_button_cancel).setOnClickListener(this);

        // get input arguments
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            quantityInput = bundle.getInt(QTY_KEY);
            donorInput = bundle.getString(DONOR_KEY);
            dateInput = bundle.getString(DATE_KEY);
            warehouseInput = bundle.getString(WAREHOUSE_KEY);
            currentId = bundle.getLong(ID_KEY);
        }

        // listen to qty input
        final EditText quantityText = (EditText) view.findViewById(R.id.update_current_quantity);
        quantityText.setText(Integer.toString(quantityInput));
        quantityText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // required stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String tempStr = s.toString();
                if (tempStr.isEmpty()) {
                    quantityInput = 0;
                } else {
                    try {
                        quantityInput = Integer.parseInt(tempStr);
                    } catch (NumberFormatException e) {
                        quantityInput = 0;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // required stub
            }
        });

        // listen to donor input
        final EditText donorText = (EditText) view.findViewById(R.id.update_current_donor);
        donorText.setText(donorInput);
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
        final EditText warehouseText = (EditText) view.findViewById(R.id.update_current_warehouse);
        warehouseText.setText(warehouseInput);
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
        final TextView dateChosen = (TextView) view.findViewById(R.id.update_current_date_string);
        dateChosen.setText(dateInput);

        Button pickDateButton = (Button) view.findViewById(R.id.update_current_button_date);
        pickDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dateInput = Utility.getDatePickerString(year, month, dayOfMonth);
                        dateChosen.setText(dateInput);
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
            case R.id.update_current_button_ok:
                if (updateCurrentItem()) {
                    this.dismiss();
                } else {
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.update_current_button_cancel:
                this.dismiss();
                break;
        }
    }

    private boolean updateCurrentItem() {
        boolean updated = false;

        // validate inputs
        if (dialogValidation()) {

            // attempt update
            if (updateCurrentInDb() != 0) {
                updated = true;
                Toast.makeText(getContext(), getContext()
                        .getString(R.string.update_current_successful), Toast.LENGTH_SHORT).show();
            } else {
                error = getContext().getResources().getString(R.string.current_inventory_update_fail);
            }
        } else {
            error = getContext().getResources().getString(R.string.validation_error);
        }

        return updated;
    }

    private boolean dialogValidation() {
        boolean check = true;

        if (quantityInput < 1) {
            check = false;
            Toast.makeText(getContext(), getContext().getResources()
                    .getString(R.string.validation_qty_zero), Toast.LENGTH_SHORT).show();
        }

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

        if (dateInput.isEmpty()) {
            check = false;
            Toast.makeText(getContext(), getContext().getResources()
                    .getString(R.string.validation_date_invalid), Toast.LENGTH_SHORT).show();
        }

        return check;
    }

    private int updateCurrentInDb() {
        int updatedRows;

        // setup update query
        Uri uri = InventoryContract.CurrentInventoryEntry.buildCurrentInventoryUri();
        String selection = InventoryContract.CurrentInventoryEntry.TABLE_NAME + "."
                + InventoryContract.CurrentInventoryEntry._ID + " = ?";
        String[] selectionArgs = {Long.toString(currentId)};

        // get new values
        ContentValues newValues = new ContentValues();
        newValues.put(InventoryContract.CurrentInventoryEntry.COLUMN_DATE_RECEIVED, dateInput);
        newValues.put(InventoryContract.CurrentInventoryEntry.COLUMN_WAREHOUSE, warehouseInput);
        newValues.put(InventoryContract.CurrentInventoryEntry.COLUMN_DONOR, donorInput);
        newValues.put(InventoryContract.CurrentInventoryEntry.COLUMN_QTY, quantityInput);

        updatedRows = getContext().getContentResolver().update(
                uri,
                newValues,
                selection,
                selectionArgs
        );

        return updatedRows;
    }
}
