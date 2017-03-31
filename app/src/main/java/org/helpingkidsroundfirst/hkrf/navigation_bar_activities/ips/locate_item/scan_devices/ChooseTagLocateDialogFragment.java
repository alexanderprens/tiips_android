package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.ips.locate_item.scan_devices;

import android.app.AlertDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.net.Uri;
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
 * Created by alexa on 3/31/2017.
 */

public class ChooseTagLocateDialogFragment extends DialogFragment implements
        View.OnClickListener {

    private static final String[] NAME_COLUMN = {
            InventoryContract.TagEntry.COLUMN_NAME
    };
    private static int[] NAME_TO_VIEW = {
            android.R.id.text1
    };
    private long nameId;
    private OnChooseTagLocateListener caller;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            caller = (OnChooseTagLocateListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Must implement OnChooseTagLocateListener");
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // use builder to create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_choose_tag_locate, null);

        // set click listeners on buttons
        rootView.findViewById(R.id.choose_tag_locate_ok).setOnClickListener(this);
        rootView.findViewById(R.id.choose_tag_locate_cancel).setOnClickListener(this);

        // init inputs
        nameId = -1;

        final Spinner nameSpinner = (Spinner) rootView.findViewById(R.id.choose_tag_locate_spinner);

        final Cursor cursor = getContext().getContentResolver().query(
                InventoryContract.TagEntry.buildTagUri(),
                new String[]{InventoryContract.TagEntry.COLUMN_NAME,
                        InventoryContract.TagEntry.TABLE_NAME + "." +
                                InventoryContract.TagEntry._ID + " AS _id"
                },
                null,
                null,
                InventoryContract.TagEntry.COLUMN_NAME
        );

        final SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                getContext(),
                android.R.layout.simple_spinner_item,
                cursor,
                NAME_COLUMN,
                NAME_TO_VIEW,
                0
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nameSpinner.setAdapter(adapter);
        nameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Cursor nameCursor = (Cursor) nameSpinner.getSelectedItem();
                nameId = nameCursor.getLong(nameCursor.getColumnIndex("_id"));
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
            case R.id.choose_tag_locate_ok:
                Uri uri = InventoryContract.TagEntry.buildTagWithIdUri(nameId);
                caller.onTagChosen(uri);
                this.dismiss();
                break;

            case R.id.choose_tag_locate_cancel:
                this.dismiss();
                break;
        }
    }

    public interface OnChooseTagLocateListener {
        void onTagChosen(Uri uri);
    }
}
