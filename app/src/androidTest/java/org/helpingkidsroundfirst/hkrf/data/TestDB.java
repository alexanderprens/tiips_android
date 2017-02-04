package org.helpingkidsroundfirst.hkrf.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

/**
 * Created by Alex on 1/25/2017.
 */

public class TestDB extends AndroidTestCase {
    public static final String LOG_TAG = TestDB.class.getSimpleName();

    // clean start
    void deleteTheDatabase() {
        mContext.deleteDatabase(InventoryDbHelper.DATABASE_NAME);
    }

    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {
        // build hash set of all table names
        final HashSet<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(InventoryContract.CategoryEntry.TABLE_NAME);
        tableNameHashSet.add(InventoryContract.ItemEntry.TABLE_NAME);
        tableNameHashSet.add(InventoryContract.CurrentInventoryEntry.TABLE_NAME);
        tableNameHashSet.add(InventoryContract.PastInventoryEntry.TABLE_NAME);

        mContext.deleteDatabase(InventoryDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new InventoryDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // check if tables are created
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify all wanted tables are created
        do {
            tableNameHashSet.remove(c.getString(0));
        }while (c.moveToNext());

        // if failed, not all tables are created
        assertTrue("Error: database was created without all tables",
                tableNameHashSet.isEmpty());

        //check if tables contain correct columns
        c = db.rawQuery("PRAGMA table_info(" + InventoryContract.CategoryEntry.TABLE_NAME
            + ")", null);
        assertTrue("Error: unable to query database for table info", c.moveToFirst());

        //build hash set of column names in category table
        final HashSet<String> categoryColumnHashSet = new HashSet<>();
        categoryColumnHashSet.add(InventoryContract.CategoryEntry._ID);
        categoryColumnHashSet.add(InventoryContract.CategoryEntry.COLUMN_NAME);
        categoryColumnHashSet.add(InventoryContract.CategoryEntry.COLUMN_BARCODE_PREFIX);

        int columnNameIndex = c.getColumnIndex("name");
        do{
            String columnName = c.getString(columnNameIndex);
            categoryColumnHashSet.remove(columnName);
        }while (c.moveToNext());

        //check category columns are still valid
        assertTrue("Error: category table missing columns", categoryColumnHashSet.isEmpty());

        c = db.rawQuery("PRAGMA table_info(" + InventoryContract.ItemEntry.TABLE_NAME + ")",
                null);
        assertTrue("Error: unable to query database for table info", c.moveToFirst());

        //build hash set of column names in item table
        final HashSet<String> itemColumnHashSet = new HashSet<>();
        itemColumnHashSet.add(InventoryContract.ItemEntry._ID);
        itemColumnHashSet.add(InventoryContract.ItemEntry.COLUMN_BARCODE_ID);
        itemColumnHashSet.add(InventoryContract.ItemEntry.COLUMN_NAME);
        itemColumnHashSet.add(InventoryContract.ItemEntry.COLUMN_DESCRIPTION);
        itemColumnHashSet.add(InventoryContract.ItemEntry.COLUMN_CATEGORY_KEY);
        itemColumnHashSet.add(InventoryContract.ItemEntry.COLUMN_VALUE);

        columnNameIndex = c.getColumnIndex("name");
        do{
            String columnName = c.getString(columnNameIndex);
            itemColumnHashSet.remove(columnName);
        }while (c.moveToNext());

        // checks if item table columns are all there
        assertTrue("Error: inventory item table missing columns", itemColumnHashSet.isEmpty());

        // now check current inventory table
        c = db.rawQuery("PRAGMA table_info(" + InventoryContract.CurrentInventoryEntry.TABLE_NAME
            + ")", null);
        assertTrue("Error: unable to query database for current inv info", c.moveToFirst());

        final HashSet<String> currInvColumnHashSet = new HashSet<>();
        currInvColumnHashSet.add(InventoryContract.CurrentInventoryEntry._ID);
        currInvColumnHashSet.add(InventoryContract.CurrentInventoryEntry.COLUMN_ITEM_KEY);
        currInvColumnHashSet.add(InventoryContract.CurrentInventoryEntry.COLUMN_DATE_RECEIVED);
        currInvColumnHashSet.add(InventoryContract.CurrentInventoryEntry.COLUMN_DONOR);
        currInvColumnHashSet.add(InventoryContract.CurrentInventoryEntry.COLUMN_QTY);

        columnNameIndex = c.getColumnIndex("name");
        do{
            String columnName = c.getString(columnNameIndex);
            itemColumnHashSet.remove(columnName);
        }while (c.moveToNext());

        assertTrue("Error: current inventory table missing columns", itemColumnHashSet.isEmpty());

        // now check past inventory table
        c = db.rawQuery("PRAGMA table_info(" + InventoryContract.PastInventoryEntry.TABLE_NAME
                + ")", null);
        assertTrue("Error: unable to query database for current inv info", c.moveToFirst());

        final HashSet<String> pastInvColumnHashSet = new HashSet<>();
        pastInvColumnHashSet.add(InventoryContract.PastInventoryEntry._ID);
        pastInvColumnHashSet.add(InventoryContract.PastInventoryEntry.COLUMN_ITEM_KEY);
        pastInvColumnHashSet.add(InventoryContract.PastInventoryEntry.COLUMN_DATE_SHIPPED);
        pastInvColumnHashSet.add(InventoryContract.PastInventoryEntry.COLUMN_DONOR);
        pastInvColumnHashSet.add(InventoryContract.PastInventoryEntry.COLUMN_QTY);

        columnNameIndex = c.getColumnIndex("name");
        do{
            String columnName = c.getString(columnNameIndex);
            itemColumnHashSet.remove(columnName);
        }while (c.moveToNext());

        assertTrue("Error: current inventory table missing columns", itemColumnHashSet.isEmpty());

        db.close();
    }


    // add item to the item table
    public void testItemTable(){
        insertItem();
        deleteTheDatabase();
    }

    // test current inventory
    public void testCurrentInventoryTable(){

        // insert inventory item
        long itemRowId = insertItem();
        assertFalse("Error: item not inserted correctly", itemRowId == -1L);

        // get writable database
        InventoryDbHelper dbHelper = new InventoryDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // create current inventory values
        ContentValues currentInventoryValues = new ContentValues();
        currentInventoryValues.put(InventoryContract.CurrentInventoryEntry.COLUMN_ITEM_KEY,itemRowId);
        currentInventoryValues.put(InventoryContract.CurrentInventoryEntry.COLUMN_DATE_RECEIVED, "170126");
        currentInventoryValues.put(InventoryContract.CurrentInventoryEntry.COLUMN_DONOR, "Brookings Health");
        currentInventoryValues.put(InventoryContract.CurrentInventoryEntry.COLUMN_QTY, 10);
        currentInventoryValues.put(InventoryContract.CurrentInventoryEntry.COLUMN_WAREHOUSE, "One");

        // insert values into table
        long currentInventoryRowId = db.insert(InventoryContract.CurrentInventoryEntry.TABLE_NAME,
                null, currentInventoryValues);
        assertTrue(currentInventoryRowId != -1);

        // query database
        Cursor cursor = db.query(
                InventoryContract.CurrentInventoryEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        // check cursor contains data
        assertTrue("Error: no records returned from current inventory query", cursor.moveToFirst());

        // validate the current inventory query
        TestUtilities.validateCurrentRecord("test insert current inventory", cursor, currentInventoryValues);

        // check for only one entry in table
        assertFalse("Error: more than one record in current inventory", cursor.moveToNext());

        //close up
        cursor.close();
        dbHelper.close();
        deleteTheDatabase();
    }

    // test past inventory
    public void testPastInventoryTable(){

        // insert inventory item
        long itemRowId = insertItem();
        assertFalse("Error: item not inserted correctly", itemRowId == -1L);

        // get writable database
        InventoryDbHelper dbHelper = new InventoryDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // create current inventory values
        ContentValues pastInventoryValues = new ContentValues();
        pastInventoryValues.put(InventoryContract.PastInventoryEntry.COLUMN_ITEM_KEY,itemRowId);
        pastInventoryValues.put(InventoryContract.PastInventoryEntry.COLUMN_DATE_SHIPPED, "170126");
        pastInventoryValues.put(InventoryContract.PastInventoryEntry.COLUMN_DONOR, "Brookings Health");
        pastInventoryValues.put(InventoryContract.PastInventoryEntry.COLUMN_QTY, 10);

        // insert values into table
        long pastInventoryRowId = db.insert(InventoryContract.PastInventoryEntry.TABLE_NAME,
                null, pastInventoryValues);
        assertTrue(pastInventoryRowId != -1);

        // query database
        Cursor cursor = db.query(
                InventoryContract.PastInventoryEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        // check cursor contains data
        assertTrue("Error: no records returned from current inventory query", cursor.moveToFirst());

        // validate the current inventory query
        TestUtilities.validateCurrentRecord("test insert current inventory", cursor, pastInventoryValues);

        // check for only one entry in table
        assertFalse("Error: more than one record in current inventory", cursor.moveToNext());

        //close up
        cursor.close();
        dbHelper.close();
        deleteTheDatabase();
    }

    // insert item into item table
    public long insertItem(){
        //get database
        InventoryDbHelper dbHelper = new InventoryDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long catRowId = insertCategory();

        //make values to put into database
        ContentValues itemValues = new ContentValues();
        itemValues.put(InventoryContract.ItemEntry.COLUMN_BARCODE_ID, "00000001");
        itemValues.put(InventoryContract.ItemEntry.COLUMN_NAME, "Bat");
        itemValues.put(InventoryContract.ItemEntry.COLUMN_DESCRIPTION, "Wooden");
        itemValues.put(InventoryContract.ItemEntry.COLUMN_CATEGORY_KEY, catRowId);
        itemValues.put(InventoryContract.ItemEntry.COLUMN_VALUE, 10);

        // insert into database
        long itemRowId;
        itemRowId = db.insert(InventoryContract.ItemEntry.TABLE_NAME, null, itemValues);

        assertTrue(itemRowId != -1);

        Cursor cursor = db.query(
                InventoryContract.ItemEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        // move cursor to verify data exists
        assertTrue("Error: no records found in inventory query", cursor.moveToFirst());

        // validate data in cursor
        TestUtilities.validateCurrentRecord("Error: Item Query Validation Failed", cursor,
                itemValues);

        // verify only one record
        assertFalse("Error: more than one record returned from item query", cursor.moveToNext());

        // close cursor and database
        cursor.close();
        db.close();
        return itemRowId;
    }

    public long insertCategory(){
        //get database
        InventoryDbHelper dbHelper = new InventoryDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //make values to put into database
        ContentValues contentValues = new ContentValues();
        contentValues.put(InventoryContract.CategoryEntry.COLUMN_NAME, "Baseball Equipment");
        contentValues.put(InventoryContract.CategoryEntry.COLUMN_BARCODE_PREFIX, "BB");

        //insert into database
        long catRowId;
        catRowId = db.insert(InventoryContract.CategoryEntry.TABLE_NAME, null, contentValues);

        assertTrue(catRowId != -1);

        Cursor cursor = db.query(
                InventoryContract.CategoryEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        // if cursor count is greater than 1 inserted correctly
        assertTrue("Error: category item not inserted", cursor.getCount() <= 1);

        cursor.moveToFirst();
        cursor.moveToNext();

        //validate data
        TestUtilities.validateCurrentRecord("Error: category item inserted incorrectly", cursor,
                contentValues);

        //close cursor and database
        cursor.close();
        db.close();
        return catRowId;
    }
}