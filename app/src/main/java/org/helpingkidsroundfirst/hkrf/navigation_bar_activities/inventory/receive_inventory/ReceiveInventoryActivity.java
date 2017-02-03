package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.receive_inventory;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.helpingkidsroundfirst.hkrf.R;

public class ReceiveInventoryActivity extends AppCompatActivity implements 
    ReceiveInventoryFragment.onReceiveInventoryButtonListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_inventory);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Receive Inventory");
        
        // start initial fragment
        Fragment fragment = new ReceiveInventoryFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_view_inventory, fragment)
                .commit();
    }

    @Override
    public void onReceiveInventoryButtonClicked(int button) {
        // TODO: 2/3/2017 implement button actions
    }
}
