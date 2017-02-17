package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.ship_inventory;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.helpingkidsroundfirst.hkrf.R;
import org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.ship_inventory.add.ShipInventoryDetailFragment;
import org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.ship_inventory.add.ShipInventoryListFragment;

public class ShipInventoryActivity extends AppCompatActivity implements
        ShipInventoryFragment.onShipInventoryButtonListener,
        ShipInventoryListFragment.ShipInventoryListListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ship_inventory);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // setup action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle(getResources().getString(R.string.ship_inventory_title));

        // start initial fragment
        Fragment fragment = new ShipInventoryFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_ship_inventory, fragment)
                .commit();
    }

    @Override
    public void onShipInventoryButtonClicked(int button) {
        Fragment fragment;
        Bundle bundle = new Bundle();

        switch (button) {
            case ShipInventoryFragment.BUTTON_ADD:
                //create fragment
                fragment = new ShipInventoryListFragment();
                //replace fragment
                startFragment(fragment, "ShipInventoryListFragment");
                break;

            case ShipInventoryFragment.BUTTON_SUBMIT:

                break;
        }
    }

    private void startFragment(Fragment fragment, String fragmentTag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_ship_inventory, fragment)
                .addToBackStack(fragmentTag)
                .commit();
    }

    @Override
    public void onItemSelected(Uri uri) {
        Fragment fragment;
        Bundle bundle = new Bundle();

        // start detail fragment
        fragment = new ShipInventoryDetailFragment();
        bundle.putParcelable(ShipInventoryDetailFragment.DETAILED_SHIP_KEY, uri);
        fragment.setArguments(bundle);
        startFragment(fragment, "ShipInventoryDetailFragment");
    }
}
