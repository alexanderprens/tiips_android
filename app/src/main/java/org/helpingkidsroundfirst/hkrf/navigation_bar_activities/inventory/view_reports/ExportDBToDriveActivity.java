package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_reports;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;

import org.helpingkidsroundfirst.hkrf.BaseDemoActivity;
import org.helpingkidsroundfirst.hkrf.Utility;

import java.util.Calendar;

/**
 * Created by alexa on 3/18/2017.
 */

public class ExportDBToDriveActivity extends BaseDemoActivity {

    private String fileName;
    private DriveFile driveFile;
    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
            ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(@NonNull DriveFolder.DriveFileResult driveFileResult) {
                    if (!driveFileResult.getStatus().isSuccess()) {
                        showMessage("Error while trying to create the file");
                        return;
                    }

                    driveFile = driveFileResult.getDriveFile();
                    showMessage("Created file: " + fileName);
                }
            };

    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);

        // get file name string
        final Calendar c = Calendar.getInstance();
        fileName = "HKRF Inventory ";
        fileName += Utility.getDatePickerString(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                c.get(Calendar.DATE));

        // create new file
        // make file meta data
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle(fileName)
                .setMimeType("application/vnd.google-apps.spreadsheet") // google sheets mime type
                .setStarred(false).build();

        // create file
        Drive.DriveApi.getRootFolder(getGoogleApiClient())
                .createFile(getGoogleApiClient(), changeSet, null /* empty file */)
                .setResultCallback(fileCallback);

        // write to file using sheets api
    }
}
