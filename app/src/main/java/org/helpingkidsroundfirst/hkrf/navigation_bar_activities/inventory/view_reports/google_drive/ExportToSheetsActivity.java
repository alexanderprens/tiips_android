package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_reports.google_drive;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AddSheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.Border;
import com.google.api.services.sheets.v4.model.Borders;
import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.CellFormat;
import com.google.api.services.sheets.v4.model.DeleteDimensionRequest;
import com.google.api.services.sheets.v4.model.DeleteSheetRequest;
import com.google.api.services.sheets.v4.model.DimensionRange;
import com.google.api.services.sheets.v4.model.GridProperties;
import com.google.api.services.sheets.v4.model.GridRange;
import com.google.api.services.sheets.v4.model.RepeatCellRequest;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
import com.google.api.services.sheets.v4.model.TextFormat;
import com.google.api.services.sheets.v4.model.UpdateSheetPropertiesRequest;
import com.google.api.services.sheets.v4.model.ValueRange;

import org.helpingkidsroundfirst.hkrf.R;
import org.helpingkidsroundfirst.hkrf.data.InventoryContract;
import org.helpingkidsroundfirst.hkrf.helper_classes.Utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by alexa on 3/18/2017.
 */

public class ExportToSheetsActivity extends Activity implements
        EasyPermissions.PermissionCallbacks {

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    private static final String[] SCOPES = {SheetsScopes.SPREADSHEETS};
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String TAG = "ExportToSheetsActivity";
    GoogleAccountCredential mCredential;
    ProgressDialog mProgress;
    private Context mContext;

    /**
     * Create the main activity.
     *
     * @param savedInstanceState previously saved instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get context
        mContext = this;

        // start progress
        mProgress = new ProgressDialog(this);
        mProgress.setMessage(getResources().getString(R.string.export_sheets_progress));

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        // start working on sheets
        getResultsFromApi();
    }


    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private void getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
            Toast.makeText(this, getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
        } else {
            new ExportToSheetsTask(mCredential).execute();
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    getResources().getString(R.string.permissions_message),
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     *
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode  code indicating the result of the incoming
     *                    activity result.
     * @param data        Intent (containing result data) returned by incoming
     *                    activity result.
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Toast.makeText(this, getResources().getString(R.string.google_play),
                            Toast.LENGTH_LONG).show();
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     *
     * @param requestCode  The request code passed in
     *                     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     *
     * @param requestCode The request code associated with the requested
     *                    permission
     * @param list        The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     *
     * @param requestCode The request code associated with the requested
     *                    permission
     * @param list        The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Checks whether the device currently has a network connection.
     *
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     *
     * @return true if Google Play Services is available and up to
     * date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     *
     * @param connectionStatusCode code describing the presence (or lack of)
     *                             Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                ExportToSheetsActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    private class ExportToSheetsTask extends AsyncTask<Void, Void, Long> {
        private static final int CURRENT_ID = 1;
        private static final int INVENTORY_ID = 1;
        private static final int PAST_ID = 1;
        private static final int CURRENT_COL_COUNT = 9;
        private static final int PAST_COL_COUNT = 8;
        private static final int ITEM_COL_COUNT = 5;
        private static final String MAJOR_DIMENSION = "ROWS";
        private static final int DATA_CHUNK_SIZE = 25000;
        private com.google.api.services.sheets.v4.Sheets mService = null;
        private Exception mLastError;
        private String currentSpreadSheetId, pastSpreadSheetId, itemSpreadSheetId;
        private int currentRowCount;
        private int pastRowCount;

        ExportToSheetsTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.sheets.v4.Sheets.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName(mContext.getResources().getString(R.string.app_name))
                    .build();
        }

        @Override
        protected Long doInBackground(Void... params) {
            Looper.prepare();

            // attempt to create spreadsheet
            try {
                createSpreadsheet();
            } catch (java.io.IOException e) {
                mLastError = e;
                cancel(true);
                return null;
            }

            // attempt to rename inventory item sheet
            try {
                createSheets();
            } catch (java.io.IOException e) {
                mLastError = e;
                cancel(true);
                return null;
            }

            // attempt to upload data
            if (!getItemData()) {
                cancel(true);
                return null;
            }

            if (!getCurrentData()) {
                cancel(true);
                return null;
            }

            if (!getPastData()) {
                cancel(true);
                return null;
            }

            // attempt to format spreadsheets
            try {
                formatSheets();
            } catch (java.io.IOException e) {
                mLastError = e;
                cancel(true);
                return null;
            }

            return null;
        }

        private void createSpreadsheet() throws java.io.IOException {
            Spreadsheet spreadsheet = new Spreadsheet();
            SpreadsheetProperties properties = new SpreadsheetProperties();

            // get current file name string
            final Calendar c = Calendar.getInstance();
            String fileName = mContext.getResources().getString(R.string.current_sheets_file_name) + " ";
            fileName += Utility.getDatePickerString(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                    c.get(Calendar.DATE));
            properties.setTitle(fileName);
            spreadsheet.setProperties(properties);

            // attempt to create current spreadsheet
            spreadsheet = mService.spreadsheets().create(spreadsheet).execute();
            currentSpreadSheetId = spreadsheet.getSpreadsheetId();

            // get past file name string
            fileName = mContext.getResources().getString(R.string.past_sheets_file_name) + " ";
            fileName += Utility.getDatePickerString(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                    c.get(Calendar.DATE));
            properties.setTitle(fileName);
            spreadsheet.setProperties(properties);

            // attempt to create past spreadsheet
            spreadsheet = mService.spreadsheets().create(spreadsheet).execute();
            pastSpreadSheetId = spreadsheet.getSpreadsheetId();

            // get item file name string
            fileName = mContext.getResources().getString(R.string.item_sheets_file_name) + " ";
            fileName += Utility.getDatePickerString(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                    c.get(Calendar.DATE));
            properties.setTitle(fileName);
            spreadsheet.setProperties(properties);

            // attempt to create spreadsheet
            spreadsheet = mService.spreadsheets().create(spreadsheet).execute();
            itemSpreadSheetId = spreadsheet.getSpreadsheetId();
        }

        private void createSheets() throws java.io.IOException {
            List<Request> requests = new ArrayList<>();

            // current inventory
            requests.add(new Request()
                    .setAddSheet(new AddSheetRequest().setProperties(
                            new SheetProperties().setTitle(mContext.getResources().getString(R.string.tab_current))
                                    .setSheetId(CURRENT_ID)))
            );

            // current
            requests.add(new Request()
                    .setDeleteDimension(new DeleteDimensionRequest().setRange(
                            new DimensionRange().setSheetId(CURRENT_ID)
                                    .setDimension("COLUMNS")
                                    .setStartIndex(CURRENT_COL_COUNT)
                                    .setEndIndex(25))
                    ));

            // delete default sheet
            requests.add(new Request()
                    .setDeleteSheet(new DeleteSheetRequest().setSheetId(0))
            );

            BatchUpdateSpreadsheetRequest body =
                    new BatchUpdateSpreadsheetRequest().setRequests(requests);
            mService.spreadsheets().batchUpdate(currentSpreadSheetId, body).execute();

            // barcode items
            requests.clear();
            requests.add(new Request()
                    .setAddSheet(new AddSheetRequest().setProperties(
                            new SheetProperties().setTitle(mContext.getResources().getString(R.string.tab_barcode))
                                    .setSheetId(INVENTORY_ID)))
            );

            // items
            requests.add(new Request()
                    .setDeleteDimension(new DeleteDimensionRequest().setRange(
                            new DimensionRange().setSheetId(INVENTORY_ID)
                                    .setDimension("COLUMNS")
                                    .setStartIndex(ITEM_COL_COUNT)
                                    .setEndIndex(25))
                    ));

            // delete default sheet
            requests.add(new Request()
                    .setDeleteSheet(new DeleteSheetRequest().setSheetId(0))
            );

            body = new BatchUpdateSpreadsheetRequest().setRequests(requests);
            mService.spreadsheets().batchUpdate(itemSpreadSheetId, body).execute();

            // past inventory
            requests.clear();
            requests.add(new Request()
                    .setAddSheet(new AddSheetRequest().setProperties(
                            new SheetProperties().setTitle(mContext.getResources().getString(R.string.tab_past))
                                    .setSheetId(PAST_ID)))
            );

            // past
            requests.add(new Request()
                    .setDeleteDimension(new DeleteDimensionRequest().setRange(
                            new DimensionRange().setSheetId(PAST_ID)
                                    .setDimension("COLUMNS")
                                    .setStartIndex(PAST_COL_COUNT)
                                    .setEndIndex(25))
                    ));

            // delete default sheet
            requests.add(new Request()
                    .setDeleteSheet(new DeleteSheetRequest().setSheetId(0))
            );

            body = new BatchUpdateSpreadsheetRequest().setRequests(requests);
            mService.spreadsheets().batchUpdate(pastSpreadSheetId, body).execute();
        }

        private boolean uploadSmallData(ValueRange valueRange, String range, String spreadSheetId) {

            try {
                Sheets.Spreadsheets.Values.Update request = mService.spreadsheets().values().update(
                        spreadSheetId,
                        range,
                        valueRange
                );
                request.setValueInputOption("RAW");
                request.execute();

            } catch (java.io.IOException e) {
                mLastError = e;
                return false;
            }

            return true;
        }

        private boolean getCurrentData() {
            // create header row
            final String[] header = mContext.getResources().getStringArray(R.array.current_inventory_sheet_header);

            // get cursor of values
            final String[] projection = {
                    InventoryContract.ItemEntry.COLUMN_BARCODE_ID,
                    InventoryContract.ItemEntry.COLUMN_NAME,
                    InventoryContract.ItemEntry.COLUMN_DESCRIPTION,
                    InventoryContract.ItemEntry.COLUMN_VALUE,
                    InventoryContract.CurrentInventoryEntry.COLUMN_QTY,
                    InventoryContract.CurrentInventoryEntry.COLUMN_DATE_RECEIVED,
                    InventoryContract.CurrentInventoryEntry.COLUMN_DONOR,
                    InventoryContract.CurrentInventoryEntry.COLUMN_WAREHOUSE,
                    InventoryContract.CategoryEntry.COLUMN_CATEGORY,
                    InventoryContract.CurrentInventoryEntry.TABLE_NAME + "." +
                            InventoryContract.CurrentInventoryEntry._ID + " AS _id"
            };

            Cursor cursor = mContext.getContentResolver().query(
                    InventoryContract.CurrentInventoryEntry.buildCurrentInventoryUri(),
                    projection,
                    null,
                    null,
                    InventoryContract.ItemEntry.COLUMN_NAME
            );

            // get list of values
            List<List<Object>> values = new ArrayList<>();
            List<Object> headerValues = new ArrayList<>();

            // get row values
            for (int i = 0; i < projection.length - 1; i++) {
                headerValues.add(header[i]);
            }

            // add row to list
            int numValues = 1;
            int rowNum = 1;
            values.add(headerValues);

            // create api valueRange
            String range = mContext.getResources().getString(R.string.tab_current) + "!A" +
                    String.format(Locale.US, "%d", rowNum);
            ValueRange valueRange = new ValueRange();
            valueRange.setMajorDimension(MAJOR_DIMENSION);
            valueRange.setRange(range);

            if (cursor != null && cursor.moveToFirst()) {

                // get all values in current inventory
                do {
                    List<Object> row = new ArrayList<>();

                    // get row values
                    for (int i = 0; i < projection.length - 1; i++) {
                        row.add(cursor.getString(i));
                    }

                    // add row to list
                    numValues++;
                    values.add(row);
                    if (numValues > DATA_CHUNK_SIZE) {
                        range = mContext.getResources().getString(R.string.tab_current) + "!A" +
                                String.format(Locale.US, "%d", rowNum);
                        valueRange.setRange(range);
                        valueRange.setValues(values);
                        if (!uploadSmallData(valueRange, range, currentSpreadSheetId)) {
                            return false;
                        }
                        values.clear();
                        numValues = 0;
                        rowNum += DATA_CHUNK_SIZE;
                    }

                } while (cursor.moveToNext());
                cursor.close();
            }

            if (!values.isEmpty()) {
                range = mContext.getResources().getString(R.string.tab_current) + "!A" +
                        String.format(Locale.US, "%d", rowNum);
                valueRange.setRange(range);
                valueRange.setValues(values);
                if (!uploadSmallData(valueRange, range, currentSpreadSheetId)) {
                    return false;
                }
            }

            currentRowCount = values.size();
            return true;
        }

        private boolean getPastData() {
            // create header row
            final String[] header = mContext.getResources().getStringArray(R.array.past_inventory_sheet_header);

            // get cursor of values
            final String[] projection = {
                    InventoryContract.PastInventoryEntry.COLUMN_BARCODE_ID,
                    InventoryContract.PastInventoryEntry.COLUMN_NAME,
                    InventoryContract.PastInventoryEntry.COLUMN_DESCRIPTION,
                    InventoryContract.PastInventoryEntry.COLUMN_VALUE,
                    InventoryContract.PastInventoryEntry.COLUMN_QTY,
                    InventoryContract.PastInventoryEntry.COLUMN_DATE_SHIPPED,
                    InventoryContract.PastInventoryEntry.COLUMN_DONOR,
                    InventoryContract.CategoryEntry.COLUMN_CATEGORY,
                    InventoryContract.PastInventoryEntry.TABLE_NAME + "." +
                            InventoryContract.PastInventoryEntry._ID + " AS _id"
            };

            Cursor cursor = mContext.getContentResolver().query(
                    InventoryContract.PastInventoryEntry.buildPastInventoryUri(),
                    projection,
                    null,
                    null,
                    InventoryContract.PastInventoryEntry.COLUMN_NAME
            );

            // get list of values
            List<List<Object>> values = new ArrayList<>();
            List<Object> headerValues = new ArrayList<>();

            // get row values
            for (int i = 0; i < projection.length - 1; i++) {
                headerValues.add(header[i]);
            }

            // add row to list
            int numValues = 1;
            int rowNum = 1;
            values.add(headerValues);

            // create api valueRange
            String range = mContext.getResources().getString(R.string.tab_past) + "!A1";
            ValueRange valueRange = new ValueRange();
            valueRange.setMajorDimension(MAJOR_DIMENSION);
            valueRange.setRange(range);

            if (cursor != null && cursor.moveToFirst()) {

                // get all values in current inventory
                do {
                    List<Object> row = new ArrayList<>();

                    // get row values
                    for (int i = 0; i < projection.length - 1; i++) {
                        row.add(cursor.getString(i));
                    }

                    // add row to list
                    numValues++;
                    values.add(row);
                    if (numValues > DATA_CHUNK_SIZE) {
                        range = mContext.getResources().getString(R.string.tab_past) + "!A" +
                                String.format(Locale.US, "%d", rowNum);
                        valueRange.setRange(range);
                        valueRange.setValues(values);
                        if (!uploadSmallData(valueRange, range, pastSpreadSheetId)) {
                            return false;
                        }
                        values.clear();
                        numValues = 0;
                        rowNum += DATA_CHUNK_SIZE;
                    }

                } while (cursor.moveToNext());
                cursor.close();
            }

            if (!values.isEmpty()) {
                range = mContext.getResources().getString(R.string.tab_past) + "!A" +
                        String.format(Locale.US, "%d", rowNum);
                valueRange.setRange(range);
                valueRange.setValues(values);
                if (!uploadSmallData(valueRange, range, pastSpreadSheetId)) {
                    return false;
                }
            }

            pastRowCount = values.size();
            return true;
        }

        private boolean getItemData() {
            // create header row
            final String[] header = mContext.getResources().getStringArray(R.array.item_inventory_sheet_header);

            // get cursor of values
            final String[] projection = {
                    InventoryContract.ItemEntry.COLUMN_BARCODE_ID,
                    InventoryContract.ItemEntry.COLUMN_NAME,
                    InventoryContract.ItemEntry.COLUMN_DESCRIPTION,
                    InventoryContract.ItemEntry.COLUMN_VALUE,
                    InventoryContract.CategoryEntry.COLUMN_CATEGORY,
                    InventoryContract.ItemEntry.TABLE_NAME + "." +
                            InventoryContract.ItemEntry._ID + " AS _id"
            };

            Cursor cursor = mContext.getContentResolver().query(
                    InventoryContract.ItemEntry.buildInventoryItemUri(),
                    projection,
                    null,
                    null,
                    InventoryContract.ItemEntry.COLUMN_NAME
            );

            // get list of values
            List<List<Object>> values = new ArrayList<>();
            List<Object> headerValues = new ArrayList<>();

            // get row values
            for (int i = 0; i < projection.length - 1; i++) {
                headerValues.add(header[i]);
            }

            // add row to list
            values.add(headerValues);

            if (cursor != null && cursor.moveToFirst()) {

                // get all values in current inventory
                do {
                    List<Object> row = new ArrayList<>();

                    // get row values
                    for (int i = 0; i < projection.length - 1; i++) {
                        row.add(cursor.getString(i));
                    }

                    // add row to list
                    values.add(row);
                } while (cursor.moveToNext());
                cursor.close();
            }

            // create api valueRange
            String range = mContext.getResources().getString(R.string.tab_barcode) + "!A1";
            ValueRange valueRange = new ValueRange();
            valueRange.setMajorDimension(MAJOR_DIMENSION);
            valueRange.setRange(range);
            valueRange.setValues(values);
            if (!uploadSmallData(valueRange, range, itemSpreadSheetId)) {
                return false;
            }
            return true;
        }

        private void formatSheets() throws java.io.IOException {
            final String BORDER_SOLID = "SOLID";
            final int BORDER_WIDTH = 1;
            final Border BORDER_DEFAULT = new Border()
                    .setStyle(BORDER_SOLID)
                    .setWidth(BORDER_WIDTH);

            List<Request> requests = new ArrayList<>();

            // update header row -------------------------------------------------------------------
            final String ALIGNMENT = "CENTER";
            final CellData HEADER_CELL = new CellData()
                    .setUserEnteredFormat(new CellFormat()
                            .setHorizontalAlignment(ALIGNMENT)
                            .setTextFormat(new TextFormat()
                                    .setBold(true)));
            // current inv
            requests.add(new Request()
                    .setRepeatCell(new RepeatCellRequest()
                            .setRange(new GridRange()
                                    .setSheetId(CURRENT_ID)
                                    .setStartRowIndex(0)
                                    .setEndRowIndex(1))
                            .setCell(HEADER_CELL)
                            .setFields("userEnteredFormat(textFormat,horizontalAlignment)")));

            requests.add(new Request()
                    .setUpdateSheetProperties(new UpdateSheetPropertiesRequest()
                            .setProperties(new SheetProperties().setSheetId(CURRENT_ID)
                                    .setGridProperties(new GridProperties()
                                            .setFrozenRowCount(1)))
                            .setFields("gridProperties.frozenRowCount")));

            final String WRAP_STRATEGY = "WRAP";
            final CellData ALL_CELLS = new CellData()
                    .setUserEnteredFormat(new CellFormat()
                            .setWrapStrategy(WRAP_STRATEGY)
                            .setBorders(new Borders()
                                    .setTop(BORDER_DEFAULT)
                                    .setBottom(BORDER_DEFAULT)
                                    .setLeft(BORDER_DEFAULT)
                                    .setRight(BORDER_DEFAULT)));

            // current inv
            requests.add(new Request()
                    .setRepeatCell(new RepeatCellRequest()
                            .setRange(new GridRange()
                                    .setSheetId(CURRENT_ID)
                                    .setStartColumnIndex(0)
                                    .setStartRowIndex(0)
                                    .setEndColumnIndex(CURRENT_COL_COUNT)
                                    .setEndRowIndex(currentRowCount)
                            )
                            .setCell(ALL_CELLS)
                            .setFields("userEnteredFormat(wrapStrategy,borders)")));

            BatchUpdateSpreadsheetRequest body =
                    new BatchUpdateSpreadsheetRequest().setRequests(requests);
            mService.spreadsheets().batchUpdate(currentSpreadSheetId, body).execute();

            // past inv
            requests.clear();
            requests.add(new Request()
                    .setRepeatCell(new RepeatCellRequest()
                            .setRange(new GridRange()
                                    .setSheetId(PAST_ID)
                                    .setStartRowIndex(0)
                                    .setEndRowIndex(1))
                            .setCell(HEADER_CELL)
                            .setFields("userEnteredFormat(textFormat,horizontalAlignment)")));

            requests.add(new Request()
                    .setUpdateSheetProperties(new UpdateSheetPropertiesRequest()
                            .setProperties(new SheetProperties().setSheetId(PAST_ID)
                                    .setGridProperties(new GridProperties()
                                            .setFrozenRowCount(1)))
                            .setFields("gridProperties.frozenRowCount")));

            // past inv
            requests.add(new Request()
                    .setRepeatCell(new RepeatCellRequest()
                            .setRange(new GridRange()
                                    .setSheetId(PAST_ID)
                                    .setStartColumnIndex(0)
                                    .setStartRowIndex(0)
                                    .setEndColumnIndex(PAST_COL_COUNT)
                                    .setEndRowIndex(pastRowCount)
                            )
                            .setCell(ALL_CELLS)
                            .setFields("userEnteredFormat(wrapStrategy,borders)")));

            body = new BatchUpdateSpreadsheetRequest().setRequests(requests);
            mService.spreadsheets().batchUpdate(pastSpreadSheetId, body).execute();

            // item inv
            requests.clear();
            requests.add(new Request()
                    .setRepeatCell(new RepeatCellRequest()
                            .setRange(new GridRange()
                                    .setSheetId(INVENTORY_ID)
                                    .setStartRowIndex(0)
                                    .setEndRowIndex(1))
                            .setCell(HEADER_CELL)
                            .setFields("userEnteredFormat(textFormat,horizontalAlignment)")));

            requests.add(new Request()
                    .setUpdateSheetProperties(new UpdateSheetPropertiesRequest()
                            .setProperties(new SheetProperties().setSheetId(INVENTORY_ID)
                                    .setGridProperties(new GridProperties()
                                            .setFrozenRowCount(1)))
                            .setFields("gridProperties.frozenRowCount")));

            // barcode inv
            requests.add(new Request()
                    .setRepeatCell(new RepeatCellRequest()
                            .setRange(new GridRange()
                                    .setSheetId(INVENTORY_ID)
                                    .setStartColumnIndex(0)
                                    .setStartRowIndex(0)
                                    .setEndColumnIndex(ITEM_COL_COUNT)
                                    .setEndRowIndex(pastRowCount)
                            )
                            .setCell(ALL_CELLS)
                            .setFields("userEnteredFormat(wrapStrategy,borders)")));

            body = new BatchUpdateSpreadsheetRequest().setRequests(requests);
            mService.spreadsheets().batchUpdate(itemSpreadSheetId, body).execute();
        }

        protected void onPreExecute() {
            mProgress.show();
        }

        protected void onPostExecute(Long result) {
            finish();
        }

        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            ExportToSheetsActivity.REQUEST_AUTHORIZATION);
                } else {
                    Log.e(TAG, mLastError.getMessage());
                }
            } else {
                Log.e(TAG, "Cancelled with no error");
            }

            finish();
        }
    }
}
