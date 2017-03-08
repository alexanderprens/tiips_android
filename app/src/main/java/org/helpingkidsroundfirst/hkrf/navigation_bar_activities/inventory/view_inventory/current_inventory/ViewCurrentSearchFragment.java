package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory.current_inventory;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.helpingkidsroundfirst.hkrf.R;
import org.helpingkidsroundfirst.hkrf.data.InventoryContract;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnSearchButton} interface
 * to handle interaction events.
 */
public class ViewCurrentSearchFragment extends Fragment {

    private static final String[] CATEGORY_COLUMNS = {
            InventoryContract.CategoryEntry.COLUMN_CATEGORY
    };
    private static int[] TO_VIEWS = {
            android.R.id.text1
    };
    private OnSearchButton mListener;
    private String nameInput;
    private long categoryInput;
    private String dateInput;
    private String donorInput;
    private String warehouseInput;
    private CheckBox categoryCheck;
    private CheckBox dateCheck;
    private CheckBox donorCheck;
    private CheckBox warehouseCheck;

    public ViewCurrentSearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_view_current_search, container, false);

        // init inputs
        nameInput = "";
        categoryInput = -1;
        dateInput = "";
        donorInput = "";
        warehouseInput = "";

        // name search
        final EditText nameText = (EditText) rootView.findViewById(R.id.current_search_name);
        nameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // required stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nameInput = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // required stub
            }
        });

        // category search
        categoryCheck = (CheckBox) rootView.findViewById(R.id.current_search_cat_check);
        final Spinner categorySpinner = (Spinner) rootView.findViewById(
                R.id.current_search_cat_spinner);

        Cursor categoryCursor = getContext().getContentResolver().query(
                InventoryContract.CurrentInventoryEntry.buildCurrentInventoryUri(),
                new String[]{InventoryContract.CategoryEntry.TABLE_NAME + "." +
                        InventoryContract.CategoryEntry._ID + " AS _id",
                        InventoryContract.CategoryEntry.COLUMN_CATEGORY
                },
                null,
                null,
                InventoryContract.CategoryEntry.COLUMN_CATEGORY
        );

        final SimpleCursorAdapter categoryAdapter = new SimpleCursorAdapter(
                getContext(),
                android.R.layout.simple_spinner_item,
                categoryCursor,
                CATEGORY_COLUMNS,
                TO_VIEWS,
                0
        );

        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoryInput = id;
                //Toast.makeText(getContext(), Long.toString(categoryInput), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // date search
        dateCheck = (CheckBox) rootView.findViewById(R.id.current_search_date_check);
        final Spinner dateSpinner = (Spinner) rootView.findViewById(
                R.id.current_search_date_spinner);

        Cursor dateCursor = getContext().getContentResolver().query(
                InventoryContract.CurrentInventoryEntry.buildCurrentInventoryUri(),
                new String[]{InventoryContract.CurrentInventoryEntry.TABLE_NAME + "."
                        + InventoryContract.CurrentInventoryEntry._ID + " AS _id",
                        InventoryContract.CurrentInventoryEntry.COLUMN_DATE_RECEIVED},
                null,
                null,
                InventoryContract.CurrentInventoryEntry.COLUMN_DATE_RECEIVED
        );

        final SimpleCursorAdapter dateAdapter = new SimpleCursorAdapter(
                getContext(),
                android.R.layout.simple_spinner_item,
                dateCursor,
                new String[]{InventoryContract.CurrentInventoryEntry.COLUMN_DATE_RECEIVED},
                TO_VIEWS,
                0
        );

        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateSpinner.setAdapter(dateAdapter);
        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) dateSpinner.getSelectedItem();
                dateInput = cursor.getString(cursor.getColumnIndex(InventoryContract
                        .CurrentInventoryEntry.COLUMN_DATE_RECEIVED));
                //Toast.makeText(getContext(), dateInput, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // donor search
        donorCheck = (CheckBox) rootView.findViewById(R.id.current_search_donor_check);
        final Spinner donorSpinner = (Spinner) rootView.findViewById(
                R.id.current_search_donor_spinner);

        Cursor donorCursor = getContext().getContentResolver().query(
                InventoryContract.CurrentInventoryEntry.buildCurrentInventoryUri(),
                new String[]{InventoryContract.CurrentInventoryEntry.TABLE_NAME + "."
                        + InventoryContract.CurrentInventoryEntry._ID + " AS _id",
                        InventoryContract.CurrentInventoryEntry.COLUMN_DONOR},
                null,
                null,
                InventoryContract.CurrentInventoryEntry.COLUMN_DONOR
        );

        final SimpleCursorAdapter donorAdapter = new SimpleCursorAdapter(
                getContext(),
                android.R.layout.simple_spinner_item,
                donorCursor,
                new String[]{InventoryContract.CurrentInventoryEntry.COLUMN_DONOR},
                TO_VIEWS,
                0
        );

        donorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        donorSpinner.setAdapter(donorAdapter);
        donorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) donorSpinner.getSelectedItem();
                donorInput = cursor.getString(cursor.getColumnIndex(InventoryContract
                        .CurrentInventoryEntry.COLUMN_DONOR));
                //Toast.makeText(getContext(), donorInput, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // warehouse search
        warehouseCheck = (CheckBox) rootView.findViewById(R.id.current_search_warehouse_check);
        final Spinner warehouseSpinner = (Spinner) rootView.findViewById(
                R.id.current_search_warehouse_spinner);

        Cursor warehouseCursor = getContext().getContentResolver().query(
                InventoryContract.CurrentInventoryEntry.buildCurrentInventoryUri(),
                new String[]{InventoryContract.CurrentInventoryEntry.TABLE_NAME + "."
                        + InventoryContract.CurrentInventoryEntry._ID + " AS _id",
                        InventoryContract.CurrentInventoryEntry.COLUMN_WAREHOUSE},
                null,
                null,
                InventoryContract.CurrentInventoryEntry.COLUMN_WAREHOUSE
        );

        final SimpleCursorAdapter warehouseAdapter = new SimpleCursorAdapter(
                getContext(),
                android.R.layout.simple_spinner_item,
                warehouseCursor,
                new String[]{InventoryContract.CurrentInventoryEntry.COLUMN_WAREHOUSE},
                TO_VIEWS,
                0
        );

        warehouseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        warehouseSpinner.setAdapter(warehouseAdapter);
        warehouseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) warehouseSpinner.getSelectedItem();
                warehouseInput = cursor.getString(cursor.getColumnIndex(InventoryContract
                        .CurrentInventoryEntry.COLUMN_WAREHOUSE));
                //Toast.makeText(getContext(), warehouseInput, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // search button
        final Button searchButton = (Button) rootView.findViewById(R.id.current_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchButtonHandler();
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSearchButton) {
            mListener = (OnSearchButton) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSearchButton");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    // handle search button click
    private void searchButtonHandler() {

        String selection;
        String[] selectionArgs = {"%", "%", "%", "%", "%"};
        Uri currentInventory = InventoryContract.CurrentInventoryEntry.buildCurrentInventoryUri();

        // build query
        selection = InventoryContract.ItemEntry.COLUMN_NAME + " LIKE ? AND " +
                InventoryContract.CategoryEntry.TABLE_NAME + "." +
                InventoryContract.CategoryEntry._ID + " LIKE ? AND " +
                InventoryContract.CurrentInventoryEntry.COLUMN_DATE_RECEIVED + " LIKE ? AND " +
                InventoryContract.CurrentInventoryEntry.COLUMN_DONOR + " LIKE ? AND " +
                InventoryContract.CurrentInventoryEntry.COLUMN_WAREHOUSE + " LIKE ? ";

        // check if name is used
        if (!nameInput.isEmpty()) {
            selectionArgs[0] = "%" + nameInput + "%";
        }

        // check if category is searched
        if (categoryCheck.isChecked()) {
            selectionArgs[1] = Long.toString(categoryInput);
        }

        // check if date
        if (dateCheck.isChecked()) {
            selectionArgs[2] = dateInput;
        }

        // check if donor
        if (donorCheck.isChecked()) {
            selectionArgs[3] = donorInput;
        }

        // check if warehouse
        if (warehouseCheck.isChecked()) {
            selectionArgs[4] = warehouseInput;
        }

        Cursor cursor = getContext().getContentResolver().query(
                currentInventory,
                null,
                selection,
                selectionArgs,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            mListener.currentSearchResults(currentInventory, selection, selectionArgs);
            cursor.close();
        } else {
            // no results
            Toast.makeText(getContext(), getContext().getResources().getString(
                    R.string.current_search_no_results), Toast.LENGTH_LONG).show();
        }
    }

    public interface OnSearchButton {
        void currentSearchResults(Uri uri, String selection, String[] selectionArgs);
    }
}
