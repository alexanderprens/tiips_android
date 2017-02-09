package org.helpingkidsroundfirst.hkrf.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Content provider for inventory database
 * Created by Alex on 12/20/2016.
 */

public class InventoryProvider extends ContentProvider {

    // Identifies constants for database commands
    static final int CURRENT_INVENTORY = 100;
    static final int CURRENT_INVENTORY_WITH_ID = 101;
    static final int CURRENT_INVENTORY_WITH_CATEGORY = 102;
    static final int PAST_INVENTORY = 200;
    static final int PAST_INVENTORY_WITH_ID = 201;
    static final int PAST_INVENTORY_WITH_CATEGORY = 202;
    static final int INVENTORY_ITEM = 300;
    static final int INVENTORY_ITEM_WITH_ID = 301;
    static final int INVENTORY_ITEM_WITH_CATEGORY = 302;
    static final int CATEGORY = 400;
    static final int CATEGORY_WITH_ID = 401;
    static final int RECEIVE_INVENTORY = 500;
    static final int RECEIVE_INVENTORY_WITH_ID = 501;
    static final int RECEIVE_INVENTORY_WITH_CATEGORY = 502;
    static final int SHIP_INVENTORY = 600;
    static final int SHIP_INVENTORY_WITH_ID = 601;
    static final int SHIP_INVENTORY_WITH_CATEGORY = 602;
    // The URI matcher used by this content provider
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    // Make Query builders for joined tables
    // inventory items
    private static final SQLiteQueryBuilder sInventoryItemQueryBuilder;
    // Current Inventory
    private static final SQLiteQueryBuilder sCurrentInventoryQueryBuilder;
    // Past Inventory
    private static final SQLiteQueryBuilder sPastInventoryQueryBuilder;
    // receive inventory
    private static final SQLiteQueryBuilder sReceiveInventoryQueryBuilder;
    // ship inventory
    private static final SQLiteQueryBuilder sShipInventoryQueryBuilder;
    // strings for cursors
    // inventory item with id
    private static final String sItemIdSelection =
            InventoryContract.ItemEntry.TABLE_NAME + "." +
                    InventoryContract.ItemEntry._ID + " = ? ";
    // inventory item with category
    private static final String sItemCategorySelection =
            InventoryContract.ItemEntry.COLUMN_CATEGORY_KEY + " = ? ";
    // current inventory with id
    private static final String sCurrentInventoryIdSelection =
            InventoryContract.CurrentInventoryEntry.TABLE_NAME + "." +
                    InventoryContract.CurrentInventoryEntry._ID + " = ? ";
    // past inventory with id
    private static final String sPastInventoryIdSelection =
            InventoryContract.PastInventoryEntry.TABLE_NAME + "." +
                    InventoryContract.PastInventoryEntry._ID + " = ? ";
    // category with id
    private static final String sCategoryIdSelection =
            InventoryContract.CategoryEntry.TABLE_NAME + "." +
                    InventoryContract.CategoryEntry._ID + " = ? ";
    // receive inventory with id
    private static final String sReceiveInventoryIdSelection =
            InventoryContract.ReceiveInventoryEntry.TABLE_NAME + "." +
                    InventoryContract.ReceiveInventoryEntry._ID + " = ? ";
    // ship inventory with id
    private static final String sShipInventoryIdSelection =
            InventoryContract.ShipInventoryEntry.TABLE_NAME + "." +
                    InventoryContract.ShipInventoryEntry._ID + " = ? ";
    static {
        sInventoryItemQueryBuilder = new SQLiteQueryBuilder();

        //inner join category on current inventory
        sInventoryItemQueryBuilder.setTables(
                InventoryContract.ItemEntry.TABLE_NAME + " INNER JOIN " +
                        InventoryContract.CategoryEntry.TABLE_NAME +
                        " ON " + InventoryContract.ItemEntry.TABLE_NAME +
                        "." + InventoryContract.ItemEntry.COLUMN_CATEGORY_KEY +
                        " = " + InventoryContract.CategoryEntry.TABLE_NAME +
                        "." + InventoryContract.CategoryEntry._ID
        );
    }

