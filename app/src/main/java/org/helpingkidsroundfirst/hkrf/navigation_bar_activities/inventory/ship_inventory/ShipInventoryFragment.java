package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.ship_inventory;


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
public class ShipInventoryFragment extends Fragment {

    public static final int BUTTON_ADD = 1;
    public static final int BUTTON_SUBMIT = 2;
    private onShipInventoryButtonListener mListener;


    public ShipInventoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ship_inventory, container, false);

        // get listener
        try {
            mListener = (onShipInventoryButtonListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement onShipInventoryButtonListener");
        }

        // Listen to button clicks
        Button addButton = (Button) view.findViewById(R.id.ship_inventory_button_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onShipInventoryButtonClicked(BUTTON_ADD);
            }
        });

        Button submitButton = (Button) view.findViewById(R.id.ship_inventory_button_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onShipInventoryButtonClicked(BUTTON_SUBMIT);
            }
        });

        return view;
    }

    public interface onShipInventoryButtonListener {
        void onShipInventoryButtonClicked(int button);
    }

}
