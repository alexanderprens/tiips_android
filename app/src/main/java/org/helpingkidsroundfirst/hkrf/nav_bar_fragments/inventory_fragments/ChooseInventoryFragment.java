package org.helpingkidsroundfirst.hkrf.nav_bar_fragments.inventory_fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.helpingkidsroundfirst.hkrf.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChooseInventoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChooseInventoryFragment extends android.support.v4.app.Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //fragment tag strings
    private final String inventoryItem = "InventoryItems";

    //set click listeners on buttons
    public ChooseInventoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChooseInventoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChooseInventoryFragment newInstance(String param1, String param2) {
        ChooseInventoryFragment fragment = new ChooseInventoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_choose_inventory, null);

        //add click listeners
        Button invButton = (Button) view.findViewById(R.id.choose_inv_item_button);
        invButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v4.app.Fragment newFragment = ViewInventoryItemFragment.newInstance(1);
                replaceFragment(newFragment, "ViewInventoryItems");
            }
        });

        Button currInvButton = (Button) view.findViewById(R.id.choose_inv_butt_current);
        currInvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v4.app.Fragment newFragment = ViewCurrentInventoryFragment.newInstance(1);
                replaceFragment(newFragment, "ViewCurrentInventory");
            }
        });

        Button pastInvButton = (Button) view.findViewById(R.id.choose_inv_past_button);
        pastInvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v4.app.Fragment newFragment = ViewPastInventoryFragment.newInstance(1);
                replaceFragment(newFragment, "ViewPastInventory");
            }
        });

        return view;
    }

    public void replaceFragment(android.support.v4.app.Fragment newFragment, String tag) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.choose_inv_layout, newFragment, tag);
        ft.addToBackStack(null);
        ft.commit();
    }
}
