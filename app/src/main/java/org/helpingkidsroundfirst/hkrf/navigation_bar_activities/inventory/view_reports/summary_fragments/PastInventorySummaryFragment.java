package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_reports.summary_fragments;


import android.database.Cursor;
import android.net.Uri;
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
public class PastInventorySummaryFragment extends Fragment {

    private Uri pastUri;

    public PastInventorySummaryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_past_inventory_summary, container, false);

        // initialize text views
        TextView donorView = (TextView) rootView.findViewById(R.id.past_summary_donor);
        TextView dateView = (TextView) rootView.findViewById(R.id.past_summary_dates);
        TextView valueView = (TextView) rootView.findViewById(R.id.past_summary_total);
        TextView itemView = (TextView) rootView.findViewById(R.id.past_summary_items);

        // setup
        pastUri = InventoryContract.PastInventoryEntry.buildPastInventoryUri();

        // get values
        donorView.setText(getDonors());
        dateView.setText(getDates());
        valueView.setText(getValue());
        itemView.setText(getItems());

        return rootView;
    }

    private String getItems() {

        // setup
        int total = 0;
        String[] projection = {"DISTINCT " + InventoryContract.PastInventoryEntry.COLUMN_NAME,
                "1 _id"
        };

        // get cursor
        Cursor cursor = getContext().getContentResolver().query(
                pastUri,
                projection,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {

            // get # of items
            total = cursor.getCount();
            cursor.close();
        }

        return Integer.toString(total);
    }

    private String getValue() {

        // setup
        int total = 0;
        String[] projection = {"1 _id",
                InventoryContract.PastInventoryEntry.COLUMN_VALUE,
                InventoryContract.PastInventoryEntry.COLUMN_QTY
        };

        // get cursor
        Cursor cursor = getContext().getContentResolver().query(
                pastUri,
                projection,
                null,
                null,
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

    private String getDates() {

        // setup
        String dates = "None";
        String[] projection = {"DISTINCT " + InventoryContract.PastInventoryEntry.COLUMN_DATE_SHIPPED,
                "1 _id"
        };

        // get cursor
        Cursor cursor = getContext().getContentResolver().query(
                pastUri,
                projection,
                null,
                null,
                InventoryContract.PastInventoryEntry.COLUMN_DATE_SHIPPED
        );

        if (cursor != null && cursor.moveToFirst()) {

            // put in first date
            dates = cursor.getString(0);

            // get rest of dates
            while (cursor.moveToNext()) {
                dates += ", " + cursor.getString(0);
            }

            cursor.close();
        }

        return dates;
    }

    private String getDonors() {

        // setup
        String donors = "None";
        String[] projection = {"DISTINCT " + InventoryContract.PastInventoryEntry.COLUMN_DONOR,
                "1 _id"
        };

        // get cursor
        Cursor cursor = getContext().getContentResolver().query(
                pastUri,
                projection,
                null,
                null,
                InventoryContract.PastInventoryEntry.COLUMN_DONOR
        );

        if (cursor != null && cursor.moveToFirst()) {

            // put in first donor
            donors = cursor.getString(0);

            // get rest of donors
            while (cursor.moveToNext()) {
                donors += ", " + cursor.getString(0);
            }

            cursor.close();
        }

        return donors;
    }
}
