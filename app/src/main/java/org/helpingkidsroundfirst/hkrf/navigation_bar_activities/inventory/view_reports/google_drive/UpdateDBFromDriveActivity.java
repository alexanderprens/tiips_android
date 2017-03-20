package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_reports.google_drive;

/**
 * Created by alexa on 3/19/2017.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi.DriveContentsResult;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityBuilder;

import org.helpingkidsroundfirst.hkrf.R;
import org.helpingkidsroundfirst.hkrf.data.InventoryDbHelper;
import org.helpingkidsroundfirst.hkrf.helper_classes.ApiClientAsyncTask;
import org.helpingkidsroundfirst.hkrf.helper_classes.BaseDemoActivity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Activity to illustrate how to retrieve and read file contents.
 */
public class UpdateDBFromDriveActivity extends BaseDemoActivity {

    private static final String TAG = "UpdateDBFromActivity";
    private static final int REQUEST_CODE_OPENER = 1;
    private Context mContext;
    private ProgressDialog progressDialog;

    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);
        mContext = UpdateDBFromDriveActivity.this;

        IntentSender intentSender = Drive.DriveApi
                .newOpenFileActivityBuilder()
                .setMimeType(new String[]{getResources().getString(R.string.sqlite_mime_type)})
                .build(getGoogleApiClient());
        try {
            startIntentSenderForResult(
                    intentSender, REQUEST_CODE_OPENER, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            Log.w(TAG, "Unable to send intent", e);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.import_database_progress));
        progressDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        progressDialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();

        progressDialog.hide();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_OPENER:
                if (resultCode == RESULT_OK) {
                    DriveId driveId = data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);

                    new RetrieveDriveFileContentsAsyncTask(
                            UpdateDBFromDriveActivity.this).execute(driveId);
                }
                finish();
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    final private class RetrieveDriveFileContentsAsyncTask
            extends ApiClientAsyncTask<DriveId, Boolean, Boolean> {

        private RetrieveDriveFileContentsAsyncTask(Context context) {
            super(context);
        }

        @Override
        protected Boolean doInBackgroundConnected(DriveId... params) {
            DriveFile file = params[0].asDriveFile();
            DriveContentsResult driveContentsResult =
                    file.open(getGoogleApiClient(), DriveFile.MODE_READ_ONLY, null).await();
            if (!driveContentsResult.getStatus().isSuccess()) {
                return null;
            }

            DriveContents driveContents = driveContentsResult.getDriveContents();

            try {
                // input from google drive
                BufferedInputStream in = new BufferedInputStream(driveContents.getInputStream());


                // output to database
                InventoryDbHelper db = new InventoryDbHelper(mContext);
                String outFileName = getApplicationContext().getDatabasePath(db.getDatabaseName()).getPath();
                FileOutputStream os = new FileOutputStream(outFileName);
                BufferedOutputStream out = new BufferedOutputStream(os);

                // buffer
                byte[] buffer = new byte[4 * 1024];
                int n;

                while ((n = in.read(buffer)) > 0) {
                    out.write(buffer, 0, n);
                    out.flush();
                }
                in.close();
                out.close();
                return true;
            } catch (IOException e) {
                Log.e(TAG, "IOException while reading from the stream", e);
            }

            driveContents.discard(getGoogleApiClient());
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (!result) {
                Log.e(TAG, "Error while reading from the file");
                showMessage(getResources().getString(R.string.import_database_error));
                finish();
                return;
            }
            showMessage(getResources().getString(R.string.import_database_success));
            finish();
        }
    }
}
