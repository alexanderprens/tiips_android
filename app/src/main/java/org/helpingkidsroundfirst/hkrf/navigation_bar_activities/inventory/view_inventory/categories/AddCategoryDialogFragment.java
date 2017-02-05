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
            if (!checkIfCategoryExists(categoryInput)) {

                // attempt to add category
                if (addCategoryToDB() != -1) {
                    added = true;
                } else {
                    error = "Error adding category to database";
                }

            } else {
                error = "Category already exists in database";
            }
        }

        return added;
    }

    // validate input data
    private boolean dialogValidation() {
        boolean check = true;

        if (categoryInput.isEmpty()) {
            check = false;
            Toast.makeText(getActivity(), "Category cannot be empty", Toast.LENGTH_SHORT).show();
        }

        // TODO: 2/4/2017 two letter validation for barcode

        return check;
    }

    private boolean checkIfCategoryExists(String category) {
        boolean exists;

        // check if category already exists in the db
        Cursor catCursor = getContext().getContentResolver().query(
                InventoryContract.CategoryEntry.CONTENT_URI,
                new String[]{InventoryContract.CategoryEntry._ID},
                InventoryContract.CategoryEntry.COLUMN_CATEGORY + " = ?",
                new String[]{category},
                null
        );

        // if category exists, return true
        exists = catCursor.moveToFirst();

        catCursor.close();
        return exists;
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