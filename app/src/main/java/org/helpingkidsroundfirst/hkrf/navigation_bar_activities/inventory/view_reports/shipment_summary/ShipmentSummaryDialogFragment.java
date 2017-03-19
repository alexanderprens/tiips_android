package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_reports.shipment_summary;

import android.app.AlertDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import org.helpingkidsroundfirst.hkrf.R;
import org.helpingkidsroundfirst.hkrf.data.InventoryContract;

/**
 * Created by alexa on 3/19/2017.
 */

public class ShipmentSummaryDialogFragment extends DialogFragment implements
        View.OnClickListener {

    private static final String[] DATE_COLUMN = {
            InventoryContract.PastInventoryEntry.COLUMN_DATE_SHIPPED
    };
    private static int[] DATE_TO_VIEW = {
            android.R.id.text1
    };
    private String dateInput;
    private OnShipmentSummaryListener caller;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            caller = (OnShipmentSummaryListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Must implement OnShipmentSummaryListener");
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // use builder to create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_shipment_summary, null);

        // set click listeners on buttons
        rootView.findViewById(R.id.dialog_shipment_sum_ok).setOnClickListener(this);
        rootView.findViewById(R.id.dialog_shipment_sum_cancel).setOnClickListener(this);

        // init inputs
        dateInput = "";

        // date spinner
        final Spinner dateSpinner = (Spinner) rootView.findViewById(R.id.dialog_shipment_sum_spinner);

        final Cursor cursor = getContext().getContentResolver().query(
                InventoryContract.PastInventoryEntry.buildPastInventoryUri(),
                new String[]{"DISTINCT " + InventoryContract.PastInventoryEntry.COLUMN_DATE_SHIPPED,
                        "1 _id"},
                null,
                null,
                InventoryContract.PastInventoryEntry.COLUMN_DATE_SHIPPED
        );

        final SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                getContext(),
                android.R.layout.simple_spinner_item,
                cursor,
                DATE_COLUMN,
                DATE_TO_VIEW,
                0
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateSpinner.setAdapter(adapter);
        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Cursor dateCursor = (Cursor) dateSpinner.getSelectedItem();
                dateInput = dateCursor.getString(dateCursor.getColumnIndex(
                        InventoryContract.PastInventoryEntry.COLUMN_DATE_SHIPPED));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        builder.setView(rootView);
        return builder.create();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_shipment_sum_ok:
                caller.onShipmentButton(dateInput);
                this.dismiss();
                break;

            case R.id.dialog_shipment_sum_cancel:
                this.dismiss();
                break;
        }
    }

    public interface OnShipmentSummaryListener {
        void onShipmentButton(String date);
    }
}
