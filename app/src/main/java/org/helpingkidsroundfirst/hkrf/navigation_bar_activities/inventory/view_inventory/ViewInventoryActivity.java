package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import org.helpingkidsroundfirst.hkrf.R;

public class ViewInventoryActivity extends AppCompatActivity
    implements View.OnClickListener{

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
    }

    @Override
    public void onClick(View v) {

        Fragment fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (v.getId()){
            case R.id.choose_inv_butt_current:
                fragment = new ViewInventoryItemListFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_view_inventory, fragment)
                        .commit();
                break;

            // TODO: 1/25/2017 implement the other buttons
            case R.id.choose_inv_past_button:

                break;

            case R.id.choose_inv_item_button:

                break;
        }
    }
}
