package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.ips.manage_tags;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

import org.helpingkidsroundfirst.hkrf.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManageTagsActivityFragment extends Fragment implements
        View.OnClickListener {

    private static final int NUM_TAGS = 8;
    private Switch[] switches;
    private Button[] buttons;


    public ManageTagsActivityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_manage_tags_activity, container, false);

        switches = new Switch[NUM_TAGS];
        buttons = new Button[NUM_TAGS];


        return rootView;
    }

    @Override
    public void onClick(View v) {

    }
}
