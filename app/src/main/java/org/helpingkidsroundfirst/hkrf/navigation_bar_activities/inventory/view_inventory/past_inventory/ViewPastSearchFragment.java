package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory.past_inventory;

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
 * {@link OnSearchButtonListener} interface
 * to handle interaction events.
 */
public class ViewPastSearchFragment extends Fragment {

    private static final String[] CATEGORY_COLUMNS = {
            InventoryContract.CategoryEntry.COLUMN_CATEGORY
    };
    private static int[] TO_VIEWS = {
            android.R.id.text1
    };
    private OnSearchButtonListener mListener;
    private String nameInput;
    private long categoryInput;
    private String dateInput;
    private String donorInput;
    private String barcodeInput;
    private CheckBox categoryCheck;
    private CheckBox dateCheck;
    private CheckBox donorCheck;
    private CheckBox barcodeCheck;

    public ViewPastSearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_view_past_search, container, false);

        // init inputs
        nameInput = "";
        categoryInput = -1;
        dateInput = "";
        donorInput = "";
        barcodeInput = "";

        // name search
        final EditText nameText = (EditText) rootView.findViewById(R.id.past_search_name);
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
        categoryCheck = (CheckBox) rootView.findViewById(R.id.past_search_cat_check);
        final Spinner categorySpinner = (Spinner) rootView.findViewById(
                R.id.past_search_cat_spinner);

        Cursor categoryCursor = getContext().getContentResolver().query(
                InventoryContract.PastInventoryEntry.buildPastInventoryUri(),
                new String[]{"DISTINCT " + InventoryContract.CategoryEntry.TABLE_NAME + "." +
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
        dateCheck = (CheckBox) rootView.findViewById(R.id.past_search_date_check);
        final Spinner dateSpinner = (Spinner) rootView.findViewById(
                R.id.past_search_date_spinner);

        Cursor dateCursor = getContext().getContentResolver().query(
                InventoryContract.PastInventoryEntry.buildPastInventoryUri(),
                new String[]{"DISTINCT 1 _id",
                        InventoryContract.PastInventoryEntry.COLUMN_DATE_SHIPPED},
                null,
                null,
                InventoryContract.PastInventoryEntry.COLUMN_DATE_SHIPPED
        );

        final SimpleCursorAdapter dateAdapter = new SimpleCursorAdapter(
                getContext(),
                android.R.layout.simple_spinner_item,
                dateCursor,
                new String[]{InventoryContract.PastInventoryEntry.COLUMN_DATE_SHIPPED},
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
                        .PastInventoryEntry.COLUMN_DATE_SHIPPED));
                //Toast.makeText(getContext(), dateInput, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // donor search
        donorCheck = (CheckBox) rootView.findViewById(R.id.past_search_donor_check);
        final Spinner donorSpinner = (Spinner) rootView.findViewById(
                R.id.past_search_donor_spinner);

        Cursor donorCursor = getContext().getContentResolver().query(
                InventoryContract.PastInventoryEntry.buildPastInventoryUri(),
                new String[]{"DISTINCT 1 _id",
                        InventoryContract.PastInventoryEntry.COLUMN_DONOR},
                null,
                null,
                InventoryContract.PastInventoryEntry.COLUMN_DONOR
        );

        final SimpleCursorAdapter donorAdapter = new SimpleCursorAdapter(
                getContext(),
                android.R.layout.simple_spinner_item,
                donorCursor,
                new String[]{InventoryContract.PastInventoryEntry.COLUMN_DONOR},
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
                        .PastInventoryEntry.COLUMN_DONOR));
                //Toast.makeText(getContext(), donorInput, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // barcode search
        barcodeCheck = (CheckBox) rootView.findViewById(R.id.past_search_barcode_check);
        final Spinner barcodeSpinner = (Spinner) rootView.findViewById(
                R.id.past_search_barcode_spinner);

        Cursor barcodeCursor = getContext().getContentResolver().query(
                InventoryContract.PastInventoryEntry.buildPastInventoryUri(),
                new String[]{"DISTINCT 1 _id",
                        InventoryContract.PastInventoryEntry.COLUMN_BARCODE_ID},
                null,
                null,
                InventoryContract.PastInventoryEntry.COLUMN_BARCODE_ID
        );

        final SimpleCursorAdapter barcodeAdapter = new SimpleCursorAdapter(
                getContext(),
                android.R.layout.simple_spinner_item,
                barcodeCursor,
                new String[]{InventoryContract.PastInventoryEntry.COLUMN_BARCODE_ID},
                TO_VIEWS,
                0
        );

        barcodeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        barcodeSpinner.setAdapter(barcodeAdapter);
        barcodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) barcodeSpinner.getSelectedItem();
                barcodeInput = cursor.getString(cursor.getColumnIndex(InventoryContract
                        .PastInventoryEntry.COLUMN_BARCODE_ID));
                //Toast.makeText(getContext(), barcodeInput, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // search button
        final Button searchButton = (Button) rootView.findViewById(R.id.past_search_button);
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
        if (context instanceof OnSearchButtonListener) {
            mListener = (OnSearchButtonListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSearchButtonListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void searchButtonHandler() {
        String selection;
        String[] selectionArgs = {"%", "%", "%", "%", "%"};
        Uri pastInventoryUri = InventoryContract.PastInventoryEntry.buildPastInventoryUri();

        // build query
        selection = InventoryContract.PastInventoryEntry.COLUMN_NAME + " LIKE ? AND " +
                InventoryContract.CategoryEntry.TABLE_NAME + "." +
                InventoryContract.CategoryEntry._ID + " LIKE ? AND " +
                InventoryContract.PastInventoryEntry.COLUMN_DATE_SHIPPED + " LIKE ? AND " +
                InventoryContract.PastInventoryEntry.COLUMN_DONOR + " LIKE ? AND " +
                InventoryContract.PastInventoryEntry.COLUMN_BARCODE_ID + " LIKE ? ";

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

        // check if barcode
        if (barcodeCheck.isChecked()) {
            selectionArgs[4] = barcodeInput;
        }

        Cursor cursor = getContext().getContentResolver().query(
                pastInventoryUri,
                null,
                selection,
                selectionArgs,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            mListener.pastSearchResults(pastInventoryUri, selection, selectionArgs);
            cursor.close();
        } else {
            // no results
            Toast.makeText(getContext(), getContext().getResources().getString(
                    R.string.search_no_results), Toast.LENGTH_LONG).show();
        }
    }

    public interface OnSearchButtonListener {
        void pastSearchResults(Uri uri, String selection, String[] selectionArgs);
    }
}
