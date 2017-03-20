package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_reports;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.helpingkidsroundfirst.hkrf.R;
import org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_reports.google_drive.ExportDBToDriveActivity;
import org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_reports.google_drive.ExportToSheetsActivity;
import org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_reports.google_drive.UpdateDBFromDriveActivity;
import org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_reports.item_summary.BarcodeSummaryFragment;
import org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_reports.item_summary.ViewBarcodeSummaryDialogFragment;
import org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_reports.shipment_summary.ShipmentSummaryDialogFragment;
import org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_reports.shipment_summary.ShipmentSummaryFragment;
import org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_reports.summary_fragments.CurrentInventorySummaryFragment;
import org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_reports.summary_fragments.PastInventorySummaryFragment;

public class ViewReportsActivity extends AppCompatActivity implements
        ViewReportFragment.OnReportButtonPressed,
        ViewBarcodeSummaryDialogFragment.OnViewBarcodeSummaryDialogResult,
        ShipmentSummaryDialogFragment.OnShipmentSummaryListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reports);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_view_reports));

        if (savedInstanceState == null) {
            // start initial fragment
            Fragment fragment = new ViewReportFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_view_reports, fragment)
                    .commit();
        }
    }

    private void startFragment(Fragment fragment, String fragmentTag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_view_reports, fragment)
                .addToBackStack(fragmentTag)
                .commit();
    }

    @Override
    public void onReportButtonPressed(int button) {

        Fragment fragment;
        Intent intent;
        FragmentManager fragmentManager;

        switch (button) {
            case ViewReportFragment.BUTTON_ITEM:
                // make dialog, start
                fragmentManager = getSupportFragmentManager();
                ViewBarcodeSummaryDialogFragment dialog = new ViewBarcodeSummaryDialogFragment();
                dialog.show(fragmentManager, "open barcode summary dialog");
                break;

            case ViewReportFragment.BUTTON_CURRENT_SUMMARY:
                fragment = new CurrentInventorySummaryFragment();
                startFragment(fragment, "CurrentInventorySummaryFragment");
                break;

            case ViewReportFragment.BUTTON_PAST_SUMMARY:
                fragment = new PastInventorySummaryFragment();
                startFragment(fragment, "PastInventorySummaryFragment");
                break;

            case ViewReportFragment.BUTTON_SHEET_EXPORT:
                intent = new Intent(ViewReportsActivity.this, ExportToSheetsActivity.class);
                startActivity(intent);
                break;

            case ViewReportFragment.BUTTON_DB_EXPORT:
                intent = new Intent(ViewReportsActivity.this, ExportDBToDriveActivity.class);
                startActivity(intent);
                break;

            case ViewReportFragment.BUTTON_SHIPMENT_SUMMARY:
                fragmentManager = getSupportFragmentManager();
                ShipmentSummaryDialogFragment dialogShip = new ShipmentSummaryDialogFragment();
                dialogShip.show(fragmentManager, "open shipment summary dialog");
                break;

            case ViewReportFragment.BUTTON_DB_IMPORT:
                intent = new Intent(ViewReportsActivity.this, UpdateDBFromDriveActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onBarcodeChosen(Uri uri) {
        Fragment fragment = new BarcodeSummaryFragment();
        Bundle bundle = new Bundle();

        bundle.putParcelable(BarcodeSummaryFragment.ITEM_URI, uri);
        fragment.setArguments(bundle);

        startFragment(fragment, "BarcodeSummaryFragment");
    }

    @Override
    public void onShipmentButton(String date) {
        Fragment fragment = new ShipmentSummaryFragment();
        Bundle bundle = new Bundle();

        bundle.putString(ShipmentSummaryFragment.DATE_KEY, date);
        fragment.setArguments(bundle);

        startFragment(fragment, "ShipmentSummaryFragment");
    }
}
