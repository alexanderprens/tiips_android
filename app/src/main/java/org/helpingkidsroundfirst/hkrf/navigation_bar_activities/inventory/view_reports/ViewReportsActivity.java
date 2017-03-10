package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_reports;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.helpingkidsroundfirst.hkrf.R;

public class ViewReportsActivity extends AppCompatActivity implements
        ViewReportFragment.OnReportButtonPressed {

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

    @Override
    public void onReportButtonPressed(int button) {

    }
}
