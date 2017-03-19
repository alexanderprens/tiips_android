package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_reports;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;

import org.helpingkidsroundfirst.hkrf.data.InventoryDbHelper;
import org.helpingkidsroundfirst.hkrf.helper_classes.ApiClientAsyncTask;
import org.helpingkidsroundfirst.hkrf.helper_classes.BaseDemoActivity;
import org.helpingkidsroundfirst.hkrf.helper_classes.Utility;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by alexa on 3/18/2017.
 */

public class ExportDBToDriveActivity extends BaseDemoActivity {

    private final static String TAG = "ExportDBToDriveActivity";
    final ResultCallback<DriveFolder.DriveFileResult> fileCallBack = new ResultCallback<DriveFolder.DriveFileResult>() {
        @Override
        public void onResult(DriveFolder.DriveFileResult driveFileResult) {

            if (!driveFileResult.getStatus().isSuccess()) {
                Log.v(TAG, "Error while trying to create the file");
                return;
            }
            //Initialize mFile to be processed in AsyncTask
            DriveFile mfile = driveFileResult.getDriveFile();
            new EditContentsAsyncTask(ExportDBToDriveActivity.this).execute(mfile);

        }
    };
    final private ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        //if failure
                        showMessage("Error while trying to create new file contents");
                        return;
                    }

                    // get file name string
                    final Calendar c = Calendar.getInstance();
                    String fileName = "HKRF Database Backup ";
                    fileName += Utility.getDatePickerString(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                            c.get(Calendar.DATE));

                    //change the metadata of the file. by setting title, setMimeType.
                    String mimeType = MimeTypeMap.getSingleton().getExtensionFromMimeType("db");
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(fileName)
                            .setMimeType(mimeType)
                            .build();

                    // change data
                    Drive.DriveApi.getAppFolder(getGoogleApiClient())
                            .createFile(getGoogleApiClient(), changeSet, result.getDriveContents()).setResultCallback(fileCallBack);

                }

            };

    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);


        // create new contents resource.
        Drive.DriveApi.newDriveContents(getGoogleApiClient()).setResultCallback(driveContentsCallback);
    }

    private class EditContentsAsyncTask extends ApiClientAsyncTask<DriveFile, Void, Boolean> {

        private EditContentsAsyncTask(Context context) {
            super(context);
        }

        @Override
        protected Boolean doInBackgroundConnected(DriveFile... args) {
            DriveFile file = args[0];
            try {

                DriveApi.DriveContentsResult driveContentsResult = file.open(
                        getGoogleApiClient(), DriveFile.MODE_WRITE_ONLY, null).await();


                if (!driveContentsResult.getStatus().isSuccess()) {
                    return false;
                }

                DriveContents driveContents = driveContentsResult.getDriveContents();


                //edit the outputStream
                InventoryDbHelper db = new InventoryDbHelper(ExportDBToDriveActivity.this);
                String inFileName = getApplicationContext().getDatabasePath(db.getDatabaseName()).getPath();
                FileInputStream is = new FileInputStream(inFileName);
                BufferedInputStream in = new BufferedInputStream(is);
                byte[] buffer = new byte[8 * 1024];

                BufferedOutputStream out = new BufferedOutputStream(driveContents.getOutputStream());
                int n;
                while ((n = in.read(buffer)) > 0) {
                    out.write(buffer, 0, n);
                }
                in.close();


                com.google.android.gms.common.api.Status status =
                        driveContents.commit(getGoogleApiClient(), null).await();
                return status.getStatus().isSuccess();
            } catch (IOException e) {
                Log.e(TAG, "IOException while appending to the output stream", e);
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                showMessage("Error while editing contents");
                return;
            }
            showMessage("Successfully edited contents");
        }
    }
}