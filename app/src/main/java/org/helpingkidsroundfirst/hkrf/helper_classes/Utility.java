package org.helpingkidsroundfirst.hkrf.helper_classes;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.helpingkidsroundfirst.hkrf.data.InventoryContract;
import org.helpingkidsroundfirst.hkrf.navigation_bar_activities.ips.locate_item.scan_devices.GetLocationDataFragment;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by alexa on 2/12/2017.
 */

public class Utility {
    // convert date from date picker to string date
    public static String getDatePickerString(int year, int month, int day) {
        String date = "%04d" + "-" + "%02d" + "-" + "%02d";
        return String.format(date, year, month + 1, day);
    }

    // create initial data for tags on database startup
    public static void initialTagData(SQLiteDatabase sqLiteDatabase) {

        ContentValues contentValue = new ContentValues();

        for (int i = 1; i <= 11; i++) {

            // set uuid
            contentValue.put(InventoryContract.TagEntry.COLUMN_ID,
                    GetLocationDataFragment.CONST_UUIDS[i]);

            // set default name
            String name = String.format(Locale.US, "TAG%2d", i);
            contentValue.put(InventoryContract.TagEntry.COLUMN_NAME, name);

            // set active
            contentValue.put(InventoryContract.TagEntry.COLUMN_ACTIVE, false);

            // insert into table
            sqLiteDatabase.insert(
                    InventoryContract.TagEntry.TABLE_NAME,
                    null,
                    contentValue
            );
        }

        // set uuid
        contentValue.put(InventoryContract.TagEntry.COLUMN_ID,
                GetLocationDataFragment.CONST_UUIDS[GetLocationDataFragment.BEACON_M_CHAR_UUID]);

        // set default name
        String name;
        name = "MASTER BEACON";
        contentValue.put(InventoryContract.TagEntry.COLUMN_NAME, name);

        // set active
        contentValue.put(InventoryContract.TagEntry.COLUMN_ACTIVE, false);

        // insert into table
        sqLiteDatabase.insert(
                InventoryContract.TagEntry.TABLE_NAME,
                null,
                contentValue
        );
    }

    // create initial data for tags on database startup
    public static void updateTagData(SQLiteDatabase sqLiteDatabase) {

        ContentValues contentValue = new ContentValues();

        sqLiteDatabase.delete(
                InventoryContract.TagEntry.TABLE_NAME,
                null,
                null
        );

        for (int i = 1; i <= 8; i++) {

            // set uuid
            contentValue.put(InventoryContract.TagEntry.COLUMN_ID,
                    GetLocationDataFragment.CONST_UUIDS[i]);

            // set default name
            String name = String.format(Locale.US, "TAG%2d", i);
            contentValue.put(InventoryContract.TagEntry.COLUMN_NAME, name);

            // set active
            contentValue.put(InventoryContract.TagEntry.COLUMN_ACTIVE, false);

            // insert into table
            sqLiteDatabase.insert(
                    InventoryContract.TagEntry.TABLE_NAME,
                    null,
                    contentValue
            );
        }

        for (int i = 9; i <= GetLocationDataFragment.NUM_BEACONS; i++) {
            // set uuid
            contentValue.put(InventoryContract.TagEntry.COLUMN_ID,
                    GetLocationDataFragment.CONST_UUIDS[i]);

            // set default name
            String name;
            if (i == 9) {
                name = "MASTER BEACON";
            } else {
                name = String.format(Locale.US, "BEACON%2d", i - 9);
            }
            contentValue.put(InventoryContract.TagEntry.COLUMN_NAME, name);

            // set active
            contentValue.put(InventoryContract.TagEntry.COLUMN_ACTIVE, false);

            // insert into table
            sqLiteDatabase.insert(
                    InventoryContract.TagEntry.TABLE_NAME,
                    null,
                    contentValue
            );
        }
    }

    // test database table capacities
    public static void testDatabase(Context context) {

        ContentResolver contentResolver = context.getContentResolver();

        // current
        // need: item key, qty, date received, donor, warehouse
        // 200k, 100k in each warehouse

        // create content values
        ArrayList<ContentValues> currentValues = new ArrayList<>();
        ContentValues currentValue1 = new ContentValues();
        ContentValues currentValue2 = new ContentValues();
        ContentValues[] current;

        // loop to add content values to list

        // bulk insert

        // past
        // barcode id, name, value, qty, date, donor
        // 200k

        // create content values
        ArrayList<ContentValues> pastValues = new ArrayList<>();
        ContentValues pastValue = new ContentValues();
        ContentValues[] past;

        // loop to add content values to list

        // bulk insert

        // receive
        // need: item key
        // 2k

        // create content values
        ArrayList<ContentValues> receiveValues = new ArrayList<>();
        ContentValues receiveValue = new ContentValues();
        ContentValues[] receive;

        // loop to add content values to list

        // bulk insert

        // ship
        // need: barcode id, name, value
        // 2k

        // create content values
        ArrayList<ContentValues> shipValues = new ArrayList<>();
        ContentValues shipValue = new ContentValues();
        ContentValues[] ship;

        // loop to add content values to list

        // bulk insert

    }
}
