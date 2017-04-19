package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.helpingkidsroundfirst.hkrf.R;
import org.helpingkidsroundfirst.hkrf.data.InventoryContract;

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

    private static void testCurrent(ContentResolver contentResolver) {
        // need: item key, qty, date received, donor, warehouse
        // 200k, 100k in each warehouse
        // create content values
        ContentValues currentValue1 = new ContentValues();
        ContentValues currentValue2;

        // add content
        currentValue1.put(InventoryContract.CurrentInventoryEntry.COLUMN_ITEM_KEY, 1);
        currentValue1.put(InventoryContract.CurrentInventoryEntry.COLUMN_QTY, 1);
        currentValue1.put(InventoryContract.CurrentInventoryEntry.COLUMN_DATE_RECEIVED, "2017-04-18");
        currentValue1.put(InventoryContract.CurrentInventoryEntry.COLUMN_DONOR, "SDSU");
        currentValue2 = currentValue1;
        currentValue1.put(InventoryContract.CurrentInventoryEntry.COLUMN_WAREHOUSE, "Brookings");
        currentValue2.put(InventoryContract.CurrentInventoryEntry.COLUMN_WAREHOUSE, "Flandreau");

        // loop to add content values to list
        for (int i = 0; i < 100000; i++) {
            contentResolver.insert(
                    InventoryContract.CurrentInventoryEntry.buildCurrentInventoryUri(),
                    currentValue1
            );

            contentResolver.insert(
                    InventoryContract.CurrentInventoryEntry.buildCurrentInventoryUri(),
                    currentValue2
            );
        }
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

        view.findViewById(R.id.choose_inv_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        testDatabase(getContext());
                        //Toast.makeText(getContext(), "Test Complete", Toast.LENGTH_SHORT).show();
                    }
                }).start();
            }
        });

        return view;
    }

    // test database table capacities
    public void testDatabase(Context context) {

        ContentResolver contentResolver = context.getContentResolver();

        // current
        testCurrent(contentResolver);

        // past
        testPast(contentResolver);

        // receive
        testReceive(contentResolver);

        // ship
        testShip(contentResolver);
    }

    private void testShip(ContentResolver contentResolver) {
        // need: barcode id, name, value
        // 2k
        // create content values
        ContentValues shipValue = new ContentValues();

        // add content
        shipValue.put(InventoryContract.ShipInventoryEntry.COLUMN_BARCODE_ID, "HKRF-BB-0001");
        shipValue.put(InventoryContract.ShipInventoryEntry.COLUMN_NAME, "Baseball");
        shipValue.put(InventoryContract.ShipInventoryEntry.COLUMN_VALUE, 1);
        shipValue.put(InventoryContract.ShipInventoryEntry.COLUMN_CATEGORY_KEY, 1);

        // loop to add content values to list
        for (int i = 0; i < 2000; i++) {
            contentResolver.insert(
                    InventoryContract.ShipInventoryEntry.buildShipInventoryUri(),
                    shipValue
            );
        }
    }

    private void testReceive(ContentResolver contentResolver) {
        // need: item key
        // 2k
        // create content values
        ContentValues receiveValue = new ContentValues();

        // add content
        receiveValue.put(InventoryContract.ReceiveInventoryEntry.COLUMN_ITEM_KEY, 1);

        // loop to add content values to list
        for (int i = 0; i < 2000; i++) {
            contentResolver.insert(
                    InventoryContract.ReceiveInventoryEntry.buildReceiveInventoryUri(),
                    receiveValue
            );
        }
    }

    private void testPast(ContentResolver contentResolver) {
        // past
        // barcode id, name, value, qty, date, donor
        // 200k
        // create content values
        ContentValues pastValue = new ContentValues();

        // add content
        pastValue.put(InventoryContract.PastInventoryEntry.COLUMN_BARCODE_ID, "HKRF-BB-0001");
        pastValue.put(InventoryContract.PastInventoryEntry.COLUMN_NAME, "Baseball");
        pastValue.put(InventoryContract.PastInventoryEntry.COLUMN_VALUE, 1);
        pastValue.put(InventoryContract.PastInventoryEntry.COLUMN_QTY, 1);
        pastValue.put(InventoryContract.PastInventoryEntry.COLUMN_DATE_SHIPPED, "2017-04-18");
        pastValue.put(InventoryContract.PastInventoryEntry.COLUMN_DONOR, "SDSU");
        pastValue.put(InventoryContract.PastInventoryEntry.COLUMN_CATEGORY_KEY, 1);

        // loop to add content values to list
        for (int i = 0; i < 200000; i++) {
            contentResolver.insert(
                    InventoryContract.PastInventoryEntry.buildPastInventoryUri(),
                    pastValue
            );
        }
    }

    public interface onViewInventoryButtonListener {
        void onViewInventoryButtonClicked(int button);
    }
}
