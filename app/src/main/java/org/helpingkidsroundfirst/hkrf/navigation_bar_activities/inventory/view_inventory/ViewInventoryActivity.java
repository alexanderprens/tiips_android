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
import org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory.categories.ViewCategoryDetailFragment;
import org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory.categories.ViewCategoryListFragment;
import org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory.category_intermediate.ViewIntermediateListFragment;
import org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory.current_inventory.ViewCurrentInventoryDetailFragment;
import org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory.current_inventory.ViewCurrentInventoryListFragment;
import org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory.inventory_items.ViewInventoryItemDetailFragment;
import org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory.inventory_items.ViewInventoryItemListFragment;
import org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory.past_inventory.ViewPastInventoryDetailFragment;
import org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory.past_inventory.ViewPastInventoryListFragment;

public class ViewInventoryActivity extends AppCompatActivity
    implements onViewInventoryButtonListener,
        ViewInventoryItemListFragment.Callback,
        ViewCurrentInventoryListFragment.Callback,
        ViewPastInventoryListFragment.Callback,
        ViewCategoryListFragment.Callback,
        ViewIntermediateListFragment.Callback {

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
        Bundle bundle;

        switch (button){
            case ViewInventoryFragment.BUTTON_ITEM:
                //create fragment
                fragment = new ViewIntermediateListFragment();

                //create bundle for fragment
                bundle = new Bundle();
                bundle.putInt(ViewIntermediateListFragment.EXPECTED_KEY,
                        ViewIntermediateListFragment.EXPECTED_INVENTORY_ITEM);
                fragment.setArguments(bundle);

                //replace fragment
                startFragment(fragment, "ViewIntermediateListFragment");
                getSupportActionBar().setTitle("Barcode Items");
                break;

            case ViewInventoryFragment.BUTTON_CURRENT:
                //create fragment
                fragment = new ViewIntermediateListFragment();

                //create bundle for fragment
                bundle = new Bundle();
                bundle.putInt(ViewIntermediateListFragment.EXPECTED_KEY,
                        ViewIntermediateListFragment.EXPECTED_CURRENT_INVENTORY);
                fragment.setArguments(bundle);

                //replace fragment
                startFragment(fragment, "ViewIntermediateListFragment");
                getSupportActionBar().setTitle("View Current Inventory");
                break;

            case ViewInventoryFragment.BUTTON_PAST:
                //create fragment
                fragment = new ViewIntermediateListFragment();

                //create bundle for fragment
                bundle = new Bundle();
                bundle.putInt(ViewIntermediateListFragment.EXPECTED_KEY,
                        ViewIntermediateListFragment.EXPECTED_PAST_INVENTORY);
                fragment.setArguments(bundle);

                //replace fragment
                startFragment(fragment, "ViewIntermediateListFragment");
                getSupportActionBar().setTitle("View Past Inventory");
                break;

            case ViewInventoryFragment.BUTTON_CATEGORIES:
                fragment = new ViewCategoryListFragment();
                startFragment(fragment, "ViewCategoryListFragment");
                getSupportActionBar().setTitle("View Categories");
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

    @Override
    public void onPastInventorySelected(Uri uri) {
        //create fragment
        Fragment fragment = new ViewPastInventoryDetailFragment();

        //create bundle for fragment
        Bundle bundle = new Bundle();
        bundle.putParcelable(ViewPastInventoryDetailFragment.DETAILED_PAST_KEY, uri);
        fragment.setArguments(bundle);

        //replace fragment
        startFragment(fragment, "ViewPastInventoryDetailFragment");
    }

    @Override
    public void onCategorySelected(Uri categoryUri) {
        //create fragment
        Fragment fragment = new ViewCategoryDetailFragment();

        //create bundle for fragment
        Bundle bundle = new Bundle();
        bundle.putParcelable(ViewCategoryDetailFragment.DETAILED_CATEGORY_KEY, categoryUri);
        fragment.setArguments(bundle);

        //replace fragment
        startFragment(fragment, "ViewCategoryDetailFragment");
    }

    private void startFragment(Fragment fragment, String fragmentTag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_view_inventory, fragment)
                .addToBackStack(fragmentTag)
                .commit();
    }

    @Override
    public void onIntermediateCategorySelected(Uri uri, int expected) {

        Fragment fragment;
        Bundle bundle = new Bundle();

        switch (expected) {
            case ViewIntermediateListFragment.EXPECTED_CURRENT_INVENTORY:
                //create fragment
                fragment = new ViewCurrentInventoryListFragment();

                //create bundle for fragment
                bundle.putParcelable(ViewCurrentInventoryListFragment.CURRENT_URI_KEY, uri);
                fragment.setArguments(bundle);

                //replace fragment
                startFragment(fragment, "ViewCurrentInventoryListFragment");
                break;

            case ViewIntermediateListFragment.EXPECTED_PAST_INVENTORY:
                //create fragment
                fragment = new ViewPastInventoryListFragment();

                //create bundle for fragment
                bundle.putParcelable(ViewPastInventoryListFragment.PAST_URI_KEY, uri);
                fragment.setArguments(bundle);

                //replace fragment
                startFragment(fragment, "ViewPastInventoryListFragment");
                break;

            case ViewIntermediateListFragment.EXPECTED_INVENTORY_ITEM:
                //create fragment
                fragment = new ViewInventoryItemListFragment();

                //create bundle for fragment
                bundle.putParcelable(ViewInventoryItemListFragment.ITEM_URI_KEY, uri);
                fragment.setArguments(bundle);

                //replace fragment
                startFragment(fragment, "ViewInventoryItemListFragment");
                break;

            default:
                //create fragment
                fragment = new ViewInventoryItemListFragment();

                //create bundle for fragment
                bundle.putParcelable(ViewInventoryItemListFragment.ITEM_URI_KEY, uri);
                fragment.setArguments(bundle);

                //replace fragment
                startFragment(fragment, "ViewInventoryItemListFragment");
                break;
        }
    }
}