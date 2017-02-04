package org.helpingkidsroundfirst.hkrf.data;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import static org.helpingkidsroundfirst.hkrf.data.TestUtilities.createCategoryValues;
import static org.helpingkidsroundfirst.hkrf.data.TestUtilities.createItemValues;
import static org.helpingkidsroundfirst.hkrf.data.TestUtilities.insertTestCategory;
import static org.helpingkidsroundfirst.hkrf.data.TestUtilities.insertTestItem;
import static org.helpingkidsroundfirst.hkrf.data.TestUtilities.validateCursor;

/**
 * Created by Alex on 1/28/2017.
 */

public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    // test provider delete functionality
    public void deleteAllRecordsFromProvider() {

        mContext.getContentResolver().delete(
                InventoryContract.CategoryEntry.CONTENT_URI,
                null,
                null
        );

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

        // check category deletions
        Cursor cursor = mContext.getContentResolver().query(
                InventoryContract.CategoryEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertEquals("Error: records not deleted from category table during delete",
                0, cursor.getCount());
        cursor.close();

        // check item deletions
        cursor = mContext.getContentResolver().query(
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
        String type = mContext.getContentResolver().getType(InventoryContract.CategoryEntry.CONTENT_URI);
        assertEquals("Error: the CategoryEntry CONTENT_URI should return CategoryEntry.CONTENT_TYPE",
                InventoryContract.CategoryEntry.CONTENT_TYPE, type);

        // content://org.helpingkidsroundfirst.hkrf/items/
        type = mContext.getContentResolver().getType(InventoryContract.ItemEntry.CONTENT_URI);
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

    public void testBasicCategoryQuery() {
        //insert test records into database
        InventoryDbHelper dbHelper = new InventoryDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long catRowId;
        ContentValues categoryValues = createCategoryValues();

        catRowId = db.insert(InventoryContract.CategoryEntry.TABLE_NAME, null, categoryValues);

        //test if inserted
        assertTrue("Unable to insert CategoryEntry into database", catRowId != -1);
        db.close();

        //test content provider query
        Cursor itemCursor = mContext.getContentResolver().query(
                InventoryContract.CategoryEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        validateCursor("testBasicCategoryQuery", itemCursor, categoryValues);
    }

    public void testBasicItemQuery() {
        // insert test records into database
        InventoryDbHelper dbHelper = new InventoryDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long catRowId = insertTestCategory(db);

        long itemRowId;
        ContentValues itemValues =  createItemValues(catRowId);

        itemRowId = db.insert(InventoryContract.ItemEntry.TABLE_NAME, null, itemValues);

        itemValues.put(InventoryContract.CategoryEntry._ID, catRowId);
        itemValues.put(InventoryContract.CategoryEntry.COLUMN_NAME, "Baseball");
        itemValues.put(InventoryContract.CategoryEntry.COLUMN_BARCODE_PREFIX, "BB");

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

    public void testBasicCurrentInventoryQuery() {
        InventoryDbHelper dbHelper = new InventoryDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // insert item into database
        long itemRowId;
        itemRowId = insertTestItem(db);

        //test if inserted
        assertTrue("Unable to insert ItemEntry into database", itemRowId != -1);

        // create current inventory values
        ContentValues currentInventoryValues = new ContentValues();
        currentInventoryValues.put(InventoryContract.CurrentInventoryEntry.COLUMN_ITEM_KEY, itemRowId);
        currentInventoryValues.put(InventoryContract.CurrentInventoryEntry.COLUMN_DATE_RECEIVED, "170126");
        currentInventoryValues.put(InventoryContract.CurrentInventoryEntry.COLUMN_DONOR, "Brookings Health");
        currentInventoryValues.put(InventoryContract.CurrentInventoryEntry.COLUMN_QTY, 10);
        currentInventoryValues.put(InventoryContract.CurrentInventoryEntry.COLUMN_WAREHOUSE, "One");

        long currentInvRowId;
        currentInvRowId = db.insert(InventoryContract.CurrentInventoryEntry.TABLE_NAME, null,
                currentInventoryValues);

        assertTrue("Unable to insert CurrentInventoryEntry into database", currentInvRowId != -1);
        db.close();

        Cursor currentInventoryCursor = mContext.getContentResolver().query(
                InventoryContract.CurrentInventoryEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        validateCursor("testBasicCurrentInventoryQuery", currentInventoryCursor,
                currentInventoryValues);
    }

    public void testBasicPastInventoryQuery() {
        InventoryDbHelper dbHelper = new InventoryDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // insert item into database
        long itemRowId;
        itemRowId = insertTestItem(db);

        //test if inserted
        assertTrue("Unable to insert ItemEntry into database", itemRowId != -1);

        // create current inventory values
        ContentValues pastInventoryValues = new ContentValues();
        pastInventoryValues.put(InventoryContract.PastInventoryEntry.COLUMN_ITEM_KEY,itemRowId);
        pastInventoryValues.put(InventoryContract.PastInventoryEntry.COLUMN_DATE_SHIPPED, "170126");
        pastInventoryValues.put(InventoryContract.PastInventoryEntry.COLUMN_DONOR, "Brookings Health");
        pastInventoryValues.put(InventoryContract.PastInventoryEntry.COLUMN_QTY, 10);

        long pastInventoryId;
        pastInventoryId = db.insert(InventoryContract.PastInventoryEntry.TABLE_NAME, null,
                pastInventoryValues);

        assertTrue("Unable to insert PastInventoryEntry into database", pastInventoryId != -1);

        Cursor pastInventoryCursor = mContext.getContentResolver().query(
                InventoryContract.PastInventoryEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        validateCursor("testBasicPastInventoryQuery", pastInventoryCursor, pastInventoryValues);
    }
}
