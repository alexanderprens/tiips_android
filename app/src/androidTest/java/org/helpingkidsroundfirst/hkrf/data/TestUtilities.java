package org.helpingkidsroundfirst.hkrf.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

/**
 * Created by Alex on 1/25/2017.
 */

public class TestUtilities extends AndroidTestCase {

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static long insertTestItem(SQLiteDatabase db) {

        long catRowId = insertTestCategory(db);

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
        return itemRowId;
    }

    static ContentValues createItemValues(long categoryId) {


        //make values to put into database
        ContentValues itemValues = new ContentValues();
        itemValues.put(InventoryContract.ItemEntry.COLUMN_BARCODE_ID, "00000001");
        itemValues.put(InventoryContract.ItemEntry.COLUMN_NAME, "Bat");
        itemValues.put(InventoryContract.ItemEntry.COLUMN_DESCRIPTION, "Wooden");
        itemValues.put(InventoryContract.ItemEntry.COLUMN_CATEGORY_KEY, categoryId);
        itemValues.put(InventoryContract.ItemEntry.COLUMN_VALUE, 10);

        return itemValues;
    }

    static long insertTestCategory(SQLiteDatabase db) {
        ContentValues contentValues = createCategoryValues();

        long catRowId;
        catRowId = db.insert(InventoryContract.CategoryEntry.TABLE_NAME, null, contentValues);
        return catRowId;
    }

    static ContentValues createCategoryValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(InventoryContract.CategoryEntry.COLUMN_CATEGORY, "Baseball");
        contentValues.put(InventoryContract.CategoryEntry.COLUMN_BARCODE_PREFIX, "BB");

        return contentValues;
    }

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }
}
