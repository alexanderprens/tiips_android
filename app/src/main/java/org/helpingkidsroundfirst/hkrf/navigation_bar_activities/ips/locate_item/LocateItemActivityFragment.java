package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.ips.locate_item;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.helpingkidsroundfirst.hkrf.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LocateItemActivityFragment extends Fragment {

    public static final int BUTTON_CHOOSE = 1;
    private LocateItemListener mListener;


    public LocateItemActivityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        try {
            mListener = (LocateItemListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement LocateItemListener");
        }

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_locate_item_activity, container, false);

        final Button chooseButton = (Button) rootView.findViewById(R.id.locate_button_choose);
        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return rootView;
    }

    public interface LocateItemListener {
        void onButtonPressed(int button);
    }
}
