package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.ips.locate_item.scan_devices;


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

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowLocationFragment extends Fragment {

    public static final String URI_KEY = "uri_key";
    private static final int MASTER = 0;
    private static final int SIDE_1 = 1;
    private static final int SIDE_2 = 2;
    private static final int SIDE_3 = 3;
    private static final double LENGTH = 35.0;
    private static final double WIDTH = 45.0;
    private static final double WALL_BUFFER = 5.0;
    private double[] distanceValues = new double[4];
    private TextView coordinateView;
    private Uri mUri;

    public ShowLocationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_show_location, container, false);

        // init variables
        mUri = null;

        // get id
        Bundle bundle = this.getArguments();
        if (bundle != null && bundle.containsKey(URI_KEY)) {
            mUri = bundle.getParcelable(URI_KEY);
        }

        coordinateView = (TextView) rootView.findViewById(R.id.show_location_coord);
        coordinateView.setText("");

        getTagData();

        return rootView;
    }

    private void getTagData() {

        String[] dataString = new String[4];
        double[][] tagDistanceValues = new double[5][4];
        double[] coordinates;
        double x = 0, y = 0;
        String text = coordinateView.getText().toString();

        // get cursor of data
        String[] projection = {
                InventoryContract.TagEntry.TABLE_NAME + "." + InventoryContract.TagEntry._ID +
                        " AS _id",
                InventoryContract.TagEntry.COLUMN_RSSI_M,
                InventoryContract.TagEntry.COLUMN_RSSI_1,
                InventoryContract.TagEntry.COLUMN_RSSI_2,
                InventoryContract.TagEntry.COLUMN_RSSI_3
        };

        Cursor cursor = getContext().getContentResolver().query(
                mUri,
                projection,
                null,
                null,
                null
        );

        // get cursor data
        if (cursor != null && cursor.moveToFirst()) {

            dataString[0] = cursor.getString(cursor.getColumnIndex(
                    InventoryContract.TagEntry.COLUMN_RSSI_M));
            dataString[1] = cursor.getString(cursor.getColumnIndex(
                    InventoryContract.TagEntry.COLUMN_RSSI_1));
            dataString[2] = cursor.getString(cursor.getColumnIndex(
                    InventoryContract.TagEntry.COLUMN_RSSI_2));
            dataString[3] = cursor.getString(cursor.getColumnIndex(
                    InventoryContract.TagEntry.COLUMN_RSSI_3));

            cursor.close();
        } else {
            return;
        }

        // parse data
        for (int i = 0; i < 5; i++) {
            int start = i * 2;
            int end = start + 2;
            tagDistanceValues[i][0] = (double) Long.parseLong(dataString[0].substring(start, end), 16);
            tagDistanceValues[i][1] = (double) Long.parseLong(dataString[1].substring(start, end), 16);
            tagDistanceValues[i][2] = (double) Long.parseLong(dataString[2].substring(start, end), 16);
            tagDistanceValues[i][3] = (double) Long.parseLong(dataString[3].substring(start, end), 16);
        }

        // call location
        for (int i = 0; i < 5; i++) {
            distanceValues = tagDistanceValues[i];

            // convert rssi to distance
            for (int j = 0; j < 4; j++) {
                distanceValues[j] = convertRssiToDistance(distanceValues[j]);
            }

            coordinates = nilex();
            x += coordinates[0];
            y += coordinates[1];
        }

        // average
        x = x / 5.0;
        y = y / 5.0;

        // write result to text view
        text += String.format(Locale.US, "%.0f,%.0f\n", x, y);
        coordinateView.setText(text);
    }

    private double convertRssiToDistance(double rssi) {
        return 4.0 * (Math.pow(10.0, -6.0)) * (Math.pow(rssi, 3.6718));
    }

    private double[] nilex() {

        double coordinates[] = {0, 0};
        double best[] = {0, 0};
        double bestError, currentError;
        double x = 0.0 - WALL_BUFFER;
        double y = 0.0 - WALL_BUFFER;
        final double X_MAX = WIDTH + WALL_BUFFER;
        final double Y_MAX = LENGTH + WALL_BUFFER;

        // get error starting value
        bestError = calcError(coordinates);
        best[0] = coordinates[0] + WALL_BUFFER;
        best[1] = coordinates[1] + WALL_BUFFER;

        while (x <= X_MAX) {

            coordinates[0] = x;

            while (y <= Y_MAX) {

                coordinates[1] = y;
                currentError = calcError(coordinates);

                if (currentError < bestError) {
                    bestError = currentError;
                    best[0] = coordinates[0] + WALL_BUFFER;
                    best[1] = coordinates[1] + WALL_BUFFER;
                }
                y++;
            }
            x++;
        }

        return best;
    }

    // calcError function
    private double calcError(double[] coordinate) {
        return Math.abs(Math.sqrt(Math.pow(coordinate[0], 2.0) + Math.pow(coordinate[1], 2.0)) - distanceValues[MASTER]) +
                Math.abs(Math.sqrt(Math.pow(WIDTH - coordinate[0], 2.0) + Math.pow(coordinate[1], 2.0)) - distanceValues[SIDE_1]) +
                Math.abs(Math.sqrt(Math.pow(coordinate[0], 2.0) + Math.pow(LENGTH - coordinate[1], 2.0)) - distanceValues[SIDE_2]) +
                Math.abs(Math.sqrt(Math.pow(WIDTH - coordinate[0], 2.0) + Math.pow(LENGTH - coordinate[1], 2.0)) - distanceValues[SIDE_3]);
    }
}