    static {
        sCurrentInventoryQueryBuilder = new SQLiteQueryBuilder();

        // Inner join item on current inventory
        sCurrentInventoryQueryBuilder.setTables(
                InventoryContract.CurrentInventoryEntry.TABLE_NAME + " INNER JOIN (" +
                        InventoryContract.ItemEntry.TABLE_NAME + " INNER JOIN " +
                        InventoryContract.CategoryEntry.TABLE_NAME +
                        " ON " + InventoryContract.ItemEntry.TABLE_NAME +
                        "." + InventoryContract.ItemEntry.COLUMN_CATEGORY_KEY +
                        " = " + InventoryContract.CategoryEntry.TABLE_NAME +
                        "." + InventoryContract.CategoryEntry._ID + ") " +
                        InventoryContract.ItemEntry.TABLE_NAME +
                        " ON " + InventoryContract.CurrentInventoryEntry.TABLE_NAME +
                        "." + InventoryContract.CurrentInventoryEntry.COLUMN_ITEM_KEY +
                        " = " + InventoryContract.ItemEntry.TABLE_NAME +
                        "." + InventoryContract.ItemEntry._ID
        );
    }

    static {
        sPastInventoryQueryBuilder = new SQLiteQueryBuilder();

        // Inner join item on past inventory
        sPastInventoryQueryBuilder.setTables(
                InventoryContract.PastInventoryEntry.TABLE_NAME + " INNER JOIN (" +
                        InventoryContract.ItemEntry.TABLE_NAME + " INNER JOIN " +
                        InventoryContract.CategoryEntry.TABLE_NAME +
                        " ON " + InventoryContract.ItemEntry.TABLE_NAME +
                        "." + InventoryContract.ItemEntry.COLUMN_CATEGORY_KEY +
                        " = " + InventoryContract.CategoryEntry.TABLE_NAME +
                        "." + InventoryContract.CategoryEntry._ID + ") " +
                        InventoryContract.ItemEntry.TABLE_NAME +
                        " ON " + InventoryContract.PastInventoryEntry.TABLE_NAME +
                        "." + InventoryContract.PastInventoryEntry.COLUMN_ITEM_KEY +
                        " = " + InventoryContract.ItemEntry.TABLE_NAME +
                        "." + InventoryContract.ItemEntry._ID
        );
    }

    static {
        sReceiveInventoryQueryBuilder = new SQLiteQueryBuilder();

        // Inner join item on current inventory
        sReceiveInventoryQueryBuilder.setTables(
                InventoryContract.ReceiveInventoryEntry.TABLE_NAME + " INNER JOIN (" +
                        InventoryContract.ItemEntry.TABLE_NAME + " INNER JOIN " +
                        InventoryContract.CategoryEntry.TABLE_NAME +
                        " ON " + InventoryContract.ItemEntry.TABLE_NAME +
                        "." + InventoryContract.ItemEntry.COLUMN_CATEGORY_KEY +
                        " = " + InventoryContract.CategoryEntry.TABLE_NAME +
                        "." + InventoryContract.CategoryEntry._ID + ") " +
                        InventoryContract.ItemEntry.TABLE_NAME +
                        " ON " + InventoryContract.ReceiveInventoryEntry.TABLE_NAME +
                        "." + InventoryContract.ReceiveInventoryEntry.COLUMN_ITEM_KEY +
                        " = " + InventoryContract.ItemEntry.TABLE_NAME +
                        "." + InventoryContract.ItemEntry._ID
        );
    }

