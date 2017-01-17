package org.helpingkidsroundfirst.hkrf.nav_bar_fragments.inventory_fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.helpingkidsroundfirst.hkrf.R;
import org.helpingkidsroundfirst.hkrf.nav_bar_fragments.Adapters.ViewInventoryAdapter;


public class ViewInventoryFragment extends android.support.v4.app.Fragment
        implements AddItemDialogFragment.AddItemDialogListener {

    private ViewInventoryAdapter mViewInventoryAdapter;
    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;

    public ViewInventoryFragment() {
        // Required empty public constructor
    }

    // Column numbers of inventory items
    public static final int COL_ITEM_ID = 0;
    public static final int COL_QTY = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // make view
        View rootView = inflater.inflate(R.layout.fragment_view_inventory, container, false);

        // add floating action button
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                AddItemDialogFragment addItemDialogFragment = new AddItemDialogFragment();
                addItemDialogFragment.setTargetFragment(ViewInventoryFragment.this, 300);
                addItemDialogFragment.show(fragmentManager, "add_new_inventory_item");
            }
        });

        // use adapter to take data from database and populate listview
        mViewInventoryAdapter = new ViewInventoryAdapter(getActivity(), null, 0);

        // get ref to listview and attach adapter
        mListView = (ListView) rootView.findViewById(R.id.view_inventory_list);
        mListView.setAdapter(mViewInventoryAdapter);

        //listen for list click
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {

                }
                mPosition = position;
            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onFinishAddItemDialog(String inputText) {
        // TODO: 1/16/2017 do stuff
    }
}
