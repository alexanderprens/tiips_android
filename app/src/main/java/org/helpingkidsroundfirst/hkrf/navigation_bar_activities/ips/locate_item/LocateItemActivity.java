package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.ips.locate_item;

import android.bluetooth.le.ScanResult;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import org.helpingkidsroundfirst.hkrf.R;
import org.helpingkidsroundfirst.hkrf.navigation_bar_activities.ips.locate_item.scan_devices.ScanBLEDevicesFragment;

public class LocateItemActivity extends AppCompatActivity implements
        LocateItemActivityFragment.LocateItemListener,
        ScanBLEDevicesFragment.ScanBLEListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locate_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        if (savedInstanceState == null) {
            Fragment fragment = new LocateItemActivityFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_locate_item, fragment)
                    .commit();
        }
    }

    @Override
    public void onButtonPressed(int button) {
        Fragment fragment;

        switch (button) {
            case LocateItemActivityFragment.BUTTON_CHOOSE:
                fragment = new ScanBLEDevicesFragment();
                startFragment(fragment, "ScanBLEDevicesFragment");
                break;
        }
    }

    private void startFragment(Fragment fragment, String fragmentTag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_locate_item, fragment)
                .addToBackStack(fragmentTag)
                .commit();
    }

    @Override
    public void BTNotEnabled() {
        getSupportFragmentManager().popBackStack();
        Toast.makeText(this, getString(R.string.ble_not_enabled), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResultSelected(ScanResult result) {
        // result detail fragment
    }
}