    static {
        sShipInventoryQueryBuilder = new SQLiteQueryBuilder();

        // Inner join item on current inventory
        sShipInventoryQueryBuilder.setTables(
                InventoryContract.ShipInventoryEntry.TABLE_NAME + " INNER JOIN (" +
                        InventoryContract.ItemEntry.TABLE_NAME + " INNER JOIN " +
                        InventoryContract.CategoryEntry.TABLE_NAME +
                        " ON " + InventoryContract.ItemEntry.TABLE_NAME +
                        "." + InventoryContract.ItemEntry.COLUMN_CATEGORY_KEY +
                        " = " + InventoryContract.CategoryEntry.TABLE_NAME +
                        "." + InventoryContract.CategoryEntry._ID + ") " +
                        InventoryContract.ItemEntry.TABLE_NAME +
                        " ON " + InventoryContract.ShipInventoryEntry.TABLE_NAME +
                        "." + InventoryContract.ShipInventoryEntry.COLUMN_ITEM_KEY +
                        " = " + InventoryContract.ItemEntry.TABLE_NAME +
                        "." + InventoryContract.ItemEntry._ID
        );
    }

    private InventoryDbHelper mOpenHelper;

    // uri matcher to match incoming uris
    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = InventoryContract.CONTENT_AUTHORITY;

        // current inventory codes
        matcher.addURI(authority, InventoryContract.PATH_CURRENT_INVENTORY, CURRENT_INVENTORY);
        matcher.addURI(authority, InventoryContract.PATH_CURRENT_INVENTORY + "/#",
                CURRENT_INVENTORY_WITH_ID);
        matcher.addURI(authority, InventoryContract.PATH_CURRENT_INVENTORY + "/*/#",
                CURRENT_INVENTORY_WITH_CATEGORY);

        // past inventory codes
        matcher.addURI(authority, InventoryContract.PATH_PAST_INVENTORY, PAST_INVENTORY);
        matcher.addURI(authority, InventoryContract.PATH_PAST_INVENTORY + "/#",
                PAST_INVENTORY_WITH_ID);
        matcher.addURI(authority, InventoryContract.PATH_PAST_INVENTORY + "/*/#",
                PAST_INVENTORY_WITH_CATEGORY);

        // inventory item codes
        matcher.addURI(authority, InventoryContract.PATH_ITEM, INVENTORY_ITEM);
        matcher.addURI(authority, InventoryContract.PATH_ITEM + "/#", INVENTORY_ITEM_WITH_ID);
        matcher.addURI(authority, InventoryContract.PATH_ITEM + "/*/#", INVENTORY_ITEM_WITH_CATEGORY);

        // category codes
        matcher.addURI(authority, InventoryContract.PATH_CATEGORY, CATEGORY);
        matcher.addURI(authority, InventoryContract.PATH_CATEGORY + "/#", CATEGORY_WITH_ID);

        // receive inventory codes
        matcher.addURI(authority, InventoryContract.PATH_RECEIVE_INVENTORY, RECEIVE_INVENTORY);
        matcher.addURI(authority, InventoryContract.PATH_RECEIVE_INVENTORY + "/#",
                RECEIVE_INVENTORY_WITH_ID);
        matcher.addURI(authority, InventoryContract.PATH_RECEIVE_INVENTORY + "/*/#",
                RECEIVE_INVENTORY_WITH_CATEGORY);

        // ship inventory codes
        matcher.addURI(authority, InventoryContract.PATH_SHIP_INVENTORY, SHIP_INVENTORY);
        matcher.addURI(authority, InventoryContract.PATH_SHIP_INVENTORY + "/#",
                SHIP_INVENTORY_WITH_ID);
        matcher.addURI(authority, InventoryContract.PATH_SHIP_INVENTORY + "/*/#",
                SHIP_INVENTORY_WITH_CATEGORY);

