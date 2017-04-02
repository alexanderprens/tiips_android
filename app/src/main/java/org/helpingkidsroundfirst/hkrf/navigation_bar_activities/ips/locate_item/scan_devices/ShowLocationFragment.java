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
    private static final double WALL_BUFFER = 10;
    private double[] distanceValues = new double[4];
    private TextView coordinateView;
    private double[] error = new double[3];
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
            findLocation();
        }
    }

    private void findLocation() {

        double[] coordinates;
        String text = coordinateView.getText().toString();

        // convert rssi to distance
        for (int i = 0; i < 4; i++) {
            distanceValues[i] = convertRssiToDistance(distanceValues[i]);
        }

        // NILEX
        coordinates = nilex();

        text += String.format(Locale.US, "%.1f,%.1f\n", coordinates[0], coordinates[1]);
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

    /*
    private void simplex() {
        int worst;
        int oldWorst = -1;
        double centroid[] = new double[2];

        // loop until found
        while (true) {

            // get errors
            error[0] = calcError(triangle[0]);
            error[1] = calcError(triangle[1]);
            error[2] = calcError(triangle[2]);

            // find worst error
            if (error[0] > error[1]) {
                if (error[0] > error[2]) {
                    worst = 0;
                } else {
                    worst = 2;
                }
            } else {
                if (error[2] > error[1]) {
                    worst = 2;
                } else {
                    worst = 1;
                }
            }

            // find centroid
            if (worst == 0) {
                centroid[0] = (triangle[1][0] + triangle[2][0]) / 2.0;
                centroid[1] = (triangle[1][1] + triangle[2][1]) / 2.0;
            } else if (worst == 1) {
                centroid[0] = (triangle[0][0] + triangle[2][0]) / 2.0;
                centroid[1] = (triangle[0][1] + triangle[2][1]) / 2.0;
            } else {
                centroid[0] = (triangle[1][0] + triangle[0][0]) / 2.0;
                centroid[1] = (triangle[1][1] + triangle[0][1]) / 2.0;
            }

            // reflect point with most error
            triangle[worst][0] = centroid[0] + (centroid[0] - triangle[worst][0]);
            triangle[worst][1] = centroid[1] + (centroid[1] - triangle[worst][1]);

            // if worst hasn't changed, exit loop
            if (worst == oldWorst) {
                break;
            }

            oldWorst = worst;
        }
    }
    */
}

