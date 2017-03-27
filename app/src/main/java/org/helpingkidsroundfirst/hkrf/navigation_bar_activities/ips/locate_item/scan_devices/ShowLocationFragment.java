package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.ips.locate_item.scan_devices;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.helpingkidsroundfirst.hkrf.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowLocationFragment extends Fragment {

    private static final int MASTER = 0;
    private static final int SIDE_1 = 1;
    private static final int SIDE_2 = 2;
    private static final int SIDE_3 = 3;
    private static final double LENGTH = 35.0;
    private static final double WIDTH = 45.0;
    private String masterIn;
    private String side1In;
    private String side2In;
    private String side3In;
    private double[] distanceValues = new double[4];
    private TextView coordinateView;
    private double[][] triangle = new double[3][2];
    private double[] error = new double[3];

    public ShowLocationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_show_location, container, false);

        // init variables
        masterIn = "";
        side1In = "";
        side2In = "";
        side3In = "";

        // master in
        final EditText masterText = (EditText) rootView.findViewById(R.id.master_beacon_in);
        masterText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                masterIn = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        final EditText side1Text = (EditText) rootView.findViewById(R.id.side_becon1_in);
        side1Text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                side1In = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        final EditText side2Text = (EditText) rootView.findViewById(R.id.side_becon2_in);
        side2Text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                side2In = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        final EditText side3Text = (EditText) rootView.findViewById(R.id.side_becon3_in);
        side3Text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                side3In = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        rootView.findViewById(R.id.show_location_calc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findLocation();
            }
        });

        coordinateView = (TextView) rootView.findViewById(R.id.show_location_coord);

        return rootView;
    }

    private void findLocation() {

        double[] coordinates;
        double x = 0.0, y = 0.0;
        int count = 0;

        // get inputs
        distanceValues[MASTER] = (double) Long.parseLong(masterIn, 16);
        distanceValues[SIDE_1] = (double) Long.parseLong(side1In, 16);
        distanceValues[SIDE_2] = (double) Long.parseLong(side2In, 16);
        distanceValues[SIDE_3] = (double) Long.parseLong(side3In, 16);

        // convert rssi to distance
        for (int i = 0; i < 4; i++) {
            distanceValues[i] = convertRssiToDistance(distanceValues[i]);
        }

        // SIMPLEX
        for (int i = 5; i < LENGTH - 5; i++) {
            triangle[0][0] = i;
            triangle[0][1] = i;
            triangle[1][0] = i;
            triangle[1][1] = i + 1;
            triangle[2][0] = i + 1;
            triangle[2][1] = i;
            coordinates = simplex(triangle);
            x += coordinates[0];
            y += coordinates[1];
            count++;
        }

        x = x / (double) count;
        y = y / (double) count;

        coordinateView.setText(String.format("%.0f,%.0f", x, y));
    }

    private double convertRssiToDistance(double rssi) {
        return 4 * (Math.pow(10.0, -6.0)) * (Math.pow(rssi, 3.6718));
    }

    private double[] simplex(double[][] firstCase) {
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

            // if worst hasn't changed, exit loop
            if (worst == oldWorst) {
                break;
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

            oldWorst = worst;
        }

        return triangle[0];
    }

    // calcError function
    private double calcError(double[] trianglePoint) {
        return Math.abs(Math.sqrt(Math.pow(trianglePoint[0], 2.0) + Math.pow(trianglePoint[1], 2.0) - distanceValues[MASTER])) +
                Math.abs(Math.sqrt(Math.pow(WIDTH - trianglePoint[0], 2.0) + Math.pow(trianglePoint[1], 2.0) - distanceValues[SIDE_1])) +
                Math.abs(Math.sqrt(Math.pow(trianglePoint[0], 2.0) + Math.pow(LENGTH - trianglePoint[1], 2.0) - distanceValues[SIDE_2])) +
                Math.abs(Math.sqrt(Math.pow(WIDTH - trianglePoint[0], 2.0) + Math.pow(LENGTH - trianglePoint[1], 2.0) - distanceValues[SIDE_3]));
    }
}
