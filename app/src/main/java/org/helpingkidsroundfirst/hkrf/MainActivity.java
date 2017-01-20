package org.helpingkidsroundfirst.hkrf;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.helpingkidsroundfirst.hkrf.nav_bar_fragments.inventory_fragments.ChooseInventoryFragment;
import org.helpingkidsroundfirst.hkrf.nav_bar_fragments.inventory_fragments.ReceiveInventoryFragment;
import org.helpingkidsroundfirst.hkrf.nav_bar_fragments.inventory_fragments.ShipInventoryFragment;
import org.helpingkidsroundfirst.hkrf.nav_bar_fragments.inventory_fragments.ViewCurrentInventoryFragment;
import org.helpingkidsroundfirst.hkrf.nav_bar_fragments.inventory_fragments.ViewInventoryItemFragment;
import org.helpingkidsroundfirst.hkrf.nav_bar_fragments.inventory_fragments.ViewPastInventoryFragment;
import org.helpingkidsroundfirst.hkrf.nav_bar_fragments.inventory_fragments.ViewReportsFragment;
import org.helpingkidsroundfirst.hkrf.nav_bar_fragments.inventory_fragments.dummy.DummyContent;
import org.helpingkidsroundfirst.hkrf.nav_bar_fragments.ips_fragments.LocateItemFragment;
import org.helpingkidsroundfirst.hkrf.nav_bar_fragments.ips_fragments.ModifyTagsFragment;
import org.helpingkidsroundfirst.hkrf.nav_bar_fragments.ips_fragments.TagMessagesFragment;

// TODO: 12/22/2016 implement interfaces for fragments that need it
public class MainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener,
    ViewCurrentInventoryFragment.OnListFragmentInteractionListener,
        ViewInventoryItemFragment.OnListFragmentInteractionListener,
        ViewPastInventoryFragment.OnListFragmentInteractionListener{

    private static final String[] fragmentTitles = {"View Inventory", "Receive Inventory",
            "Ship Inventory", "View Reports", "Locate Items", "Modify Tags", "View Tag Messages"};

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
        Fragment fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Call fragments based on menu options
        if (id == R.id.nav_inventory) {
            fragment = new ChooseInventoryFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_main, fragment)
                    .commit();

            getSupportActionBar().setTitle(fragmentTitles[0]);
        } else if (id == R.id.nav_receive) {
            fragment = new ReceiveInventoryFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_main, fragment)
                    .commit();

            getSupportActionBar().setTitle(fragmentTitles[1]);
        } else if (id == R.id.nav_ship) {
            fragment = new ShipInventoryFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_main, fragment)
                    .commit();

            getSupportActionBar().setTitle(fragmentTitles[2]);
        } else if (id == R.id.nav_reports) {
            fragment = new ViewReportsFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_main, fragment)
                    .commit();

            getSupportActionBar().setTitle(fragmentTitles[3]);
        } else if (id == R.id.nav_locate) {
            fragment = new LocateItemFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_main, fragment)
                    .commit();

            getSupportActionBar().setTitle(fragmentTitles[4]);
        } else if (id == R.id.nav_add) {
            fragment = new ModifyTagsFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_main, fragment)
                    .commit();

            getSupportActionBar().setTitle(fragmentTitles[5]);
        } else if (id == R.id.nav_messages) {
            fragment = new TagMessagesFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_main, fragment)
                    .commit();

            getSupportActionBar().setTitle(fragmentTitles[6]);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }
}
