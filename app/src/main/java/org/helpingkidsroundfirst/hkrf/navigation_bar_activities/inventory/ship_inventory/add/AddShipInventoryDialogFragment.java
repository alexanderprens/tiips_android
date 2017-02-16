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

    public interface AddShipDialogListener {
        void onButtonOK();
    }
}
