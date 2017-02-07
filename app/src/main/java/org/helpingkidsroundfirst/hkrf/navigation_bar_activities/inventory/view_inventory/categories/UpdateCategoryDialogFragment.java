package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory.categories;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
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

public class UpdateCategoryDialogFragment extends DialogFragment implements
        View.OnClickListener {

    public static final String CATEGORY_KEY = "category_string";
    public static final String ID_KEY = "id_long";
    private String categoryInput;
    private String error;
    private String previousCategory;
    private long categoryId;
    private UpdateCategoryListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            mListener = (UpdateCategoryListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Must implement UpdateCategoryListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // use builder ot create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_update_category, null);

        // set click listeners on buttons
        view.findViewById(R.id.update_category_button_cancel).setOnClickListener(this);
        view.findViewById(R.id.update_category_button_ok).setOnClickListener(this);

        // init inputs
        categoryInput = "";

        // get arguments from bundle
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            previousCategory = bundle.getString(CATEGORY_KEY);
            categoryId = bundle.getLong(ID_KEY);
        }

        // listen to category inputs
        final EditText categoryText = (EditText) view.findViewById(R.id.update_category_category);
        categoryText.setText(previousCategory);
        categoryText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // required stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                categoryInput = s.toString();
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
            case R.id.update_category_button_ok:
                if (updateCategory()) {
                    mListener.onButtonOK();
                    this.dismiss();
                } else {
                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.update_category_button_cancel:
                this.dismiss();
                break;
        }
    }

    private boolean updateCategory() {
        boolean added = false;

        // validate inputs
        if (dialogValidation()) {

            // check if category already exists
            if (!checkIfCategoryExists()) {

                // attempt to update category
                if (attemptUpdateCateogory() != 0) {
                    added = true;
                } else {
                    error = getContext().getResources().getString(R.string.error_updating_category);
                }
            }
        } else {
            error = getContext().getResources().getString(R.string.validation_error);
        }

        return added;
    }

    private boolean dialogValidation() {
        boolean check = true;

        // check if input is empty
        if (categoryInput.isEmpty()) {
            check = false;
            Toast.makeText(getContext(), getContext().getResources().getString(
                    R.string.validation_category_empty), Toast.LENGTH_SHORT).show();
        }

        return check;
    }

    private boolean checkIfCategoryExists() {
        boolean check;

        // check if category already exists in the db
        Cursor catCursor = getContext().getContentResolver().query(
                InventoryContract.CategoryEntry.CONTENT_URI,
                new String[]{InventoryContract.CategoryEntry._ID},
                InventoryContract.CategoryEntry.COLUMN_CATEGORY + " = ?",
                new String[]{categoryInput},
                null
        );

        // if category exists, return true
        check = catCursor.moveToFirst();
        error = getContext().getResources().getString(R.string.validation_category_exists);
        return check;
    }

    private int attemptUpdateCateogory() {
        int rowsUpdated;
        Uri categoryUri = InventoryContract.CategoryEntry.buildCategoryUri();
        String selection = InventoryContract.CategoryEntry.TABLE_NAME + "." +
                InventoryContract.CategoryEntry._ID + " = ? ";
        String[] selectionArgs = {Long.toString(categoryId)};

        ContentValues updatedValues = new ContentValues();
        updatedValues.put(InventoryContract.CategoryEntry.COLUMN_CATEGORY, categoryInput);

        rowsUpdated = getContext().getContentResolver().update(
                categoryUri,
                updatedValues,
                selection,
                selectionArgs
        );

        return rowsUpdated;
    }

    public interface UpdateCategoryListener {
        void onButtonOK();
    }
}
