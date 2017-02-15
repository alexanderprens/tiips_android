package org.helpingkidsroundfirst.hkrf.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.helpingkidsroundfirst.hkrf.data.InventoryContract.CategoryEntry;
import org.helpingkidsroundfirst.hkrf.data.InventoryContract.CurrentInventoryEntry;
import org.helpingkidsroundfirst.hkrf.data.InventoryContract.ItemEntry;
import org.helpingkidsroundfirst.hkrf.data.InventoryContract.PastInventoryEntry;
import org.helpingkidsroundfirst.hkrf.data.InventoryContract.ReceiveInventoryEntry;
import org.helpingkidsroundfirst.hkrf.data.InventoryContract.ShipInventoryEntry;

/**
 * Manages the local database
 * Created by Alex on 12/20/2016.
 */

public class InventoryDbHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "inventory.db";
    //change when database changes
    private static final int DATABASE_VERSION = 12;

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // create category table
        final String SQL_CREATE_CATEGORY_TABLE = "CREATE TABLE " +
                CategoryEntry.TABLE_NAME + " (" +
                CategoryEntry._ID + " INTEGER PRIMARY KEY, " +
                CategoryEntry.COLUMN_CATEGORY + " TEXT UNIQUE NOT NULL, " +
                CategoryEntry.COLUMN_BARCODE_PREFIX + " TEXT UNIQUE NOT NULL);";

        // create item table
        final String SQL_CREATE_ITEM_TABLE = "CREATE TABLE " +
                ItemEntry.TABLE_NAME + " (" +
                ItemEntry._ID + " INTEGER PRIMARY KEY, " +
                ItemEntry.COLUMN_BARCODE_ID + " TEXT UNIQUE NOT NULL, " +
                ItemEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                ItemEntry.COLUMN_DESCRIPTION + " TEXT, " +
                ItemEntry.COLUMN_CATEGORY_KEY + " TEXT, " +
                ItemEntry.COLUMN_VALUE + " REAL NOT NULL, " +

                // set up foreign key for category
                "FOREIGN KEY (" + ItemEntry.COLUMN_CATEGORY_KEY + ") REFERENCES " +
                CategoryEntry.TABLE_NAME + " (" + CategoryEntry._ID + "));";

        // create current inventory table
        final String SQL_CREATE_CURRENT_INVENTORY_TABLE = "CREATE TABLE " + 
                CurrentInventoryEntry.TABLE_NAME + " (" +
                CurrentInventoryEntry._ID + " INTEGER PRIMARY KEY, " +
                CurrentInventoryEntry.COLUMN_ITEM_KEY + " INTEGER NOT NULL, " +
                CurrentInventoryEntry.COLUMN_VALUE + " REAL NOT NULL, " +
                CurrentInventoryEntry.COLUMN_QTY + " INTEGER NOT NULL, " +
                CurrentInventoryEntry.COLUMN_DATE_RECEIVED + " TEXT NOT NULL, " +
                CurrentInventoryEntry.COLUMN_DONOR + " TEXT NOT NULL, " +
                CurrentInventoryEntry.COLUMN_WAREHOUSE + " TEXT NOT NULL, " +

                // set up foreign key for category
                "FOREIGN KEY (" + CurrentInventoryEntry.COLUMN_ITEM_KEY + ") REFERENCES " +
                ItemEntry.TABLE_NAME + " (" + ItemEntry._ID + "));";

        // create receive inventory table
        final String SQL_CREATE_RECEIVE_INVENTORY_TABLE = "CREATE TABLE " +
                ReceiveInventoryEntry.TABLE_NAME + " (" +
                ReceiveInventoryEntry._ID + " INTEGER PRIMARY KEY, " +
                ReceiveInventoryEntry.COLUMN_ITEM_KEY + " INTEGER NOT NULL, " +
                ReceiveInventoryEntry.COLUMN_VALUE + " REAL NOT NULL, " +
                ReceiveInventoryEntry.COLUMN_QTY + " INTEGER, " +
                ReceiveInventoryEntry.COLUMN_DATE_RECEIVED + " TEXT, " +
                ReceiveInventoryEntry.COLUMN_DONOR + " TEXT, " +
                ReceiveInventoryEntry.COLUMN_WAREHOUSE + " TEXT, " +

                // set up foreign key for category
                "FOREIGN KEY (" + ReceiveInventoryEntry.COLUMN_ITEM_KEY + ") REFERENCES " +
                ItemEntry.TABLE_NAME + " (" + ItemEntry._ID + "));";
        
        // create past inventory table
        final String SQL_CREATE_PAST_INVENTORY_TABLE = "CREATE TABLE " +
                PastInventoryEntry.TABLE_NAME + " (" +
                PastInventoryEntry._ID + " INTEGER PRIMARY KEY, " +
                PastInventoryEntry.COLUMN_BARCODE_ID + " TEXT NOT NULL, " +
                PastInventoryEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                PastInventoryEntry.COLUMN_DESCRIPTION + " TEXT, " +
                PastInventoryEntry.COLUMN_CATEGORY_KEY + " TEXT, " +
                PastInventoryEntry.COLUMN_VALUE + " REAL NOT NULL, " +
                PastInventoryEntry.COLUMN_QTY + " INTEGER NOT NULL, " +
                PastInventoryEntry.COLUMN_DATE_SHIPPED + " TEXT NOT NULL, " +
                PastInventoryEntry.COLUMN_DONOR + " TEXT NOT NULL, " +

                // set up foreign key for category
                "FOREIGN KEY (" + PastInventoryEntry.COLUMN_CATEGORY_KEY + ") REFERENCES " +
                CategoryEntry.TABLE_NAME + " (" + CategoryEntry._ID + "));";

        // create ship inventory table
        final String SQL_CREATE_SHIP_INVENTORY_TABLE = "CREATE TABLE " +
                ShipInventoryEntry.TABLE_NAME + " (" +
                ShipInventoryEntry._ID + " INTEGER PRIMARY KEY, " +
                ShipInventoryEntry.COLUMN_BARCODE_ID + " TEXT NOT NULL, " +
                ShipInventoryEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                ShipInventoryEntry.COLUMN_DESCRIPTION + " TEXT, " +
                ShipInventoryEntry.COLUMN_CATEGORY_KEY + " TEXT, " +
                ShipInventoryEntry.COLUMN_VALUE + " REAL NOT NULL, " +
                ShipInventoryEntry.COLUMN_QTY + " INTEGER, " +
                ShipInventoryEntry.COLUMN_DATE_SHIPPED + " TEXT, " +
                ShipInventoryEntry.COLUMN_DONOR + " TEXT, " +

                // set up foreign key for category
                "FOREIGN KEY (" + ShipInventoryEntry.COLUMN_CATEGORY_KEY + ") REFERENCES " +
                CategoryEntry.TABLE_NAME + " (" + CategoryEntry._ID + "));";

        // execute SQL commands
        sqLiteDatabase.execSQL(SQL_CREATE_CATEGORY_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ITEM_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CURRENT_INVENTORY_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_PAST_INVENTORY_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_RECEIVE_INVENTORY_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_SHIP_INVENTORY_TABLE);

        // insert "Un-categorized" into category table
        ContentValues defaultCategory = new ContentValues();
        defaultCategory.put(CategoryEntry.COLUMN_CATEGORY, "Uncategorized");
        defaultCategory.put(CategoryEntry.COLUMN_BARCODE_PREFIX, "00");
        sqLiteDatabase.insert(CategoryEntry.TABLE_NAME, null, defaultCategory);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // If different version table, handle changes

        // For this database revision (x to 9) drop old tables
        if (oldVersion <= 11) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CurrentInventoryEntry.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PastInventoryEntry.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ItemEntry.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CategoryEntry.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReceiveInventoryEntry.TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ShipInventoryEntry.TABLE_NAME);
            onCreate(sqLiteDatabase);
        }
    }
}
