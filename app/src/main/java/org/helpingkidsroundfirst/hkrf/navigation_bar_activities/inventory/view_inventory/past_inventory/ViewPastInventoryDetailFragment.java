package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory.past_inventory;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.helpingkidsroundfirst.hkrf.R;
import org.helpingkidsroundfirst.hkrf.data.InventoryContract;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewPastInventoryDetailFragment extends Fragment implements
    LoaderManager.LoaderCallbacks<Cursor> {

    public static final int COL_PAST_ID = 0;
    public static final int COL_PAST_QTY = 1;
    public static final int COL_PAST_DATE_SHIPPED = 2;
    public static final int COL_PAST_DONOR = 3;
    public static final int COL_ITEM_NAME = 4;
    public static final int COL_ITEM_DESCRIPTION = 5;
    public static final int COL_ITEM_CATEGORY_KEY = 6;
    public static final int COL_ITEM_VALUE = 7;
    public static final int COL_CATEGORY_ID = 8;
    public static final int COL_CATEGORY_NAME = 9;
    public static final int COL_CATEGORY_BARCODE = 10;
    public static final int COL_BARCODE_ID = 11;
    public static final String DETAILED_PAST_KEY = "PAST_URI";
    public static final int PAST_DETAIL_LOADER = 6;
    private static final String[] PAST_DETAIL_COLUMNS = {
            InventoryContract.PastInventoryEntry.TABLE_NAME + "." + InventoryContract.PastInventoryEntry._ID,
            InventoryContract.PastInventoryEntry.COLUMN_QTY,
            InventoryContract.PastInventoryEntry.COLUMN_DATE_SHIPPED,
            InventoryContract.PastInventoryEntry.COLUMN_DONOR,
            InventoryContract.PastInventoryEntry.COLUMN_NAME,
            InventoryContract.PastInventoryEntry.COLUMN_DESCRIPTION,
            InventoryContract.PastInventoryEntry.COLUMN_CATEGORY_KEY,
            InventoryContract.PastInventoryEntry.COLUMN_VALUE,
            InventoryContract.CategoryEntry.TABLE_NAME + "." +
                    InventoryContract.CategoryEntry._ID,
            InventoryContract.CategoryEntry.COLUMN_CATEGORY,
            InventoryContract.CategoryEntry.COLUMN_BARCODE_PREFIX,
            InventoryContract.PastInventoryEntry.COLUMN_BARCODE_ID
    };
    private TextView nameView;
    private TextView descriptionView;
    private TextView categoryView;
    private TextView valueView;
    private TextView barcodeView;
    private TextView qtyView;
    private TextView dateView;
    private TextView donorView;
    private Uri mUri;
    private long pastInventoryId;

    public ViewPastInventoryDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_view_past_inventory_detail, container,
                false);

        // get past inventory uri from bundle
        Bundle bundle = this.getArguments();
        if(bundle != null) {
            mUri = bundle.getParcelable(DETAILED_PAST_KEY);
        }

        // assign text views
        nameView = (TextView) rootView.findViewById(R.id.past_inventory_detail_text_name);
        descriptionView = (TextView) rootView.findViewById(
                R.id.past_inventory_detail_text_description);
        categoryView = (TextView) rootView.findViewById(R.id.past_inventory_detail_text_category);
        valueView = (TextView) rootView.findViewById(R.id.past_inventory_detail_text_value);
        barcodeView = (TextView) rootView.findViewById(R.id.past_inventory_detail_text_barcode);
        qtyView = (TextView) rootView.findViewById(R.id.past_inventory_detail_text_qty);
        dateView = (TextView) rootView.findViewById(R.id.past_inventory_detail_text_date);
        donorView = (TextView) rootView.findViewById(R.id.past_inventory_detail_text_donor);

        // implement delete button
        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        if (handlePastInventoryDelete()) {
                            FragmentManager manager = getActivity().getSupportFragmentManager();
                            manager.popBackStack();
                            manager.popBackStack();
                            manager.popBackStack();
                        }
                        break;
                }
            }
        };

        Button delete = (Button) rootView.findViewById(R.id.view_past_inventory_delete);
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
        getLoaderManager().initLoader(PAST_DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(null != mUri) {

            // get details from database
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    PAST_DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // if there's data load it
        if(data != null && data.moveToFirst()) {
            //read data from cursor
            String name = data.getString(COL_ITEM_NAME);
            String description = data.getString(COL_ITEM_DESCRIPTION);
            String category = data.getString(COL_CATEGORY_NAME);
            int value = data.getInt(COL_ITEM_VALUE);
            String valueString = "" + value;
            String barcode = data.getString(COL_BARCODE_ID);
            int quantity = data.getInt(COL_PAST_QTY);
            String quantityString = "" + quantity;
            String date = data.getString(COL_PAST_DATE_SHIPPED);
            String donor = data.getString(COL_PAST_DONOR);
            pastInventoryId = data.getLong(COL_PAST_ID);

            //place data into text views
            nameView.setText(name);
            descriptionView.setText(description);
            categoryView.setText(category);
            valueView.setText(valueString);
            barcodeView.setText(barcode);
            qtyView.setText(quantityString);
            dateView.setText(date);
            donorView.setText(donor);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private boolean handlePastInventoryDelete() {
        boolean deleted = false;
        int rowDeleted = -1;
        Uri pastInventoryUri = InventoryContract.PastInventoryEntry.buildPastInventoryUri();
        String selection = InventoryContract.PastInventoryEntry.TABLE_NAME + "." +
                InventoryContract.PastInventoryEntry._ID + " = ? AND " +
                InventoryContract.PastInventoryEntry.COLUMN_DONOR + " = ? ";
        String[] selectionArgs = {Long.toString(pastInventoryId),
                "SDSU"
        };
        String message = getContext().getResources().getString(R.string.past_inventory_delete_fail);

        if (pastInventoryId != -1) {
            rowDeleted = getContext().getContentResolver().delete(
                    pastInventoryUri,
                    selection,
                    selectionArgs
            );
        }

        if (rowDeleted != 0) {
            message = getContext().getResources().getString(R.string.past_inventory_delete_success);
            deleted = true;
        }

        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        return deleted;
    }
}
