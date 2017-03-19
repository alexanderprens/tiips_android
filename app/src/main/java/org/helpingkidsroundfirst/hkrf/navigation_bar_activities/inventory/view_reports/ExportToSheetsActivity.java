package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_reports;

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
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse;
import com.google.api.services.sheets.v4.model.DeleteSheetRequest;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
import com.google.api.services.sheets.v4.model.ValueRange;

import org.helpingkidsroundfirst.hkrf.R;
import org.helpingkidsroundfirst.hkrf.Utility;
import org.helpingkidsroundfirst.hkrf.data.InventoryContract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

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
        mProgress.setMessage("Exporting to sheets");

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
            Toast.makeText(this, "No network connection available.", Toast.LENGTH_LONG).show();
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
                    "This app needs to access your Google account (via Contacts).",
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
                    Toast.makeText(this, "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.",
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
        private static final int INVENTORY_ID = 3;
        private static final int PAST_ID = 2;
        private com.google.api.services.sheets.v4.Sheets mService = null;
        private Exception mLastError;
        private String spreadSheetId;

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
                updatedSheets();
            } catch (java.io.IOException e) {
                mLastError = e;
                cancel(true);
                return null;
            }

            // get data
            List<ValueRange> valueRanges = new ArrayList<>();
            valueRanges.add(getCurrentData());
            valueRanges.add(getPastData());
            valueRanges.add(getItemData());

            // attempt to upload data
            try {
                uploadData(valueRanges);
            } catch (java.io.IOException e) {
                mLastError = e;
                cancel(true);
                return null;
            }

            // attempt to format spreadsheets


            return null;
        }

        private void createSpreadsheet() throws java.io.IOException {
            Spreadsheet spreadsheet = new Spreadsheet();
            SpreadsheetProperties properties = new SpreadsheetProperties();

            // get file name string
            final Calendar c = Calendar.getInstance();
            String fileName = "HKRF Inventory ";
            fileName += Utility.getDatePickerString(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                    c.get(Calendar.DATE));
            properties.setTitle(fileName);
            spreadsheet.setProperties(properties);

            // attempt to create spreadsheet
            spreadsheet = mService.spreadsheets().create(spreadsheet).execute();
            spreadSheetId = spreadsheet.getSpreadsheetId();
        }

        private void updatedSheets() throws java.io.IOException {
            List<Request> requests = new ArrayList<>();

            // barcode items
            requests.add(new Request()
                    .setAddSheet(new AddSheetRequest().setProperties(
                            new SheetProperties().setTitle("Barcode Items")
                                    .setSheetId(INVENTORY_ID)))
            );

            // current inventory
            requests.add(new Request()
                    .setAddSheet(new AddSheetRequest().setProperties(
                            new SheetProperties().setTitle("Current Inventory")
                                    .setSheetId(CURRENT_ID)))
            );

            // past inventory
            requests.add(new Request()
                    .setAddSheet(new AddSheetRequest().setProperties(
                            new SheetProperties().setTitle("Past Inventory")
                                    .setSheetId(PAST_ID)))
            );

            // delete default sheet
            requests.add(new Request()
                    .setDeleteSheet(new DeleteSheetRequest().setSheetId(0))
            );

            BatchUpdateSpreadsheetRequest body =
                    new BatchUpdateSpreadsheetRequest().setRequests(requests);
            mService.spreadsheets().batchUpdate(spreadSheetId, body).execute();
        }

        private ValueRange getCurrentData() {
            // create header row
            final String[] header = {"Barcode #", "Name", "Description", "Value", "Quantity",
                    "Date Received", "Donor", "Warehouse", "Category"
            };

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
            String range = "Current Inventory" + "!A1";
            ValueRange valueRange = new ValueRange();
            valueRange.setMajorDimension("ROWS");
            valueRange.setRange(range);
            valueRange.setValues(values);
            return valueRange;
        }

        private ValueRange getPastData() {
            // create header row
            final String[] header = {"Barcode #", "Name", "Description", "Value", "Quantity",
                    "Date Shipped", "Donor", "Category"
            };

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
            String range = "Past Inventory" + "!A1";
            ValueRange valueRange = new ValueRange();
            valueRange.setMajorDimension("ROWS");
            valueRange.setRange(range);
            valueRange.setValues(values);
            return valueRange;
        }

        private ValueRange getItemData() {
            // create header row
            final String[] header = {"Barcode #", "Name", "Description", "Value", "Category"};

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
            String range = "Barcode Items" + "!A1";
            ValueRange valueRange = new ValueRange();
            valueRange.setMajorDimension("ROWS");
            valueRange.setRange(range);
            valueRange.setValues(values);
            return valueRange;
        }

        private void uploadData(List<ValueRange> inputRange) throws java.io.IOException {

            // create request
            BatchUpdateValuesRequest requestBody = new BatchUpdateValuesRequest();
            requestBody.setValueInputOption("RAW");
            requestBody.setData(inputRange);

            Sheets.Spreadsheets.Values.BatchUpdate request = mService.spreadsheets().values()
                    .batchUpdate(spreadSheetId, requestBody);

            BatchUpdateValuesResponse response = request.execute();
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
