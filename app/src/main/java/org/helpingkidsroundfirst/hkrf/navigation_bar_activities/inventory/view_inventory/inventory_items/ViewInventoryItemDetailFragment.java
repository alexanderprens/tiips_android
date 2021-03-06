package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory.inventory_items;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
public class ViewInventoryItemDetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        UpdateItemDialogFragment.UpdateItemListener {

    // Inventory item column indices
    public static final int COL_ITEM_ID = 0;
    public static final int COL_ITEM_BARCODE = 1;
    public static final int COL_ITEM_NAME = 2;
    public static final int COL_ITEM_DESC = 3;
    public static final int COL_ITEM_CAT_KEY = 4;
    public static final int COL_ITEM_VALUE = 5;
    public static final int COL_CATEGORY_ID = 6;
    public static final int COL_CATEGORY_NAME = 7;
    public static final int COL_CATEGORY_BARCODE = 8;
    public static final String DETAILED_ITEM_KEY = "ITEM_URI";
    public static final int ITEM_DETAIL_LOADER = 4;
    private static final String[] ITEM_DETAIL_COLUMNS = {
            InventoryContract.ItemEntry.TABLE_NAME + "." + InventoryContract.ItemEntry._ID,
            InventoryContract.ItemEntry.COLUMN_BARCODE_ID,
            InventoryContract.ItemEntry.COLUMN_NAME,
            InventoryContract.ItemEntry.COLUMN_DESCRIPTION,
            InventoryContract.ItemEntry.COLUMN_CATEGORY_KEY,
            InventoryContract.ItemEntry.COLUMN_VALUE,
            InventoryContract.CategoryEntry.TABLE_NAME + "." + InventoryContract.CategoryEntry._ID,
            InventoryContract.CategoryEntry.COLUMN_CATEGORY,
            InventoryContract.CategoryEntry.COLUMN_BARCODE_PREFIX
    };
    private TextView nameView;
    private TextView descriptionView;
    private TextView categoryView;
    private TextView valueView;
    private TextView barcodeView;
    private Uri inventoryItemDetailUri;
    private long itemId = -1;
    private String name;
    private String description;
    private int value;
    private long categoryKey;

    public ViewInventoryItemDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_view_inventory_item_detail, container,
                false);

        // get item uri from bundle
        final Bundle bundle = this.getArguments();
        if (bundle != null) {
            inventoryItemDetailUri = bundle.getParcelable(DETAILED_ITEM_KEY);
        }

        // assign text views
        nameView = (TextView) rootView.findViewById(R.id.inventory_item_detail_text_name);
        descriptionView = (TextView) rootView.findViewById(
                R.id.inventory_item_detail_text_description);
        categoryView = (TextView) rootView.findViewById(R.id.inventory_item_detail_text_category);
        valueView = (TextView) rootView.findViewById(R.id.inventory_item_detail_text_value);
        barcodeView = (TextView) rootView.findViewById(R.id.inventory_item_detail_text_barcode);

        // implement fab
        FloatingActionButton fab = (FloatingActionButton) rootView
                .findViewById(R.id.view_item_detail_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                UpdateItemDialogFragment dialog = new UpdateItemDialogFragment();
                Bundle dialogInputs = new Bundle();
                dialogInputs.putString(UpdateItemDialogFragment.NAME_KEY, name);
                dialogInputs.putString(UpdateItemDialogFragment.DESCRIPTION_KEY, description);
                dialogInputs.putInt(UpdateItemDialogFragment.VALUE_KEY, value);
                dialogInputs.putLong(UpdateItemDialogFragment.ID_KEY, itemId);
                dialogInputs.putLong(UpdateItemDialogFragment.CATEGORY_KEY, categoryKey);
                dialog.setArguments(dialogInputs);
                dialog.setTargetFragment(ViewInventoryItemDetailFragment.this, 300);
                dialog.show(fragmentManager, "open update item dialog");
            }
        });

        // implement delete button
        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        if (handleItemDeletion()) {
                            FragmentManager manager = getActivity().getSupportFragmentManager();
                            manager.popBackStack();
                        }
                        break;
                }
            }
        };

        Button delete = (Button) rootView.findViewById(R.id.view_inventory_item_delete);
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
        getLoaderManager().initLoader(ITEM_DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if( null != inventoryItemDetailUri) {

            // get the detailed inventory item from database
            return  new CursorLoader(
                    getActivity(),
                    inventoryItemDetailUri,
                    ITEM_DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data != null && data.moveToFirst()) {
            //read data from cursor
            name = data.getString(COL_ITEM_NAME);
            description = data.getString(COL_ITEM_DESC);
            String category = data.getString(COL_CATEGORY_NAME);
            value = data.getInt(COL_ITEM_VALUE);
            String valueString = "" + value;
            String barcode = data.getString(COL_ITEM_BARCODE);
            itemId = data.getLong(COL_ITEM_ID);
            categoryKey = data.getLong(COL_ITEM_CAT_KEY);

            //place data into text views
            nameView.setText(name);
            descriptionView.setText(description);
            categoryView.setText(category);
            valueView.setText(valueString);
            barcodeView.setText(barcode);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private boolean handleItemDeletion() {
        boolean deleted = false;
        boolean used = false;
        int rowDeleted = -1;
        Uri itemURI = InventoryContract.ItemEntry.buildInventoryItemUri();
        String[] selectionArgs = {Long.toString(itemId)};
        String message = getContext().getResources().getString(R.string.barcode_item_delete_failed);

        // check if item is being used in current inventory
        String selection = InventoryContract.CurrentInventoryEntry.COLUMN_ITEM_KEY + " = ? ";
        Cursor cursor = getContext().getContentResolver().query(
                InventoryContract.CurrentInventoryEntry.buildCurrentInventoryUri(),
                null,
                selection,
                selectionArgs,
                null,
                null
        );

        // check if cursor returns anything
        if (cursor != null && cursor.moveToFirst()) {
            used = true;
            cursor.close();
        }

        // check if item is being used in receive inventory
        selection = InventoryContract.ReceiveInventoryEntry.COLUMN_ITEM_KEY + " = ? ";
        cursor = getContext().getContentResolver().query(
                InventoryContract.ReceiveInventoryEntry.buildReceiveInventoryUri(),
                null,
                selection,
                selectionArgs,
                null,
                null
        );

        // check if cursor returns anything
        if (cursor != null && cursor.moveToFirst()) {
            used = true;
            cursor.close();
        }

        selection = InventoryContract.ItemEntry.TABLE_NAME + "." +
                InventoryContract.ItemEntry._ID + " = ? ";
        if (!used) {

            // attempt delete
            if (itemId != -1) {
                rowDeleted = getContext().getContentResolver().delete(
                        itemURI,
                        selection,
                        selectionArgs
                );
            }

            // check if deletion occurred
            if (rowDeleted != 0) {
                message = getContext().getResources().getString(R.string.barcode_item_delete_success);
                deleted = true;
            }
        }

        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        return deleted;
    }

    @Override
    public void onUpdateItemOKButton() {
        getLoaderManager().restartLoader(ITEM_DETAIL_LOADER, null, this);
    }
}
