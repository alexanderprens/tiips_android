package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import org.helpingkidsroundfirst.hkrf.data.InventoryContract;

/**
 * Created by Alex on 1/28/2017.
 */

public class FetchInventoryTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchInventoryTask.class.getSimpleName();

    private final Context mContext;

    public FetchInventoryTask(Context context) {
        mContext = context;
    }

    public long addInventoryItem(String name, String desc, String cat, String barcode, int value) {
        long itemId;

        ContentValues itemValues = new ContentValues();

        // make content values of inventory item data
        itemValues.put(InventoryContract.ItemEntry.COLUMN_NAME, name);
        itemValues.put(InventoryContract.ItemEntry.COLUMN_DESCRIPTION, desc);
        itemValues.put(InventoryContract.ItemEntry.COLUMN_CATEGORY, cat);
        itemValues.put(InventoryContract.ItemEntry.COLUMN_BARCODE_ID, barcode);
        itemValues.put(InventoryContract.ItemEntry.COLUMN_VALUE, value);

        // insert item into database
        Uri insertedUri = mContext.getContentResolver().insert(
                InventoryContract.ItemEntry.CONTENT_URI,
                itemValues
        );

        itemId = ContentUris.parseId(insertedUri);

        return itemId;
    }

    // checks if item already exists by looking at the barcode string
    public boolean checkIfItemExists(String barcode) {

        boolean exists;

        // Check if barcode id already exists in the db
        Cursor itemCursor = mContext.getContentResolver().query(
                InventoryContract.ItemEntry.CONTENT_URI,
                new String[]{InventoryContract.ItemEntry._ID},
                InventoryContract.ItemEntry.COLUMN_BARCODE_ID + " = ?",
                new String[]{barcode},
                null
        );

        // if barcode exists, return true
        // return -1 to inform user barcode already exists
        exists = itemCursor.moveToFirst();

        itemCursor.close();
        return exists;
    }


    @Override
    protected Void doInBackground(String... params) {
        return null;
    }
}
