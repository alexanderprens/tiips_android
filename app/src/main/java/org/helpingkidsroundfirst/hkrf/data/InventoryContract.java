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
    // Content authority is a name for the entire content provider. must be unique
    public static final String CONTENT_AUTHORITY     = "org.helpingkidsroundfirst.hkrf";

    // use CONTENT_AUTHORITY to create base URI
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Table names
    public static final String PATH_CATEGORY = "categories";
    public static final String PATH_ITEM = "items";
    public static final String PATH_CURRENT_INVENTORY = "current_inventory";
    public static final String PATH_PAST_INVENTORY = "past_inventory";
    public static final String PATH_RECEIVE_INVENTORY = "receive_inventory";
    public static final String PATH_SHIP_INVENTORY = "ship_inventory";

    /* Define Category Table */
    public static final class CategoryEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CATEGORY).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORY;

        // Table name
        public static final String TABLE_NAME = "categories";

        // Table columns
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_BARCODE_PREFIX = "barcode_prefix";

        // Uri builders and handlers
        public static Uri buildCategoryUri() {
            return CONTENT_URI;
        }

        public static Uri buildCategoryWithIdUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getCategoryIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }

    /* Define Item Table */
    public static final class ItemEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ITEM).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEM;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEM;

        // Table name
        public static final String TABLE_NAME = "item";

        // Table columns
        public static final String COLUMN_BARCODE_ID = "barcode_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_CATEGORY_KEY = "category_id";
        public static final String COLUMN_VALUE = "value";

        public static Uri buildInventoryItemUri() {
            return CONTENT_URI;
        }

        public static Uri buildInventoryItemWithIdUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildInventoryItemWithCategoryUri(Long category) {
            return CONTENT_URI.buildUpon().appendPath("category_id")
                    .appendPath(Long.toString(category)).build();
        }

        public static long getItemIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static Long getCategoryFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }
    }

    /* Define Current Inventory Table */
    public static final class CurrentInventoryEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CURRENT_INVENTORY).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY +
                        "/" + PATH_CURRENT_INVENTORY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY +
                        "/" + PATH_CURRENT_INVENTORY;

        // Table name
        public static final String TABLE_NAME = "current_inventory";

        // Table columns
        public static final String COLUMN_BARCODE_ID = "current_barcode_id";
        public static final String COLUMN_NAME = "current_name";
        public static final String COLUMN_DESCRIPTION = "current_description";
        public static final String COLUMN_CATEGORY_KEY = "current_category_id";
        public static final String COLUMN_VALUE = "value";
        public static final String COLUMN_QTY = "qty";
        public static final String COLUMN_DATE_RECEIVED = "date_received";
        public static final String COLUMN_DONOR = "donor";
        public static final String COLUMN_WAREHOUSE = "warehouse";

        public static Uri buildCurrentInventoryUri() {
            return CONTENT_URI;
        }

        public static Uri buildCurrentInventoryWithIdUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildCurrentInventoryWithCategoryUri(Long category) {
            return CONTENT_URI.buildUpon().appendPath("category_id")
                    .appendPath(Long.toString(category)).build();
        }

        public static long getCurrentIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static Long getCategoryFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }
    }

    /* Define Receive Inventory Table */
    public static final class ReceiveInventoryEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECEIVE_INVENTORY).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY +
                        "/" + PATH_RECEIVE_INVENTORY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY +
                        "/" + PATH_RECEIVE_INVENTORY;

        // Table name
        public static final String TABLE_NAME = "receive_inventory";

        // Table columns
        public static final String COLUMN_BARCODE_ID = "receive_barcode_id";
        public static final String COLUMN_NAME = "receive_name";
        public static final String COLUMN_DESCRIPTION = "receive_description";
        public static final String COLUMN_CATEGORY_KEY = "receive_category_id";
        public static final String COLUMN_VALUE = "receive_value";
        public static final String COLUMN_QTY = "receive_qty";
        public static final String COLUMN_DATE_RECEIVED = "receive_date_received";
        public static final String COLUMN_DONOR = "receive_donor";
        public static final String COLUMN_WAREHOUSE = "receive_warehouse";

        public static Uri buildReceiveInventoryUri() {
            return CONTENT_URI;
        }

        public static Uri buildReceiveInventoryWithIdUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildReceiveInventoryWithCategoryUri(Long category) {
            return CONTENT_URI.buildUpon().appendPath("category_id")
                    .appendPath(Long.toString(category)).build();
        }

        public static long getReceiveIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static Long getCategoryFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }
    }

    /* Define Past Inventory Table */
    public static final class PastInventoryEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PAST_INVENTORY).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +
                        PATH_PAST_INVENTORY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +  "/" + CONTENT_AUTHORITY + "/" +
                        PATH_PAST_INVENTORY;

        // Table name
        public static final String TABLE_NAME = "past_inventory";

        // Table columns
        public static final String COLUMN_BARCODE_ID = "past_barcode_id";
        public static final String COLUMN_NAME = "past_name";
        public static final String COLUMN_DESCRIPTION = "past_description";
        public static final String COLUMN_CATEGORY_KEY = "past_category_id";
        public static final String COLUMN_VALUE = "past_value";
        public static final String COLUMN_QTY = "past_qty";
        public static final String COLUMN_DATE_SHIPPED = "date_shipped";
        public static final String COLUMN_DONOR = "past_donor";

        public static Uri buildPastInventoryUri() {
            return CONTENT_URI;
        }

        public static Uri buildPastInventoryWithIdUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildPastInventoryWithCategoryUri(Long category) {
            return CONTENT_URI.buildUpon().appendPath("category_id")
                    .appendPath(Long.toString(category)).build();
        }

        public static long getPastIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static Long getCategoryFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }
    }

    /* Define Ship Inventory Table */
    public static final class ShipInventoryEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SHIP_INVENTORY).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +
                        PATH_SHIP_INVENTORY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +
                        PATH_SHIP_INVENTORY;

        // Table name
        public static final String TABLE_NAME = "ship_inventory";

        // Table columns
        public static final String COLUMN_BARCODE_ID = "ship_barcode_id";
        public static final String COLUMN_NAME = "ship_name";
        public static final String COLUMN_DESCRIPTION = "ship_description";
        public static final String COLUMN_CATEGORY_KEY = "ship_category_id";
        public static final String COLUMN_VALUE = "ship_value";
        public static final String COLUMN_QTY = "ship_qty";
        public static final String COLUMN_DATE_SHIPPED = "ship_date_shipped";
        public static final String COLUMN_DONOR = "ship_donor";

        public static Uri buildShipInventoryUri() {
            return CONTENT_URI;
        }

        public static Uri buildShipInventoryWithIdUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildShipInventoryWithCategoryUri(Long category) {
            return CONTENT_URI.buildUpon().appendPath("category_id")
                    .appendPath(Long.toString(category)).build();
        }

        public static long getShipIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static Long getCategoryFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }
    }
}