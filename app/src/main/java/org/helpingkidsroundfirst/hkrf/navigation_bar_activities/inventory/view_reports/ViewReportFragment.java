package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_reports;

import android.content.Context;
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
public class ViewReportFragment extends Fragment {

    private OnReportButtonPressed mListener;

    public ViewReportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_view_report, container, false);

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

    public interface OnReportButtonPressed {
        void onReportButtonPressed(int button);
    }
}
