package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.ship_inventory;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.helpingkidsroundfirst.hkrf.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShipInventoryFragment extends Fragment {


    public ShipInventoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ship_inventory, container, false);

        return view;
    }

}
