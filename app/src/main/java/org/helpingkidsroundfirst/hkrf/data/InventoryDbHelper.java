package org.helpingkidsroundfirst.hkrf.data;

import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.helpingkidsroundfirst.hkrf.data.InventoryContract.CurrentInventoryEntry;
import org.helpingkidsroundfirst.hkrf.data.InventoryContract.CategoryEntry;
import org.helpingkidsroundfirst.hkrf.data.InventoryContract.ItemEntry;
import org.helpingkidsroundfirst.hkrf.data.InventoryContract.PastInventoryEntry;

/**
 * Manages the local database
 * Created by Alex on 12/20/2016.
 */

public class InventoryDbHelper extends SQLiteOpenHelper {

    //change when database changes
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "inventory.db";

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        
        // create category table
        final String SQL_CREATE_CATEGORY_TABLE = "CREATE TABLE " +
                CategoryEntry.TABLE_NAME + " (" +
                CategoryEntry._ID + " INTEGER PRIMARY KEY" +
                CategoryEntry.COLUMN_NAME + " TEXT NOT NULL);";

        // create item table
        final String SQL_CREATE_ITEM_TABLE = "CREATE TABLE " +
                ItemEntry.TABLE_NAME + " (" +
                ItemEntry._ID + " INTEGER PRIMARY KEY" +
                ItemEntry.COLUMN_BARCODE_ID + "TEXT UNIQUE NOT NULL, " +
                ItemEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                ItemEntry.COLUMN_DESCRIPTION + " TEXT, " +
                ItemEntry.COLUMN_CATEGORY_KEY + " INTEGER NOT NULL, " +
                ItemEntry.COLUMN_VALUE + " REAL NOT NULL, " +

                // set up foreign key for category
                "FOREIGN KEY (" + ItemEntry.COLUMN_CATEGORY_KEY + ") REFERENCES " +
                CategoryEntry.TABLE_NAME + " (" + CategoryEntry._ID + "));";

        // create current inventory table
        final String SQL_CREATE_CURRENT_INVENTORY_TABLE = "CREATE TABLE " + 
                CurrentInventoryEntry.TABLE_NAME + " (" +
                CurrentInventoryEntry._ID + " INTEGER PRIMARY KEY, " + 
                CurrentInventoryEntry.COLUMN_ITEM_KEY + " INTEGER NOT NULL, " +
                CurrentInventoryEntry.COLUMN_QTY + " INTEGER NOT NULL, " +
                CurrentInventoryEntry.COLUMN_DATE_RECEIVED + " INTEGER NOT NULL, " +
                CurrentInventoryEntry.COLUMN_DONOR + " TEXT NOT NULL, " +
                CurrentInventoryEntry.COLUMN_WAREHOUSE + " TEXT NOT NULL, " +

                //set up foreign key for item
                "FOREIGN KEY (" + CurrentInventoryEntry.COLUMN_ITEM_KEY + ") REFERENCES " +
                ItemEntry.TABLE_NAME + " (" + ItemEntry._ID + "));";
        
        // create past inventory table
        final String SQL_CREATE_PAST_INVENTORY_TABLE = "CREATE TABLE " +
                PastInventoryEntry.TABLE_NAME + " (" +
                PastInventoryEntry._ID + " INTEGER PRIMARY KEY, " +
                PastInventoryEntry.COLUMN_ITEM_KEY + " INTEGER NOT NULL, " +
                PastInventoryEntry.COLUMN_QTY + " INTEGER NOT NULL, " +
                PastInventoryEntry.COLUMN_DATE_SHIPPED + " INTEGER NOT NULL, " +
                PastInventoryEntry.COLUMN_DONOR + " TEXT NOT NULL, " +

                //set up foreign key for item
                "FOREIGN KEY (" + CurrentInventoryEntry.COLUMN_ITEM_KEY + ") REFERENCES " +
                ItemEntry.TABLE_NAME + " (" + ItemEntry._ID + "));";

        // execute SQL commands
        sqLiteDatabase.execSQL(SQL_CREATE_CATEGORY_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ITEM_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CURRENT_INVENTORY_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_PAST_INVENTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // If different version table, handle changes
        // TODO: 12/21/2016 do something on database update
    }
}
