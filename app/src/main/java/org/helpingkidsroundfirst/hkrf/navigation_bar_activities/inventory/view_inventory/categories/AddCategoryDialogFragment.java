package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory.categories;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
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
 * Created by alexa on 2/4/2017.
 */

public class AddCategoryDialogFragment extends DialogFragment implements
        View.OnClickListener {

    // dialog inputs
    private String categoryInput;
    private String barcodeInput;
    private String error;
    private AddCategoryListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            mListener = (AddCategoryListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Must implement AddCategoryDialogFragment listener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // use builder class to create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_category, null);

        // set click listeners on buttons
        view.findViewById(R.id.add_category_button_cancel).setOnClickListener(this);
        view.findViewById(R.id.add_category_button_ok).setOnClickListener(this);

        // init inputs
        categoryInput = "";
        barcodeInput = "";

        // listen to category inputs
        final EditText categoryText = (EditText) view.findViewById(R.id.add_category_text);
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

        // listen to barcode input
        final EditText barcodeText = (EditText) view.findViewById(R.id.add_category_barcode);
        barcodeText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // required stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                barcodeInput = s.toString();
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
            case R.id.add_category_button_cancel:
                this.dismiss();
                break;

            case R.id.add_category_button_ok:
                if (addCategory()) {
                    mListener.onButtonOK();
                    this.dismiss();
                } else {
                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                // oops
                break;
        }
    }

    // attempt to add category into db
    private boolean addCategory() {
        boolean added = false;

        // validate inputs
        if (dialogValidation()) {

            // check if category exists
            if (!checkIfCategoryExists()) {

                // attempt to add category
                if (addCategoryToDB() != -1) {
                    added = true;
                } else {
                    error = getContext().getResources().getString(R.string.error_adding_category);
                }

            } else {
                //
            }
        } else {
            error = getContext().getResources().getString(R.string.validation_error);
        }

        return added;
    }

    // validate input data
    private boolean dialogValidation() {
        boolean check = true;

        // check if category is empty
        if (categoryInput.isEmpty()) {
            check = false;
            Toast.makeText(getActivity(), getContext().getResources().getString(
                    R.string.error_category_empty), Toast.LENGTH_SHORT).show();
        }

        // check if barcode prefix is empty or greater than
        if (barcodeInput.isEmpty()) {
            check = false;
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.error_category_prefix_empty),
                    Toast.LENGTH_SHORT).show();

        } else {

            // check if barcode prefix is two letters
            if (barcodeInput.length() != 2) {
                check = false;
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.error_prefix_not_two),
                        Toast.LENGTH_SHORT).show();
            }
        }

        return check;
    }

    private boolean checkIfCategoryExists() {
        boolean prefix;
        boolean category;

        // check if category already exists in the db
        Cursor catCursor = getContext().getContentResolver().query(
                InventoryContract.CategoryEntry.CONTENT_URI,
                new String[]{InventoryContract.CategoryEntry._ID},
                InventoryContract.CategoryEntry.COLUMN_CATEGORY + " = ?",
                new String[]{categoryInput},
                null
        );

        // if category exists, return true
        category = catCursor.moveToFirst();

        // check if barcode prefix exists
        catCursor = getContext().getContentResolver().query(
                InventoryContract.CategoryEntry.CONTENT_URI,
                new String[]{InventoryContract.CategoryEntry._ID},
                InventoryContract.CategoryEntry.COLUMN_BARCODE_PREFIX + " = ?",
                new String[]{barcodeInput},
                null
        );

        // check if prefix exists
        prefix = catCursor.moveToFirst();

        // set error message
        if (prefix && category) {
            error = getContext().getResources().getString(R.string.error_cat_and_prefix_exists);
        } else if (category) {
            error = getContext().getResources().getString(R.string.error_category_exists);
        } else if (prefix) {
            error = getContext().getResources().getString(R.string.error_prefix_exists);
        }

        catCursor.close();
        return prefix | category;
    }

    private long addCategoryToDB() {
        long categoryId;

        ContentValues categoryValues = new ContentValues();

        // make content values of category data
        categoryValues.put(InventoryContract.CategoryEntry.COLUMN_CATEGORY, categoryInput);
        categoryValues.put(InventoryContract.CategoryEntry.COLUMN_BARCODE_PREFIX, barcodeInput);

        // insert item into database
        Uri insertedUri = getContext().getContentResolver().insert(
                InventoryContract.CategoryEntry.CONTENT_URI,
                categoryValues
        );

        categoryId = ContentUris.parseId(insertedUri);

        return categoryId;
    }

    public interface AddCategoryListener {
        void onButtonOK();
    }
}
