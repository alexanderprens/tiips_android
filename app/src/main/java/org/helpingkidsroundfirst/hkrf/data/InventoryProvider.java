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

    // The URI matcher used by this content provider
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private InventoryDbHelper mOpenHelper;

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

    // Make Query builders for joined tables
    // inventory items
    private static final SQLiteQueryBuilder sInventoryItemQueryBuilder;
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
    // Current Inventory
    private static final SQLiteQueryBuilder sCurrentInventoryQueryBuilder;
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

    // Past Inventory
    private static final SQLiteQueryBuilder sPastInventoryQueryBuilder;
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

    // strings for cursors
    // inventory item with id
    private static final String sItemIdSelection =
            InventoryContract.ItemEntry.TABLE_NAME + "." +
                    InventoryContract.ItemEntry._ID + " = ? ";

    // current inventory with id
    private static final String sCurrentInventoryIdSelection =
            InventoryContract.CurrentInventoryEntry.TABLE_NAME + "." +
                    InventoryContract.CurrentInventoryEntry._ID + " = ? ";

    // past inventory with id
    private static final String sPastInventoryIdSelection =
            InventoryContract.PastInventoryEntry.TABLE_NAME + "." +
                    InventoryContract.PastInventoryEntry._ID + " = ? ";

    // Cursors
    // inventory with id cursor
    private Cursor getItemById(Uri uri, String[] projection, String sortOrder) {
        long itemId = InventoryContract.ItemEntry.getItemIdFromUri(uri);

        return mOpenHelper.getReadableDatabase().query(
                InventoryContract.ItemEntry.TABLE_NAME,
                projection,
                sItemIdSelection,
                new String[] {Long.toString(itemId)},
                null,
                null,
                sortOrder
        );
    }

    // current inventory with id cursor
    private Cursor getCurrentInventoryById(Uri uri, String[] projection, String sortOrder) {
        long currentInventoryId = InventoryContract.CurrentInventoryEntry.getCurrentIdFromUri(uri);

        return mOpenHelper.getReadableDatabase().query(
                InventoryContract.ItemEntry.TABLE_NAME,
                projection,
                sCurrentInventoryIdSelection,
                new String[] {Long.toString(currentInventoryId)},
                null,
                null,
                sortOrder
        );
    }

    // past inventory with id cursor
    private Cursor getPastInventoryById(Uri uri, String[] projection, String sortOrder) {
        long pastInventoryId = InventoryContract.PastInventoryEntry.getPastIdFromUri(uri);

        return mOpenHelper.getReadableDatabase().query(
                InventoryContract.ItemEntry.TABLE_NAME,
                projection,
                sPastInventoryIdSelection,
                new String[] {Long.toString(pastInventoryId)},
                null,
                null,
                sortOrder
        );
    }

    // uri matcher to match incoming uris
    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = InventoryContract.CONTENT_AUTHORITY;

        // current inventory codes
        matcher.addURI(authority, InventoryContract.PATH_CURRENT_INVENTORY, CURRENT_INVENTORY);
        matcher.addURI(authority, InventoryContract.PATH_CURRENT_INVENTORY + "/#",
                CURRENT_INVENTORY_WITH_ID);
        matcher.addURI(authority, InventoryContract.PATH_CURRENT_INVENTORY + "/*",
                CURRENT_INVENTORY_WITH_CATEGORY);

        // past inventory codes
        matcher.addURI(authority, InventoryContract.PATH_PAST_INVENTORY, PAST_INVENTORY);
        matcher.addURI(authority, InventoryContract.PATH_PAST_INVENTORY + "/#",
                PAST_INVENTORY_WITH_ID);
        matcher.addURI(authority, InventoryContract.PATH_PAST_INVENTORY + "/*",
                PAST_INVENTORY_WITH_CATEGORY);

        // inventory item codes
        matcher.addURI(authority, InventoryContract.PATH_ITEM, INVENTORY_ITEM);
        matcher.addURI(authority, InventoryContract.PATH_ITEM + "/#", INVENTORY_ITEM_WITH_ID);
        matcher.addURI(authority, InventoryContract.PATH_ITEM + "/*", INVENTORY_ITEM_WITH_CATEGORY);

        // category codes
        matcher.addURI(authority, InventoryContract.PATH_CATEGORY, CATEGORY);
        matcher.addURI(authority, InventoryContract.PATH_CATEGORY + "/#", CATEGORY_WITH_ID);

        return matcher;
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
                // TODO: 2/3/2017 implement query
                retCursor = null;
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
                // TODO: 2/3/2017 implement query
                retCursor = null;
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
                // TODO: 2/3/2017 implement query
                retCursor = null;
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
                // TODO: 2/3/2017 implement query
                retCursor = null;
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

            default:
                throw new UnsupportedOperationException("Unkown uri: " + uri);
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

            default:
                throw new UnsupportedOperationException("Unkown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    // TODO: 12/20/2016 bulk inserts

    //Target api testing helper function
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }

}
