package org.helpingkidsroundfirst.hkrf.data;

import android.content.ContentProvider;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Content provider for inventory database
 * Created by Alex on 12/20/2016.
 */

public class InventoryProvider extends ContentProvider {

    //The URI matcher used by this content provider
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private InventoryDbHelper mOpenHelper;

    // Identifies constants for database commands
    // TODO: 12/20/2016 make ints for different database commands
    static final int CURRENT_INVENTORY = 100;
    static final int PAST_INVENTORY = 101;

    // TODO: 12/20/2016 make cursors for different tables and queries

    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = InventoryContract.CONTENT_AUTHORITY;

        //create code for every type of URI
        matcher.addURI(authority, InventoryContract.PATH_CURRENT_INVENTORY, CURRENT_INVENTORY);

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
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    //actually query database
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Determine what type of request a uri is and query database
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            //"inventory"
            case CURRENT_INVENTORY: {
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
            }

            default:
                throw new UnsupportedOperationException("Unkown uri: " + uri);
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
                    returnUri = InventoryContract.CurrentInventoryEntry.buildCurrentInventoryUri(_id);
                } else{
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unkown uri: " + uri);
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
            default:
                throw new UnsupportedOperationException("Unkown uri: " + uri);
        }
        //if rowsdeleted = 0 all rows have been deleted
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
