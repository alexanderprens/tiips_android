package org.helpingkidsroundfirst.hkrf.helper_classes;

/**
 * Created by alexa on 2/12/2017.
 */

public class Utility {
    // convert date from date picker to string date
    public static String getDatePickerString(int year, int month, int day) {
        String date = "%04d" + "-" + "%02d" + "-" + "%02d";
        return String.format(date, year, month + 1, day);
    }
}
