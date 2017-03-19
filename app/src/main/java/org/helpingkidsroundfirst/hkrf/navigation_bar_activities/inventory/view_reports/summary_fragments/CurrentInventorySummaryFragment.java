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
public class CurrentInventorySummaryFragment extends Fragment {

    private Uri currentUri;

    public CurrentInventorySummaryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_current_inventory_summary, container, false);

        // initialize text views
        TextView warehouseView = (TextView) rootView.findViewById(R.id.current_summary_warehouse);
        TextView donorView = (TextView) rootView.findViewById(R.id.current_summary_donor);
        TextView dateView = (TextView) rootView.findViewById(R.id.current_summary_dates);
        TextView valueView = (TextView) rootView.findViewById(R.id.current_summary_total);
        TextView itemView = (TextView) rootView.findViewById(R.id.current_summary_items);

        // setup
        currentUri = InventoryContract.CurrentInventoryEntry.buildCurrentInventoryUri();

        // get values
        warehouseView.setText(getWarehouses());
        donorView.setText(getDonors());
        dateView.setText(getDates());
        valueView.setText(getValue());
        itemView.setText(getItems());

        return rootView;
    }

    private String getItems() {

        // setup
        int total = 0;
        String[] projection = {"DISTINCT " + InventoryContract.ItemEntry.COLUMN_NAME,
                "1 _id"
        };

        // get cursor
        Cursor cursor = getContext().getContentResolver().query(
                currentUri,
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
                InventoryContract.ItemEntry.COLUMN_VALUE,
                InventoryContract.CurrentInventoryEntry.COLUMN_QTY
        };

        // get cursor
        Cursor cursor = getContext().getContentResolver().query(
                currentUri,
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
        String[] projection = {"DISTINCT " + InventoryContract.CurrentInventoryEntry.COLUMN_DATE_RECEIVED,
                "1 _id"
        };

        // get cursor
        Cursor cursor = getContext().getContentResolver().query(
                currentUri,
                projection,
                null,
                null,
                InventoryContract.CurrentInventoryEntry.COLUMN_DATE_RECEIVED
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
        String[] projection = {"DISTINCT " + InventoryContract.CurrentInventoryEntry.COLUMN_DONOR,
                "1 _id"
        };

        // get cursor
        Cursor cursor = getContext().getContentResolver().query(
                currentUri,
                projection,
                null,
                null,
                InventoryContract.CurrentInventoryEntry.COLUMN_DONOR
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

    private String getWarehouses() {

        // setup
        String warehouses = "None";
        String[] projection = {"DISTINCT " + InventoryContract.CurrentInventoryEntry.COLUMN_WAREHOUSE,
                "1 _id"
        };

        // get cursor
        Cursor cursor = getContext().getContentResolver().query(
                currentUri,
                projection,
                null,
                null,
                InventoryContract.CurrentInventoryEntry.COLUMN_WAREHOUSE
        );

        if (cursor != null && cursor.moveToFirst()) {

            // put in first warehouse
            warehouses = cursor.getString(0);

            // get rest of warehouses
            while (cursor.moveToNext()) {
                warehouses += ", " + cursor.getString(0);
            }

            cursor.close();
        }

        return warehouses;
    }
}
