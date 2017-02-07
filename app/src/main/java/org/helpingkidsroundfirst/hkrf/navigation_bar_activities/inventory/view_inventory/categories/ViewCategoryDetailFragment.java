package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory.categories;


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
public class ViewCategoryDetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final int COL_CATEGORY_ID = 0;
    public static final int COL_CATEGORY_CAT = 1;
    public static final int COL_CATEGORY_BARCODE = 2;
    public static final String DETAILED_CATEGORY_KEY = "CATEGORY_URI";
    public static final int CATEGORY_DETAIL_LOADER = 8;
    private static final String[] CATEGORY_DETAIL_COLUMNS = {
            InventoryContract.CategoryEntry.TABLE_NAME + "." + InventoryContract.CategoryEntry._ID,
            InventoryContract.CategoryEntry.COLUMN_CATEGORY,
            InventoryContract.CategoryEntry.COLUMN_BARCODE_PREFIX
    };
    private TextView nameView;
    private TextView barcodeView;
    private Uri mUri;
    private long categoryID = -1;

    public ViewCategoryDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_view_category_detail, container, false);

        // get current inventory id from bundle
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mUri = bundle.getParcelable(DETAILED_CATEGORY_KEY);
        }

        // assign text views
        nameView = (TextView) rootView.findViewById(R.id.category_detail_name);
        barcodeView = (TextView) rootView.findViewById(R.id.category_detail_barcode);

        // implement fab


        // implement delete
        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        if (handleCategoryDeletion()) {
                            FragmentManager manager = getActivity().getSupportFragmentManager();
                            manager.popBackStack();
                        }
                        break;
                }
            }
        };

        Button delete = (Button) rootView.findViewById(R.id.view_category_detail_delete_button);
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
        getLoaderManager().initLoader(CATEGORY_DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mUri) {

            // get the category details from database
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    CATEGORY_DETAIL_COLUMNS,
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
            String name = data.getString(COL_CATEGORY_CAT);
            String barcode = data.getString(COL_CATEGORY_BARCODE);
            categoryID = data.getLong(COL_CATEGORY_ID);

            //place data into text views
            nameView.setText(name);
            barcodeView.setText(barcode);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private boolean handleCategoryDeletion() {
        boolean deleted = false;
        int rowDeleted = -1;
        Uri categoryUri = InventoryContract.CategoryEntry.buildCategoryUri();
        String selection = InventoryContract.CategoryEntry.TABLE_NAME + "." +
                InventoryContract.CategoryEntry._ID + " = ? ";
        String[] selectionArgs = {Long.toString(categoryID)};
        String message = "Category delete failed.";

        if (categoryID != -1) {
            rowDeleted = getContext().getContentResolver().delete(
                    categoryUri,
                    selection,
                    selectionArgs
            );
        }

        if (rowDeleted != 0) {
            message = "Category delete successful";
            deleted = true;
        }

        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        return deleted;
    }
}
