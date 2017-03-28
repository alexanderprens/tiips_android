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

import java.util.Arrays;
import java.util.Locale;

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
    private static final double TRIANGLE_SIZE = 7.0;
    private static final double MIN_AREA = 0.01;
    private static final double SHRINK = 0.7;
    private static final double EXPAND = 1.3;
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
        String text = "";
        double triangleStartY, triangleStartX;

        // get inputs
        distanceValues[MASTER] = (double) Long.parseLong(masterIn, 16);
        distanceValues[SIDE_1] = (double) Long.parseLong(side1In, 16);
        distanceValues[SIDE_2] = (double) Long.parseLong(side2In, 16);
        distanceValues[SIDE_3] = (double) Long.parseLong(side3In, 16);

        // convert rssi to distance
        for (int i = 0; i < 4; i++) {
            distanceValues[i] = convertRssiToDistance(distanceValues[i]);
        }

        // x values
        triangleStartX = 15;
        triangle[0][0] = triangleStartX;
        triangle[1][0] = triangleStartX + TRIANGLE_SIZE;
        triangle[2][0] = triangleStartX;

        // y values
        triangleStartY = 15;
        triangle[0][1] = triangleStartY;
        triangle[1][1] = triangleStartY;
        triangle[2][1] = triangleStartY + TRIANGLE_SIZE;

        // nm simplex
        nmSimplex();
        coordinates = triangle[1];

        text += String.format(Locale.US, "\n%.1f,%.1f", coordinates[0], coordinates[1]);
        coordinateView.setText(text);
    }

    private double convertRssiToDistance(double rssi) {
        return 4.0 * (Math.pow(10.0, -6.0)) * (Math.pow(rssi, 3.6718));
    }



    // calcError function
    private double calcError(double[] trianglePoint) {
        return Math.abs(Math.sqrt(Math.pow(trianglePoint[0], 2.0) + Math.pow(trianglePoint[1], 2.0)) - distanceValues[MASTER]) +
                Math.abs(Math.sqrt(Math.pow(WIDTH - trianglePoint[0], 2.0) + Math.pow(trianglePoint[1], 2.0)) - distanceValues[SIDE_1]) +
                Math.abs(Math.sqrt(Math.pow(trianglePoint[0], 2.0) + Math.pow(LENGTH - trianglePoint[1], 2.0)) - distanceValues[SIDE_2]) +
                Math.abs(Math.sqrt(Math.pow(WIDTH - trianglePoint[0], 2.0) + Math.pow(LENGTH - trianglePoint[1], 2.0)) - distanceValues[SIDE_3]);
    }

    private void nmSimplex() {

        double[] centroid;
        double[] reflected = new double[2];
        double[] sortedError;
        double[] expansion = new double[2];
        double[] contraction = new double[2];
        double reflectedError, expandedError, contractError;
        int worst;

        while (true) {

            // get errors
            error[0] = calcError(triangle[0]);
            error[1] = calcError(triangle[1]);
            error[2] = calcError(triangle[2]);
            sortedError = error;
            Arrays.sort(sortedError);   // sorts into ascending order

            // compute centroid
            worst = getMostError();
            centroid = calcCentroid(worst);

            // compute reflection
            reflected[0] = centroid[0] + 1 * (centroid[0] - triangle[worst][0]);
            reflected[1] = centroid[1] + 1 * (centroid[1] - triangle[worst][1]);
            reflectedError = calcError(reflected);

            // check reflected error results
            if (reflectedError < sortedError[1] && reflectedError >= sortedError[0]) {
                triangle[worst] = reflected;

            } else if (reflectedError < sortedError[0]) {
                expansion[0] = centroid[0] + EXPAND * (reflected[0] - centroid[0]);
                expansion[1] = centroid[1] + EXPAND * (reflected[1] - centroid[1]);

                expandedError = calcError(expansion);
                if (expandedError < reflectedError) {
                    triangle[worst] = expansion;

                } else {
                    triangle[worst] = reflected;
                }

            } else if (reflectedError > sortedError[1]) {

                if (reflectedError < sortedError[2]) {
                    // outside calc contraction
                    contraction[0] = centroid[0] + SHRINK * (reflected[0] - centroid[0]);
                    contraction[1] = centroid[1] + SHRINK * (reflected[1] - centroid[1]);
                    contractError = calcError(contraction);

                    if (contractError < reflectedError) {
                        triangle[worst] = contraction;

                    } else {
                        shrink();
                    }
                } else {
                    // inside calc contraction
                    contraction[0] = centroid[0] - SHRINK * (centroid[0] - triangle[worst][0]);
                    contraction[1] = centroid[1] - SHRINK * (centroid[1] - triangle[worst][1]);
                    contractError = calcError(contraction);

                    if (contractError < sortedError[2]) {
                        triangle[worst] = contraction;

                    } else {
                        shrink();
                    }
                }
            } else {
                shrink();
            }

            // check area
            if (calcArea() < MIN_AREA) {
                break;
            }
        }
    }

    private void shrink() {
        int best;

        best = getLeastError();

        for (int i = 0; i < 3; i++) {
            if (i != best) {
                triangle[i][0] = triangle[i][0] + SHRINK * (triangle[i][0] - triangle[best][0]);
                triangle[i][1] = triangle[i][1] + SHRINK * (triangle[i][1] - triangle[best][1]);
            }
        }
    }

    private double[] calcCentroid(int worst) {
        double[] centroid = new double[2];

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

        return centroid;
    }

    private int getMostError() {
        int worst;

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

        return worst;
    }

    private int getLeastError() {
        int best;

        // find worst error
        if (error[0] < error[1]) {
            if (error[0] < error[2]) {
                best = 0;
            } else {
                best = 2;
            }
        } else {
            if (error[2] < error[1]) {
                best = 2;
            } else {
                best = 1;
            }
        }

        return best;
    }

    private double calcArea() {
        return 0.5 * Math.abs((triangle[0][0] - triangle[2][0]) * (triangle[1][1] - triangle[0][1]) -
                (triangle[0][0] - triangle[1][0]) * (triangle[2][1] - triangle[0][1]));
    }

    /*
    private double calcAvg(ArrayList<Double> values){
        double sum = 0.0;

        if(!values.isEmpty()){
            for(Double value : values){
                sum += value;
            }

            return sum / (double) values.size();
        }

        return sum;
    }

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

