package org.helpingkidsroundfirst.hkrf;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.receive_inventory.ReceiveInventoryActivity;
import org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.ship_inventory.ShipInventoryActivity;
import org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory.ViewInventoryActivity;
import org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_reports.ViewReportsActivity;
import org.helpingkidsroundfirst.hkrf.navigation_bar_activities.ips.locate_item.LocateItemActivity;
import org.helpingkidsroundfirst.hkrf.navigation_bar_activities.ips.manage_tags.ManageTagsActivity;
import org.helpingkidsroundfirst.hkrf.navigation_bar_activities.ips.tag_messages.TagMessagesActivity;

// TODO: 2/3/2017 add context to layouts

public class MainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Fragment fragment = new HomeFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_main, fragment)
                .commit();

        // check permissions
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSION_REQUEST);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST + 1);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;

        switch (id){
            case R.id.nav_inventory:
                intent = new Intent(MainActivity.this, ViewInventoryActivity.class);
                startActivity(intent);
                //Log.i(TAG, "Start View Inventory Activity");
                break;

            case R.id.nav_ship:
                intent = new Intent(MainActivity.this, ShipInventoryActivity.class);
                startActivity(intent);
                //Log.i(TAG, "Start Ship Inventory Activity");
                break;

            case R.id.nav_receive:
                intent = new Intent(MainActivity.this, ReceiveInventoryActivity.class);
                startActivity(intent);
                //Log.i(TAG, "Start Receive Inventory Activity");
                break;

            case R.id.nav_reports:
                intent = new Intent(MainActivity.this, ViewReportsActivity.class);
                startActivity(intent);
                //Log.i(TAG, "Start View Reports Activity");
                break;

            case R.id.nav_locate:
                intent = new Intent(MainActivity.this, LocateItemActivity.class);
                startActivity(intent);
                //Log.i(TAG, "Start Locate Item Activity");
                break;

            case R.id.nav_manage:
                intent = new Intent(MainActivity.this, ManageTagsActivity.class);
                startActivity(intent);
                //Log.i(TAG, "Start Manage tags activity");
                break;

            case R.id.nav_messages:
                intent = new Intent(MainActivity.this, TagMessagesActivity.class);
                startActivity(intent);
                //Log.i(TAG, "Start Tag Messages Activity");
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay

                } else {

                    // permission denied, boo
                    Toast.makeText(this, getString(R.string.need_camera_permission), Toast.LENGTH_SHORT).show();
                }

                break;

            case PERMISSION_REQUEST + 1:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay

                } else {

                    // permission denied, boo
                    Toast.makeText(this, getString(R.string.need_camera_permission), Toast.LENGTH_SHORT).show();
                }

            default:
                // do nothing
                break;
        }
    }
}
