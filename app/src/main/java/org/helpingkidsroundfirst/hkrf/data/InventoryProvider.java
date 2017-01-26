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
    static final int PAST_INVENTORY = 200;
    static final int PAST_INVENTORY_WITH_ID = 201;
    static final int INVENTORY_ITEM = 300;
    static final int INVENTORY_ITEM_WITH_ID = 301;

    // Make Query builders for past and current inventory
    // Current Inventory
    private static final SQLiteQueryBuilder sCurrentInventoryQueryBuilder;
    static {
        sCurrentInventoryQueryBuilder = new SQLiteQueryBuilder();

        // Inner join item on current inventory
        sCurrentInventoryQueryBuilder.setTables(
                InventoryContract.CurrentInventoryEntry.TABLE_NAME + " INNER JOIN " +
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
                InventoryContract.PastInventoryEntry.TABLE_NAME + " INNER JOIN " +
                        InventoryContract.ItemEntry.TABLE_NAME +
                        " ON " + InventoryContract.PastInventoryEntry.TABLE_NAME +
                        "." + InventoryContract.PastInventoryEntry.COLUMN_ITEM_KEY +
                        " = " + InventoryContract.ItemEntry.TABLE_NAME +
                        "." + InventoryContract.ItemEntry._ID
        );
    }

    // Make cursors

    // uri matcher to match incoming uris
    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = InventoryContract.CONTENT_AUTHORITY;

        // current inventory codes
        matcher.addURI(authority, InventoryContract.PATH_CURRENT_INVENTORY, CURRENT_INVENTORY);
        matcher.addURI(authority, InventoryContract.PATH_CURRENT_INVENTORY + "/#",
                CURRENT_INVENTORY_WITH_ID);

        // inventory item codes
        matcher.addURI(authority, InventoryContract.PATH_ITEM, INVENTORY_ITEM);
        matcher.addURI(authority, InventoryContract.PATH_ITEM + "/#", INVENTORY_ITEM_WITH_ID);

        // past inventory codes
        matcher.addURI(authority, InventoryContract.PATH_PAST_INVENTORY, PAST_INVENTORY);
        matcher.addURI(authority, InventoryContract.PATH_PAST_INVENTORY + "/#",
                PAST_INVENTORY_WITH_ID);

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
            case PAST_INVENTORY_WITH_ID:
                return InventoryContract.PastInventoryEntry.CONTENT_ITEM_TYPE;
            case PAST_INVENTORY:
                return InventoryContract.PastInventoryEntry.CONTENT_TYPE;
            case INVENTORY_ITEM_WITH_ID:
                return InventoryContract.ItemEntry.CONTENT_ITEM_TYPE;
            case INVENTORY_ITEM:
                return InventoryContract.ItemEntry.CONTENT_TYPE;
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
                retCursor = mOpenHelper.getReadableDatabase().query(
                        InventoryContract.CurrentInventoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case PAST_INVENTORY_WITH_ID:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        InventoryContract.PastInventoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
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
            case INVENTORY_ITEM_WITH_ID:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        InventoryContract.ItemEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case INVENTORY_ITEM:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        InventoryContract.ItemEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return retCursor;
    }

    // Handle database insertions
    // TODO: 12/21/2016 fill in switch
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
                            InventoryContract.CurrentInventoryEntry.buildCurrentInventoryUri(_id);
                } else{
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

            case PAST_INVENTORY: {
                long _id = db.insert(InventoryContract.PastInventoryEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = InventoryContract.PastInventoryEntry.buildPastInventoryUri(_id);
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
    // TODO: 12/21/2016 fill in switch
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
    // TODO: 12/21/2016 fill in switch
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
