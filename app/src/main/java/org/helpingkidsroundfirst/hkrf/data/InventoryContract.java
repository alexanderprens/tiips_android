package org.helpingkidsroundfirst.hkrf.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;


/**
 * Contracts for SQLite Database Tables
 * Created by Alex on 12/20/2016.
 */

public class InventoryContract {
    //Content authority is a name for the entire content provider. must be unique
    public static final String CONTENT_AUTHORITY     = "org.helpingkidsroundfirst.hkrf";

    //use CONTENT_AUTHORITY to create base URI
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //possible data paths
    //TODO: Make all tables here
    public static final String PATH_CURRENT_INVENTORY = "current_inventory";
    public static final String PATH_INVENTORY_CATEGORIES = "inventory_categories";

    /* Define Current Inventory Table */
    public static final class CurrentInventoryEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CURRENT_INVENTORY).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/" + PATH_CURRENT_INVENTORY;

        //Table name
        public static final String TABLE_NAME = "current_inventory";

        //TODO: Make columns for inventory


        public static Uri buildCurrentInventoryUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    //TODO: Define the rest of the tables


}
