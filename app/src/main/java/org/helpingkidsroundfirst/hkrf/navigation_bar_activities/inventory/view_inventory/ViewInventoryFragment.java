package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory;


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
public class ViewInventoryFragment extends Fragment {

    public static final int BUTTON_ITEM = 1;
    public static final int BUTTON_CURRENT = 2;
    public static final int BUTTON_PAST = 3;
    public static final int BUTTON_CATEGORIES = 4;

    private Button inventoryItemButton;
    private Button currentInventoryButton;
    private Button pastInventoryButton;
    private Button categoryButton;
    private onViewInventoryButtonListener mListener;

    public ViewInventoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_inventory, container, false);

        try {
            mListener = (onViewInventoryButtonListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement onViewInventoryButtonListener");
        }

        // Listen to button clicks
        inventoryItemButton = (Button) view.findViewById(R.id.choose_inv_item_button);
        inventoryItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                mListener.onViewInventoryButtonClicked(BUTTON_ITEM);
            }
        });

        currentInventoryButton = (Button) view.findViewById(R.id.choose_inv_butt_current);
        currentInventoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onViewInventoryButtonClicked(BUTTON_CURRENT);
            }
        });

        pastInventoryButton = (Button) view.findViewById(R.id.choose_inv_past_button);
        pastInventoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onViewInventoryButtonClicked(BUTTON_PAST);
            }
        });

        categoryButton = (Button) view.findViewById(R.id.choose_inv_category_button);
        categoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onViewInventoryButtonClicked(BUTTON_CATEGORIES);
            }
        });

        return view;
    }

    public interface onViewInventoryButtonListener {
        void onViewInventoryButtonClicked(int button);
    }
}
