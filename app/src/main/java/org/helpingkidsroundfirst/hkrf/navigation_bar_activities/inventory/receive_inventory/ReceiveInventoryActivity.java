package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.receive_inventory;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.helpingkidsroundfirst.hkrf.R;
import org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.receive_inventory.add.ReceiveInventoryDetailFragment;
import org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.receive_inventory.add.ReceiveInventoryListFragment;

public class ReceiveInventoryActivity extends AppCompatActivity implements
        ReceiveInventoryFragment.onReceiveInventoryButtonListener,
        ReceiveInventoryListFragment.ReceiveInventoryListListener,
        ReceiveInventorySubmitDialogFragment.SubmitReceiveListListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_inventory);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // setup action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle(getResources().getString(R.string.receive_inventory_title));

        if (savedInstanceState == null) {
            // start initial fragment if saved instance is null
            Fragment fragment = new ReceiveInventoryFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_recieve_inventory, fragment)
                    .commit();
        }
    }

    @Override
    public void onReceiveInventoryButtonClicked(int button) {
        Fragment fragment;

        switch (button) {
            case ReceiveInventoryFragment.BUTTON_ADD_ITEMS:
                //create fragment
                fragment = new ReceiveInventoryListFragment();
                //replace fragment
                startFragment(fragment, "ReceiveInventoryListFragment");
                break;

            case ReceiveInventoryFragment.BUTTON_SUBMIT_RECEPTION:
                // make dialog, start
                FragmentManager fragmentManager = getSupportFragmentManager();
                ReceiveInventorySubmitDialogFragment dialog = new ReceiveInventorySubmitDialogFragment();
                dialog.show(fragmentManager, "open submit receive dialog");
                break;
        }
    }

    @Override
    public void onItemSelected(Uri uri) {
        Fragment fragment;
        Bundle bundle = new Bundle();

        fragment = new ReceiveInventoryDetailFragment();
        bundle.putParcelable(ReceiveInventoryDetailFragment.DETAILED_RECEIVE_KEY, uri);
        fragment.setArguments(bundle);
        startFragment(fragment, "ReceiveInventoryDetailFragment");
    }

    @Override
    public void onSubmitButtonClicked() {

    }

    private void startFragment(Fragment fragment, String fragmentTag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_recieve_inventory, fragment)
                .addToBackStack(fragmentTag)
                .commit();
    }
}
