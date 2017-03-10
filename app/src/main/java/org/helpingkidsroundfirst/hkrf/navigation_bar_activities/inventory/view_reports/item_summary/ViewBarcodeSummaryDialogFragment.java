package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_reports.item_summary;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.zxing.client.android.CaptureActivity;

import org.helpingkidsroundfirst.hkrf.R;
import org.helpingkidsroundfirst.hkrf.data.InventoryContract;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by alexa on 3/10/2017.
 */

public class ViewBarcodeSummaryDialogFragment extends DialogFragment implements
        View.OnClickListener {

    private static final String[] BARCODE_AUTOCOMPLETE = {
            InventoryContract.ItemEntry.COLUMN_BARCODE_ID
    };
    private static int[] BARCODE_TO_VIEW = {
            android.R.id.text1
    };

    private long itemId;
    private Spinner barcodeView;
    private OnViewBarcodeSummaryDialogResult caller;
    private Cursor cursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            caller = (OnViewBarcodeSummaryDialogResult) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Must implement AddItemDialogFragment listener");
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // use builder to create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_barcode_summary, null);

        // set click listeners on button
        view.findViewById(R.id.dialog_barcode_summary_ok).setOnClickListener(this);
        view.findViewById(R.id.dialog_barcode_summary_cancel).setOnClickListener(this);

        // init inputs
        itemId = -1;

        // scan barcode button
        Button buttonScan = (Button) view.findViewById(R.id.dialog_barcode_summary_scan);
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

        // barcode spinner
        barcodeView = (Spinner) view.findViewById(R.id.dialog_barcode_summary_spinner);

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

        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_barcode_summary_ok:
                if (itemId != -1) {
                    caller.onBarcodeChosen(InventoryContract.ItemEntry
                            .buildInventoryItemWithIdUri(itemId));
                }

                this.dismiss();
                break;

            case R.id.dialog_barcode_summary_cancel:
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
                        barcodeView.setSelection(position);
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

    public interface OnViewBarcodeSummaryDialogResult {
        void onBarcodeChosen(Uri uri);
    }
}
