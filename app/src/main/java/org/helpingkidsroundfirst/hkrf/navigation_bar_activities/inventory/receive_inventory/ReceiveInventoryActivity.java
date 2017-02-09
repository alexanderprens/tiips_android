package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.receive_inventory;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.helpingkidsroundfirst.hkrf.R;
import org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.receive_inventory.add.ReceiveInventoryListFragment;

public class ReceiveInventoryActivity extends AppCompatActivity implements
        ReceiveInventoryFragment.onReceiveInventoryButtonListener,
        ReceiveInventoryListFragment.ReceiveInventoryListListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_inventory);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.receive_inventory_title));
        
        // start initial fragment
        Fragment fragment = new ReceiveInventoryFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_recieve_inventory, fragment)
                .commit();
    }

    @Override
    public void onReceiveInventoryButtonClicked(int button) {
        Fragment fragment;

        switch (button) {
            case ReceiveInventoryFragment.BUTTON_ADD_ITEMS:
                //create fragment
                fragment = new ReceiveInventoryListFragment();
                //replace fragment
                startFragment(fragment, "ViewIntermediateListFragment");
                break;

            case ReceiveInventoryFragment.BUTTON_SUBMIT_RECEPTION:

                break;
        }
    }

    private void startFragment(Fragment fragment, String fragmentTag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_recieve_inventory, fragment)
                .addToBackStack(fragmentTag)
                .commit();
    }

    @Override
    public void onItemSelected(Uri uri) {

    }
}
