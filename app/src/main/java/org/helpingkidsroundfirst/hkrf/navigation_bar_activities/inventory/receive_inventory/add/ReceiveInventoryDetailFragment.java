package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.receive_inventory.add;


import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.helpingkidsroundfirst.hkrf.R;
import org.helpingkidsroundfirst.hkrf.data.InventoryContract;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReceiveInventoryDetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    // Current inventory column indices
    public static final int COL_RECEIVE_ID = 0;
    public static final int COL_RECEIVE_QTY = 1;
    public static final int COL_RECEIVE_DATE_RECEIVED = 2;
    public static final int COL_RECEIVE_DONOR = 3;
    public static final int COL_RECEIVE_WAREHOUSE = 4;
    public static final int COL_ITEM_BARCODE_KEY = 5;
    public static final int COL_ITEM_NAME = 6;
    public static final int COL_ITEM_DESCRIPTION = 7;
    public static final int COL_ITEM_CATEGORY_KEY = 8;
    public static final int COL_ITEM_VALUE = 9;
    public static final int COL_CATEGORY_ID = 10;
    public static final int COL_CATEGORY_NAME = 11;
    public static final int COL_CATEGORY_BARCODE_PREFIX = 12;
    public static final String DETAILED_RECEIVE_KEY = "RECEIVE_URI";
    public static final int RECEIVE_DETAIL_LOADER = 10;
    private static final String[] RECEIVE_DETAIL_COLUMNS = {
            InventoryContract.ReceiveInventoryEntry.TABLE_NAME + "." +
                    InventoryContract.ReceiveInventoryEntry._ID + " AS _id",
            InventoryContract.ReceiveInventoryEntry.COLUMN_QTY,
            InventoryContract.ReceiveInventoryEntry.COLUMN_DONOR,
            InventoryContract.ReceiveInventoryEntry.COLUMN_DATE_RECEIVED,
            InventoryContract.ReceiveInventoryEntry.COLUMN_WAREHOUSE,
            InventoryContract.ItemEntry.COLUMN_BARCODE_ID,
            InventoryContract.ItemEntry.COLUMN_NAME,
            InventoryContract.ItemEntry.COLUMN_DESCRIPTION,
            InventoryContract.ItemEntry.COLUMN_CATEGORY_KEY,
            InventoryContract.ReceiveInventoryEntry.COLUMN_VALUE,
            InventoryContract.CategoryEntry.TABLE_NAME + "." + InventoryContract.CategoryEntry._ID,
            InventoryContract.CategoryEntry.COLUMN_CATEGORY,
            InventoryContract.CategoryEntry.COLUMN_BARCODE_PREFIX
    };
    private TextView nameView;
    private TextView descriptionView;
    private TextView categoryView;
    private TextView valueView;
    private TextView barcodeView;
    private TextView qtyView;
    private Uri mUri;
    private long receiveInventoryId;
    private EditText qtyEditText;
    private int newQty;
    private int currentQty;

    public ReceiveInventoryDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_receive_inventory_detail,
                container, false);

        // get receive inventory id from bundle
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mUri = bundle.getParcelable(DETAILED_RECEIVE_KEY);
        }

        // assign text views
        nameView = (TextView) rootView.findViewById(R.id.receive_inventory_detail_text_name);
        descriptionView = (TextView) rootView
                .findViewById(R.id.receive_inventory_detail_text_description);
        categoryView = (TextView) rootView.findViewById(R.id.receive_inventory_detail_text_category);
        valueView = (TextView) rootView.findViewById(R.id.receive_inventory_detail_text_value);
        barcodeView = (TextView) rootView.findViewById(R.id.receive_inventory_detail_text_barcode);
        qtyView = (TextView) rootView.findViewById(R.id.receive_inventory_detail_text_qty);

        // implement quantity edit text
        newQty = -1;
        qtyEditText = (EditText) rootView.findViewById(R.id.receive_detail_new_qty);
        qtyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // required stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String tempStr = s.toString();
                if (tempStr.isEmpty()) {
                    newQty = 0;
                } else {
                    newQty = Integer.parseInt(tempStr);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // required stub
            }
        });

        // implement qty plus button
        Button plusButton = (Button) rootView.findViewById(R.id.receive_detail_add);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add one to qty
                newQty++;
                qtyEditText.setText(Integer.toString(newQty));
            }
        });

        // implement qty minus button
        Button minusButton = (Button) rootView.findViewById(R.id.receive_detail_subtract);
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // subtract one from qty
                newQty--;
                if (newQty < 0) {
                    newQty = 0;
                }

                qtyEditText.setText(Integer.toString(newQty));
            }
        });

        // implement qty submit button
        Button submitButton = (Button) rootView.findViewById(R.id.receive_detail_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // update receive inventory item with new qty
                handleReceiveInventoryUpdate();
            }
        });

        // implement delete button
        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        if (handleReceiveInventoryDeletion()) {
                            FragmentManager manager = getActivity().getSupportFragmentManager();
                            manager.popBackStack();
                        }
                        break;
                }
            }
        };

        Button delete = (Button) rootView.findViewById(R.id.receive_inventory_detail_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // call dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage(R.string.are_you_sure_message)
                        .setPositiveButton(R.string.are_you_sure_yes, dialogClickListener)
                        .setNegativeButton(R.string.are_you_sure_no, dialogClickListener)
                        .show();
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(RECEIVE_DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mUri != null) {

            // get detailed item from database
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    RECEIVE_DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        // if there's data then load it
        if (data != null && data.moveToFirst()) {
            // read data from cursor
            String name = data.getString(COL_ITEM_NAME);
            String description = data.getString(COL_ITEM_DESCRIPTION);
            String category = data.getString(COL_CATEGORY_NAME);
            int value = data.getInt(COL_ITEM_VALUE);
            String valueString = "" + value;
            String barcode = data.getString(COL_ITEM_BARCODE_KEY);
            currentQty = data.getInt(COL_RECEIVE_QTY);
            String quantityString = "" + currentQty;
            receiveInventoryId = data.getLong(COL_RECEIVE_ID);

            // place data into text views
            nameView.setText(name);
            descriptionView.setText(description);
            categoryView.setText(category);
            valueView.setText(valueString);
            barcodeView.setText(barcode);
            qtyView.setText(quantityString);

            if (newQty == -1) {
                qtyEditText.setText(quantityString);
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private boolean handleReceiveInventoryDeletion() {
        boolean deleted = false;
        int rowDeleted = 0;
        Uri receiveInvenotryUri = InventoryContract.ReceiveInventoryEntry.buildReceiveInventoryUri();
        String selection = InventoryContract.ReceiveInventoryEntry.TABLE_NAME + "." +
                InventoryContract.ReceiveInventoryEntry._ID + " = ? ";
        String[] selectionArgs = {Long.toString(receiveInventoryId)};
        String message = getContext().getResources().getString(R.string.error_receive_not_deleted);

        if (receiveInventoryId != -1) {
            rowDeleted = getContext().getContentResolver().delete(
                    receiveInvenotryUri,
                    selection,
                    selectionArgs
            );
        }

        if (rowDeleted != 0) {
            message = getContext().getResources().getString(R.string.message_receive_deleted);
            deleted = true;
        }

        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        return deleted;
    }

    private void handleReceiveInventoryUpdate() {
        int rowUpdated = -1;

        // setup db update
        Uri receiveInventoryUri = InventoryContract.ReceiveInventoryEntry.buildReceiveInventoryUri();
        String selection = InventoryContract.ReceiveInventoryEntry.TABLE_NAME + "." +
                InventoryContract.ReceiveInventoryEntry._ID + " = ? ";
        String[] selectionArgs = {Long.toString(receiveInventoryId)};
        String message = getContext().getResources().getString(R.string.error_updating_receive);

        // get new value
        ContentValues newValues = new ContentValues();
        newValues.put(InventoryContract.ReceiveInventoryEntry.COLUMN_QTY, Integer.toString(newQty));

        if (receiveInventoryId != -1) {
            rowUpdated = getContext().getContentResolver().update(
                    receiveInventoryUri,
                    newValues,
                    selection,
                    selectionArgs
            );
        }

        if (rowUpdated != 0) {
            message = getContext().getResources().getString(R.string.update_receive_successful);
        }

        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
