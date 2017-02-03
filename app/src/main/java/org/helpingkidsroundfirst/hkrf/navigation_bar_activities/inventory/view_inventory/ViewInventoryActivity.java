package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import org.helpingkidsroundfirst.hkrf.R;
import org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory.ViewInventoryFragment.onViewInventoryButtonListener;
import org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory.current_inventory.ViewCurrentInventoryDetailFragment;
import org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory.current_inventory.ViewCurrentInventoryListFragment;
import org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory.inventory_items.ViewInventoryItemDetailFragment;
import org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory.inventory_items.ViewInventoryItemListFragment;
import org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory.past_inventory.ViewPastInventoryListFragment;

public class ViewInventoryActivity extends AppCompatActivity
    implements onViewInventoryButtonListener,
        ViewInventoryItemListFragment.Callback,
        ViewCurrentInventoryListFragment.Callback {

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
        Fragment fragment;

        switch (button){
            case ViewInventoryFragment.BUTTON_ITEM:
                fragment = new ViewInventoryItemListFragment();
                startFragment(fragment, "ViewInventoryItemListFragment");
                getSupportActionBar().setTitle("Inventory Items");
                break;

            case ViewInventoryFragment.BUTTON_CURRENT:
                fragment = new ViewCurrentInventoryListFragment();
                startFragment(fragment, "ViewCurrentInventoryListFragment");
                getSupportActionBar().setTitle("View Current Inventory");
                break;

            case ViewInventoryFragment.BUTTON_PAST:
                fragment = new ViewPastInventoryListFragment();
                startFragment(fragment, "ViewPastInventoryListFragment");
                getSupportActionBar().setTitle("View Past Inventory");
                break;
        }
    }

    @Override
    public void onItemSelected(Uri invItemURI) {
        //create fragment
        Fragment fragment = new ViewInventoryItemDetailFragment();

        //create bundle for fragment
        Bundle bundle = new Bundle();
        bundle.putParcelable(ViewInventoryItemDetailFragment.DETAILED_ITEM_KEY, invItemURI);
        fragment.setArguments(bundle);

        //replace fragment
        startFragment(fragment, "ViewInventoryItemDetailFragment");
    }

    @Override
    public void onCurrentInventorySelected(Uri uri) {
        //create fragment
        Fragment fragment = new ViewCurrentInventoryDetailFragment();

        //create bundle for fragment
        Bundle bundle = new Bundle();
        bundle.putParcelable(ViewCurrentInventoryDetailFragment.DETAILED_CURRENT_KEY, uri);
        fragment.setArguments(bundle);

        //replace fragment
        startFragment(fragment, "ViewCurrentInventoryDetailFragment");
    }

    private void startFragment(Fragment fragment, String fragmentTag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_view_inventory, fragment)
                .addToBackStack(fragmentTag)
                .commit();
    }
}
