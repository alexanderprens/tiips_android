package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.ship_inventory.add;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

/**
 * Created by Alex on 2/14/2017.
 */

public class ShipInventoryListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
