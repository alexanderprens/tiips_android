package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_reports.shipment_summary;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.helpingkidsroundfirst.hkrf.R;
import org.helpingkidsroundfirst.hkrf.data.InventoryContract;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShipmentSummaryFragment extends Fragment {

    public static final String DATE_KEY = "date_key";
    private String dateSelected;

    public ShipmentSummaryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_shipment_summary, container, false);

        // init text views
        TextView dateView = (TextView) rootView.findViewById(R.id.shipment_sum_date);
        TextView valueView = (TextView) rootView.findViewById(R.id.shipment_sum_value);

        // get date from arguments
        Bundle bundle = this.getArguments();
        if (bundle != null && bundle.containsKey(DATE_KEY)) {
            dateSelected = bundle.getString(DATE_KEY);
        } else {
            dateSelected = "";
        }

        // set text views
        dateView.setText(dateSelected);
        valueView.setText(getValue());

        return rootView;
    }

    private String getValue() {

        int total = 0;
        String[] projection = {"1 _id",
                InventoryContract.PastInventoryEntry.COLUMN_VALUE,
                InventoryContract.PastInventoryEntry.COLUMN_QTY
        };
        String selection = InventoryContract.PastInventoryEntry.COLUMN_DATE_SHIPPED + " = ? ";
        String[] selectionArgs = {dateSelected};

        // get cursor
        Cursor cursor = getContext().getContentResolver().query(
                InventoryContract.PastInventoryEntry.buildPastInventoryUri(),
                projection,
                selection,
                selectionArgs,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {

            // put in first warehouse
            total = cursor.getInt(1) * cursor.getInt(2);

            // get rest of value
            do {
                total += cursor.getInt(1) * cursor.getInt(2);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return Integer.toString(total);
    }
}
