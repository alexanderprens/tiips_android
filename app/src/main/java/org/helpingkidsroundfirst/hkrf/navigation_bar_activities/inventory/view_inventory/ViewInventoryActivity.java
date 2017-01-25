package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import org.helpingkidsroundfirst.hkrf.R;
import org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory.ViewInventoryFragment.onViewInventoryButtonListener;

public class ViewInventoryActivity extends AppCompatActivity
    implements onViewInventoryButtonListener{

    private static final String TAG = "ViewInventoryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_inventory);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.i(TAG, "ViewInventoryActivity created");


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("View Inventory");

        // start initial fragment
        Fragment fragment = new ViewInventoryFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_view_inventory, fragment)
                .commit();
    }

    // View inventory button listener
    public void onViewInventoryButtonClicked(int button){
        switch (button){
            case ViewInventoryFragment.BUTTON_ITEM:
                Fragment fragment = new ViewInventoryItemListFragment();
                startFragment(fragment, "ViewInventoryItemListFragment");
                getSupportActionBar().setTitle("Inventory Items");
                break;
        }
    }

    private void startFragment(Fragment fragment, String fragmentTag){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_view_inventory, fragment)
                .addToBackStack(fragmentTag)
                .commit();
    }
}
