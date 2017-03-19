package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_reports;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;

import org.helpingkidsroundfirst.hkrf.data.InventoryDbHelper;
import org.helpingkidsroundfirst.hkrf.helper_classes.BaseDemoActivity;
import org.helpingkidsroundfirst.hkrf.helper_classes.Utility;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.util.Calendar;

/**
 * Created by alexa on 3/18/2017.
 */

public class ExportDBToDriveActivity extends BaseDemoActivity {

    private final static String TAG = "ExportDBToDriveActivity";
    final ResultCallback<DriveFolder.DriveFileResult> fileCallBack =
            new ResultCallback<DriveFolder.DriveFileResult>() {
        @Override
        public void onResult(@NonNull DriveFolder.DriveFileResult driveFileResult) {

            if (!driveFileResult.getStatus().isSuccess()) {
                Log.v(TAG, "Error while trying to create the file");
                finish();
                return;
            }
            showMessage("Successfully uploaded database");
            finish();
        }
    };
    final private ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(@NonNull DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        //if failure
                        showMessage("Error while trying to create new file contents");
                        return;
                    }

                    // get drive contents
                    final DriveContents driveContents = result.getDriveContents();

                    new Thread() {
                @Override
                public void run() {
                    // write contents of database to drive
                    try {
                        InventoryDbHelper db = new InventoryDbHelper(ExportDBToDriveActivity.this);
                        String inFileName = getApplicationContext().getDatabasePath(db.getDatabaseName()).getPath();
                        FileInputStream is = new FileInputStream(inFileName);
                        BufferedInputStream in = new BufferedInputStream(is);
                        byte[] buffer = new byte[4 * 1024];

                        BufferedOutputStream out = new BufferedOutputStream(driveContents.getOutputStream());
                        int n;
                        while ((n = in.read(buffer)) > 0) {
                            out.write(buffer, 0, n);
                            out.flush();
                        }
                        in.close();
                    } catch (java.io.IOException e) {
                        Log.e(TAG, "Error writing db file");
                    }

                    // get file name string
                    final Calendar c = Calendar.getInstance();
                    String fileName = "HKRF Database Backup ";
                    fileName += Utility.getDatePickerString(c.get(Calendar.YEAR),
                            c.get(Calendar.MONTH),
                            c.get(Calendar.DATE));

                    //change the metadata of the file. by setting title, setMimeType.
                    String mimeType = "application/x-sqlite3";   // sqlite mime type
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(fileName)
                            .setMimeType(mimeType)
                            .build();

                    // change data
                    Drive.DriveApi.getRootFolder(getGoogleApiClient())
                            .createFile(getGoogleApiClient(), changeSet, driveContents)
                            .setResultCallback(fileCallBack);
                }
                    }.start();
                }
            };
    private ProgressDialog progressDialog;

    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);

        // create new contents resource.
        Drive.DriveApi.newDriveContents(getGoogleApiClient()).setResultCallback(driveContentsCallback);
    }

    @Override
    public void onStart() {
        super.onStart();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Backing up database");
        progressDialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();

        progressDialog.hide();
    }
}