package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.receive_inventory;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.helpingkidsroundfirst.hkrf.R;

/**
 * Created by alexa on 2/3/2017.
 */

public class ReceiveInventoryFragment extends Fragment {

    public static final int BUTTON_VIEW_RECEPTION = 1;
    public static final int BUTTON_ADD_ITEMS_MANUAL = 2;
    public static final int BUTTON_ADD_ITEMS_CAMERA = 3;

    public interface onReceiveInventoryButtonListener {
        void onReceiveInventoryButtonClicked(int button);
    }

    private onReceiveInventoryButtonListener mListener;

    public ReceiveInventoryFragment() {
        // required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_receive_inventory, container, false);

        try {
            mListener = (onReceiveInventoryButtonListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                + " must implement onReceiveInventoryButtonListener");
        }

        // Listen to button clicks


        return view;
    }
}
