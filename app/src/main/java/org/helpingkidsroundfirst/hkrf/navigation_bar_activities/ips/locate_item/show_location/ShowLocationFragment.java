package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.ips.locate_item.show_location;


import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
    private static final double LENGTH = 15.0;
    private static final double WIDTH = 30.0;
    private static final double WALL_BUFFER = 10.0;
    private double[] distanceValues = new double[4];
    private double[] tagLocation = new double[2];
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

        // coordinates
        coordinateView = (TextView) rootView.findViewById(R.id.show_location_coord);
        coordinateView.setText("");

        // header
        String header = getContext().getResources().getString(R.string.location_of) + " " +
                String.format(Locale.US, "%2d", InventoryContract.TagEntry.getTagIdFromUri(mUri));
        final TextView headerView = (TextView) rootView.findViewById(R.id.show_location_header);
        headerView.setText(header);

        // tag alert button
        rootView.findViewById(R.id.show_location_alert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // attempt to write to tag
                ((OnShowLocation) getActivity()).onAlertButtonClick(mUri);
            }
        });

        // get data from tag
        getTagData();

        // draw tag location
        final ImageView imageView = (ImageView) rootView.findViewById(R.id.show_location_graph);
        imageView.setImageDrawable(new TagLocationGraph());

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

        // set location
        tagLocation[0] = x;
        tagLocation[1] = y;

        // write result to text view
        text += String.format(Locale.US, "%.0f,%.0f\n", x, y);
        coordinateView.setText(text);
    }

    private double convertRssiToDistance(double rssi) {
        return 131.03 * Math.log(rssi) - 537.94;
    }

    private double[] nilex() {

        double coordinates[] = {0, 0};
        double best[] = {0, 0};
        double bestError, currentError;
        double x = 0.0 - WALL_BUFFER;
        double y;
        final double X_MAX = WIDTH + WALL_BUFFER;
        final double Y_MAX = LENGTH + WALL_BUFFER;

        // get error starting value
        bestError = calcError(coordinates);
        best[0] = coordinates[0] + WALL_BUFFER;
        best[1] = coordinates[1] + WALL_BUFFER;

        while (x <= X_MAX) {

            coordinates[0] = x;
            y = 0.0 - WALL_BUFFER;

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

    private float dipToPix(float dips) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dips,
                getResources().getDisplayMetrics());
    }

    public interface OnShowLocation {
        void onAlertButtonClick(Uri uri);
    }

    private class TagLocationGraph extends Drawable {

        @Override
        public void draw(@NonNull Canvas canvas) {

            Paint paint = new Paint();
            paint.setColor(Color.BLACK);

            // constants
            final float SCALE = 10;
            final float BUFFER = dipToPix(16);
            final float PLOT_W = (float) ((WIDTH + WALL_BUFFER * 2.0) * SCALE);
            final float PLOT_L = (float) ((LENGTH + WALL_BUFFER * 2.0) * SCALE);

            // draw location
            float y = (float) tagLocation[0];
            y = PLOT_W - y * SCALE;
            y = dipToPix(y) + BUFFER;
            float x = (float) tagLocation[1];
            x = PLOT_L - x * SCALE;
            x = dipToPix(x) + BUFFER;
            float rad = 10 * SCALE;
            rad = dipToPix(rad);
            paint.setColor(Color.BLUE);
            canvas.drawCircle(x, y, rad, paint);

            // draw outline
            paint.setStrokeWidth(2);
            paint.setColor(Color.BLACK);
            canvas.drawLine(BUFFER, BUFFER, dipToPix(PLOT_L) + BUFFER, BUFFER, paint);
            canvas.drawLine(BUFFER, BUFFER, BUFFER, dipToPix(PLOT_W) + BUFFER, paint);
            canvas.drawLine(dipToPix(PLOT_L) + BUFFER, BUFFER, dipToPix(PLOT_L) + BUFFER,
                    dipToPix(PLOT_W) + BUFFER, paint);
            canvas.drawLine(BUFFER, dipToPix(PLOT_W) + BUFFER, dipToPix(PLOT_L) + BUFFER,
                    dipToPix(PLOT_W) + BUFFER, paint);

            // draw grid
            paint.setStrokeWidth(0);
            paint.setColor(Color.GRAY);
            float l = dipToPix(5 * SCALE) + BUFFER;
            while (l < dipToPix(PLOT_L) + BUFFER) {
                canvas.drawLine(l, BUFFER, l, dipToPix(PLOT_W) + BUFFER, paint);
                l += dipToPix(5 * SCALE);
            }
            float w = BUFFER + dipToPix(PLOT_W) - dipToPix(5 * SCALE);
            while (w > BUFFER) {
                canvas.drawLine(BUFFER, w, dipToPix(PLOT_L) + BUFFER, w, paint);
                w -= dipToPix(5 * SCALE);
            }
        }

        @Override
        public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {

        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {

        }

        @Override
        public int getOpacity() {
            return PixelFormat.OPAQUE;
        }
    }
}

