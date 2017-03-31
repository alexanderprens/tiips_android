package org.helpingkidsroundfirst.hkrf.helper_classes;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import org.helpingkidsroundfirst.hkrf.data.InventoryContract;
import org.helpingkidsroundfirst.hkrf.navigation_bar_activities.ips.locate_item.scan_devices.ScanBLEDevicesFragment;

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

        for (int i = 1; i <= ScanBLEDevicesFragment.NUM_BEACONS; i++) {
            ContentValues contentValue = new ContentValues();

            // set uuid
            contentValue.put(InventoryContract.TagEntry.COLUMN_ID,
                    ScanBLEDevicesFragment.CONST_UUIDS[i]);

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
    }
}
