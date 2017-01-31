package org.helpingkidsroundfirst.hkrf.data;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import static org.helpingkidsroundfirst.hkrf.data.TestUtilities.createItemValues;
import static org.helpingkidsroundfirst.hkrf.data.TestUtilities.validateCursor;

/**
 * Created by Alex on 1/28/2017.
 */

public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    // test provider delete functionality
    public void deleteAllRecordsFromProvider() {

        mContext.getContentResolver().delete(
                InventoryContract.ItemEntry.CONTENT_URI,
                null,
                null
        );

        mContext.getContentResolver().delete(
                InventoryContract.CurrentInventoryEntry.CONTENT_URI,
                null,
                null
        );

        mContext.getContentResolver().delete(
                InventoryContract.PastInventoryEntry.CONTENT_URI,
                null,
                null
        );

        // check item deletions
        Cursor cursor = mContext.getContentResolver().query(
                InventoryContract.ItemEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertEquals("Error: records not deleted from item table during delete",
                0, cursor.getCount());
        cursor.close();

        //check current inv deletions
        cursor = mContext.getContentResolver().query(
                InventoryContract.CurrentInventoryEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertEquals("Error: records not deleted from current inventory table during delete",
                0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                InventoryContract.PastInventoryEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertEquals("Error: records not deleted from past inventory table during delete",
                0, cursor.getCount());
        cursor.close();
    }

    // function call to delete all records
    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }

    // clean database on startup
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    // check to make sure content provider is registered correctly
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                InventoryProvider.class.getName());

        try {

            // fetch provider info using package manager
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            assertEquals("Error: provider registered with authority " + providerInfo.authority +
                    " instead of authority: " + InventoryContract.CONTENT_AUTHORITY,
                    providerInfo.authority, InventoryContract.CONTENT_AUTHORITY
            );
        } catch (PackageManager.NameNotFoundException e) {
            // provider not registered correctly
            assertTrue("Error: inventory provider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    public void testGetType() {
        // content://org.helpingkidsroundfirst.hkrf/items/
        String type = mContext.getContentResolver().getType(InventoryContract.ItemEntry.CONTENT_URI);
        assertEquals("Error: the ItemEntry CONTENT_URI should return ItemEntry.CONTENT_TYPE",
                InventoryContract.ItemEntry.CONTENT_TYPE, type);

        // content://org.helpingkidsroundfirst.hkrf/past_inventory/
        type = mContext.getContentResolver().getType(InventoryContract.PastInventoryEntry.CONTENT_URI);
        assertEquals("Error: the PastInvEntry CONTENT_URI should return ItemEntry.CONTENT_TYPE",
                InventoryContract.PastInventoryEntry.CONTENT_TYPE, type);

        // content://org.helpingkidsroundfirst.hkrf/current_inventory/
        type = mContext.getContentResolver().getType(InventoryContract.CurrentInventoryEntry.CONTENT_URI);
        assertEquals("Error: the PastInvEntry CONTENT_URI should return ItemEntry.CONTENT_TYPE",
                InventoryContract.CurrentInventoryEntry.CONTENT_TYPE, type);
    }

    public void testBasicItemQuery() {
        // insert test records into database
        InventoryDbHelper dbHelper = new InventoryDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long itemRowId;
        ContentValues itemValues =  createItemValues();

        itemRowId = db.insert(InventoryContract.ItemEntry.TABLE_NAME, null, itemValues);

        //test if inserted
        assertTrue("Unable to insert ItemEntry into database", itemRowId != -1);
        db.close();

        //test content provider query
        Cursor itemCursor = mContext.getContentResolver().query(
                InventoryContract.ItemEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        validateCursor("testBasicItemQuery", itemCursor, itemValues);
    }
}
