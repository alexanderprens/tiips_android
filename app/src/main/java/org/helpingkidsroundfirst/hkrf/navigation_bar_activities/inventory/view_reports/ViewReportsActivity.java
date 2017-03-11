package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_reports;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.helpingkidsroundfirst.hkrf.R;
import org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_reports.item_summary.BarcodeSummaryFragment;
import org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_reports.item_summary.ViewBarcodeSummaryDialogFragment;

public class ViewReportsActivity extends AppCompatActivity implements
        ViewReportFragment.OnReportButtonPressed,
        ViewBarcodeSummaryDialogFragment.OnViewBarcodeSummaryDialogResult {

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

        switch (button) {
            case ViewReportFragment.BUTTON_ITEM:
                // make dialog, start
                FragmentManager fragmentManager = getSupportFragmentManager();
                ViewBarcodeSummaryDialogFragment dialog = new ViewBarcodeSummaryDialogFragment();
                dialog.show(fragmentManager, "open barcode summary dialog");
                break;

            case ViewReportFragment.BUTTON_CURRENT_SUMMARY:

                break;

            case ViewReportFragment.BUTTON_PAST_SUMMARY:

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
}
