package org.helpingkidsroundfirst.hkrf.data;

import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

// TODO: 12/20/2016 import all table contracts
import org.helpingkidsroundfirst.hkrf.data.InventoryContract.CurrentInventoryEntry;

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
        
        //create a table to hold current inventory
        // TODO: 12/20/2016 add in columns
        final String SQL_CREATE_CURRENT_INVENTORY_TABLE = "CREATE TABLE " + 
                CurrentInventoryEntry.TABLE_NAME + " (" +
                CurrentInventoryEntry._ID + " INTEGER PRIMARY KEY, " + 
                "" +
                " );";
        
        // TODO: 12/20/2016 add the rest of the table creates

        sqLiteDatabase.execSQL(SQL_CREATE_CURRENT_INVENTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        //If different version table, handle changes
    }
}