        return matcher;
    }

    // Cursors
    // category with id cursor
    private Cursor getCategoryById(Uri uri, String[] projection, String sortOrder) {
        long categoryId = InventoryContract.CategoryEntry.getCategoryIdFromUri(uri);

        return mOpenHelper.getReadableDatabase().query(
                InventoryContract.CategoryEntry.TABLE_NAME,
                projection,
                sCategoryIdSelection,
                new String[]{Long.toString(categoryId)},
                null,
                null,
                sortOrder
        );
    }

    // inventory with id cursor
    private Cursor getItemById(Uri uri, String[] projection, String sortOrder) {
        long itemId = InventoryContract.ItemEntry.getItemIdFromUri(uri);

        return sInventoryItemQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sItemIdSelection,
                new String[] {Long.toString(itemId)},
                null,
                null,
                sortOrder
        );
    }

    // inventory item with category
    private Cursor getItemByCategory(Uri uri, String[] projection, String sortOrder) {
        long categoryId = InventoryContract.ItemEntry.getCategoryFromUri(uri);

        return sInventoryItemQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sItemCategorySelection,
                new String[]{Long.toString(categoryId)},
                null,
                null,
                sortOrder
        );
    }

    // current inventory with id cursor
    private Cursor getCurrentInventoryById(Uri uri, String[] projection, String sortOrder) {
        long currentInventoryId = InventoryContract.CurrentInventoryEntry.getCurrentIdFromUri(uri);

        return sCurrentInventoryQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sCurrentInventoryIdSelection,
                new String[] {Long.toString(currentInventoryId)},
                null,
                null,
                sortOrder
        );
    }

    // current inventory with category cursor
    private Cursor getCurrentInventoryByCategory(Uri uri, String[] projection, String sortOrder) {
        long currentInventoryId = InventoryContract.CurrentInventoryEntry.getCategoryFromUri(uri);

        return sCurrentInventoryQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sItemCategorySelection,
                new String[]{Long.toString(currentInventoryId)},
                null,
                null,
                sortOrder
        );
    }

    // past inventory with id cursor
    private Cursor getPastInventoryById(Uri uri, String[] projection, String sortOrder) {
        long pastInventoryId = InventoryContract.PastInventoryEntry.getPastIdFromUri(uri);

        return sPastInventoryQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sPastInventoryIdSelection,
                new String[] {Long.toString(pastInventoryId)},
                null,
                null,
                sortOrder
        );
    }

    // past inventory with category cursor
    private Cursor getPastInventoryByCategory(Uri uri, String[] projection, String sortOrder) {
        long pastInventoryId = InventoryContract.PastInventoryEntry.getCategoryFromUri(uri);

        return sPastInventoryQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sItemCategorySelection,
                new String[]{Long.toString(pastInventoryId)},
                null,
                null,
                sortOrder
        );
    }

    // receive inventory with id cursor
    private Cursor getReceiveInventoryById(Uri uri, String[] projection, String sortOrder) {
        long receiveInventoryId = InventoryContract.ReceiveInventoryEntry.getReceiveIdFromUri(uri);

        return sReceiveInventoryQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sReceiveInventoryIdSelection,
                new String[]{Long.toString(receiveInventoryId)},
                null,
                null,
                sortOrder
        );
    }

    // receive inventory with category cursor
    private Cursor getReceiveInventoryByCategory(Uri uri, String[] projection, String sortOrder) {
        long receiveInventoryId = InventoryContract.ReceiveInventoryEntry.getCategoryFromUri(uri);

        return sReceiveInventoryQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sItemCategorySelection,
                new String[]{Long.toString(receiveInventoryId)},
                null,
                null,
                sortOrder
        );
    }

    // ship inventory with id cursor
    private Cursor getShipInventoryById(Uri uri, String[] projection, String sortOrder) {
        long shipInventoryId = InventoryContract.ShipInventoryEntry.getShipIdFromUri(uri);

        return sShipInventoryQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sShipInventoryIdSelection,
                new String[]{Long.toString(shipInventoryId)},
                null,
                null,
                sortOrder
        );
    }

    // ship inventory with category cursor
    private Cursor getShipInventoryByCategory(Uri uri, String[] projection, String sortOrder) {
        long shipInventoryId = InventoryContract.ReceiveInventoryEntry.getCategoryFromUri(uri);

        return sShipInventoryQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sShipInventoryIdSelection,
                new String[]{Long.toString(shipInventoryId)},
                null,
                null,
                sortOrder
        );
    }

    // create provider
    @Override
    public boolean onCreate() {

        mOpenHelper = new InventoryDbHelper((getContext()));
        return true;
    }

    // get uri type
    @Override
    public String getType(Uri uri) {

        // Use Uri matcher to determine uri type
        final int match = sUriMatcher.match(uri);

        switch (match) {
            //make case for each uri type
            case CURRENT_INVENTORY:
                return InventoryContract.CurrentInventoryEntry.CONTENT_TYPE;
            case CURRENT_INVENTORY_WITH_ID:
                return InventoryContract.CurrentInventoryEntry.CONTENT_ITEM_TYPE;
            case CURRENT_INVENTORY_WITH_CATEGORY:
                return InventoryContract.CurrentInventoryEntry.CONTENT_TYPE;
            case PAST_INVENTORY:
                return InventoryContract.PastInventoryEntry.CONTENT_TYPE;
            case PAST_INVENTORY_WITH_ID:
                return InventoryContract.PastInventoryEntry.CONTENT_ITEM_TYPE;
            case PAST_INVENTORY_WITH_CATEGORY:
                return InventoryContract.PastInventoryEntry.CONTENT_TYPE;
            case INVENTORY_ITEM:
                return InventoryContract.ItemEntry.CONTENT_TYPE;
            case INVENTORY_ITEM_WITH_ID:
                return InventoryContract.ItemEntry.CONTENT_ITEM_TYPE;
            case INVENTORY_ITEM_WITH_CATEGORY:
                return InventoryContract.ItemEntry.CONTENT_TYPE;
            case CATEGORY:
                return InventoryContract.CategoryEntry.CONTENT_TYPE;
            case CATEGORY_WITH_ID:
                return InventoryContract.CategoryEntry.CONTENT_ITEM_TYPE;
            case RECEIVE_INVENTORY:
                return InventoryContract.ReceiveInventoryEntry.CONTENT_TYPE;
            case RECEIVE_INVENTORY_WITH_ID:
                return InventoryContract.ReceiveInventoryEntry.CONTENT_ITEM_TYPE;
            case RECEIVE_INVENTORY_WITH_CATEGORY:
                return InventoryContract.ReceiveInventoryEntry.CONTENT_TYPE;
            case SHIP_INVENTORY:
                return InventoryContract.ShipInventoryEntry.CONTENT_TYPE;
            case SHIP_INVENTORY_WITH_ID:
                return InventoryContract.ShipInventoryEntry.CONTENT_ITEM_TYPE;
            case SHIP_INVENTORY_WITH_CATEGORY:
                return InventoryContract.ShipInventoryEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    // actually query database
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Determine what type of request a uri is and query database
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case CURRENT_INVENTORY:
                retCursor = sCurrentInventoryQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case CURRENT_INVENTORY_WITH_ID:
                retCursor = getCurrentInventoryById(uri, projection, sortOrder);
                break;

            case CURRENT_INVENTORY_WITH_CATEGORY:
                retCursor = getCurrentInventoryByCategory(uri, projection, sortOrder);
                break;

            case PAST_INVENTORY:
                retCursor = sPastInventoryQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case PAST_INVENTORY_WITH_ID:
                retCursor = getPastInventoryById(uri, projection, sortOrder);
                break;

            case PAST_INVENTORY_WITH_CATEGORY:
                retCursor = getPastInventoryByCategory(uri, projection, sortOrder);
                break;

            case INVENTORY_ITEM:
                retCursor = sInventoryItemQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case INVENTORY_ITEM_WITH_ID:
                retCursor = getItemById(uri, projection, sortOrder);
                break;

            case INVENTORY_ITEM_WITH_CATEGORY:
                retCursor = getItemByCategory(uri, projection, sortOrder);
                break;

            case CATEGORY:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        InventoryContract.CategoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case CATEGORY_WITH_ID:
                retCursor = getCategoryById(uri, projection, sortOrder);
                break;

            case RECEIVE_INVENTORY:
                retCursor = sReceiveInventoryQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case RECEIVE_INVENTORY_WITH_ID:
                retCursor = getReceiveInventoryById(uri, projection, sortOrder);
                break;

            case RECEIVE_INVENTORY_WITH_CATEGORY:
                retCursor = getReceiveInventoryByCategory(uri, projection, sortOrder);
                break;

            case SHIP_INVENTORY:
                retCursor = sShipInventoryQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case SHIP_INVENTORY_WITH_ID:
                retCursor = getShipInventoryById(uri, projection, sortOrder);
                break;

            case SHIP_INVENTORY_WITH_CATEGORY:
                retCursor = getShipInventoryByCategory(uri, projection, sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return retCursor;
    }

    // Handle database insertions
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch(match){
            case CURRENT_INVENTORY: {
                long _id = db.insert(InventoryContract.CurrentInventoryEntry.TABLE_NAME, null,
                        values);
                if (_id > 0) {
                    returnUri =
                            InventoryContract.CurrentInventoryEntry.buildCurrentInventoryUri();
                } else{
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }

            case PAST_INVENTORY: {
                long _id = db.insert(InventoryContract.PastInventoryEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = InventoryContract.PastInventoryEntry.buildPastInventoryUri();
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }

            case INVENTORY_ITEM: {
                long _id = db.insert(InventoryContract.ItemEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = InventoryContract.ItemEntry.buildInventoryItemWithIdUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }

            case CATEGORY: {
                long _id = db.insert(InventoryContract.CategoryEntry.TABLE_NAME, null, values);
                if(_id > 0) {
                    returnUri = InventoryContract.CategoryEntry.buildCategoryWithIdUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }

            case RECEIVE_INVENTORY: {
                long _id = db.insert(InventoryContract.ReceiveInventoryEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = InventoryContract.ReceiveInventoryEntry.buildReceiveInventoryWithIdUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }

            case SHIP_INVENTORY: {
                long _id = db.insert(InventoryContract.ShipInventoryEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = InventoryContract.ShipInventoryEntry.buildShipInventoryWithCategoryUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    // Handle database deletions
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        //delete all rows in selection, return #rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case CURRENT_INVENTORY:
                rowsDeleted = db.delete(InventoryContract.CurrentInventoryEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;

            case PAST_INVENTORY:
                rowsDeleted = db.delete(InventoryContract.PastInventoryEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;

            case INVENTORY_ITEM:
                rowsDeleted = db.delete(InventoryContract.ItemEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;

            case CATEGORY:
                rowsDeleted = db.delete(InventoryContract.CategoryEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;

            case RECEIVE_INVENTORY:
                rowsDeleted = db.delete(InventoryContract.ReceiveInventoryEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;

            case SHIP_INVENTORY:
                rowsDeleted = db.delete(InventoryContract.ShipInventoryEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        //if rows deleted = 0 all rows have been deleted
        if(rowsDeleted !=0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    // Handle database updates
    @Override
    public int update( Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case CURRENT_INVENTORY:
                rowsUpdated = db.update(InventoryContract.CurrentInventoryEntry.TABLE_NAME, values,
                        selection, selectionArgs);
                break;

            case PAST_INVENTORY:
                rowsUpdated = db.update(InventoryContract.PastInventoryEntry.TABLE_NAME, values,
                        selection, selectionArgs);
                break;

            case INVENTORY_ITEM:
                rowsUpdated = db.update(InventoryContract.ItemEntry.TABLE_NAME, values,
                        selection, selectionArgs);
                break;

            case CATEGORY:
                rowsUpdated = db.update(InventoryContract.CategoryEntry.TABLE_NAME, values,
                        selection, selectionArgs);
                break;

            case RECEIVE_INVENTORY:
                rowsUpdated = db.update(InventoryContract.ReceiveInventoryEntry.TABLE_NAME,
                        values, selection, selectionArgs);
                break;

            case SHIP_INVENTORY:
                rowsUpdated = db.update(InventoryContract.ShipInventoryEntry.TABLE_NAME,
                        values, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    //Target api testing helper function
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }

}
