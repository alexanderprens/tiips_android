package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_reports;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.helpingkidsroundfirst.hkrf.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnReportButtonPressed} interface
 * to handle interaction events.
 */
public class ViewReportFragment extends Fragment implements
        View.OnClickListener {

    public static final int BUTTON_ITEM = 0;
    public static final int BUTTON_CURRENT_SUMMARY = 1;
    public static final int BUTTON_PAST_SUMMARY = 2;
    public static final int BUTTON_SHEET_EXPORT = 3;
    public static final int BUTTON_DB_EXPORT = 4;
    public static final int BUTTON_SHIPMENT_SUMMARY = 5;
    public static final int BUTTON_DB_IMPORT = 6;
    private OnReportButtonPressed mListener;
    private DialogInterface.OnClickListener dialogClickListener;

    public ViewReportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_view_report, container, false);

        rootView.findViewById(R.id.view_report_button_barcode).setOnClickListener(this);

        rootView.findViewById(R.id.view_report_button_current).setOnClickListener(this);

        rootView.findViewById(R.id.view_report_button_past).setOnClickListener(this);

        rootView.findViewById(R.id.view_report_button_sheets).setOnClickListener(this);

        rootView.findViewById(R.id.view_report_button_export).setOnClickListener(this);

        rootView.findViewById(R.id.view_report_button_shipments).setOnClickListener(this);

        rootView.findViewById(R.id.view_report_button_import).setOnClickListener(this);

        // implement delete button
        dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        mListener.onReportButtonPressed(BUTTON_DB_IMPORT);
                        break;
                }
            }
        };

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnReportButtonPressed) {
            mListener = (OnReportButtonPressed) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnReportButtonPressed");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.view_report_button_barcode:
                mListener.onReportButtonPressed(BUTTON_ITEM);
                break;

            case R.id.view_report_button_current:
                mListener.onReportButtonPressed(BUTTON_CURRENT_SUMMARY);
                break;

            case R.id.view_report_button_past:
                mListener.onReportButtonPressed(BUTTON_PAST_SUMMARY);
                break;

            case R.id.view_report_button_sheets:
                mListener.onReportButtonPressed(BUTTON_SHEET_EXPORT);
                break;

            case R.id.view_report_button_export:
                mListener.onReportButtonPressed(BUTTON_DB_EXPORT);
                break;

            case R.id.view_report_button_shipments:
                mListener.onReportButtonPressed(BUTTON_SHIPMENT_SUMMARY);
                break;

            case R.id.view_report_button_import:
                // call dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage(R.string.view_reports_sure)
                        .setPositiveButton(R.string.are_you_sure_yes, dialogClickListener)
                        .setNegativeButton(R.string.are_you_sure_no, dialogClickListener)
                        .show();
                break;
        }
    }

    interface OnReportButtonPressed {
        void onReportButtonPressed(int button);
    }
}
