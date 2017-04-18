package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.ips.manage_tags;


import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import org.helpingkidsroundfirst.hkrf.R;
import org.helpingkidsroundfirst.hkrf.data.InventoryContract;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManageTagsActivityFragment extends Fragment implements
        View.OnClickListener,
        UpdateTagNameDialogFragment.UpdateTagNameListener {

    private static final int NUM_TAGS = 11;
    private static final int TAG1 = 0;
    private static final int TAG2 = 1;
    private static final int TAG3 = 2;
    private static final int TAG4 = 3;
    private static final int TAG5 = 4;
    private static final int TAG6 = 5;
    private static final int TAG7 = 6;
    private static final int TAG8 = 7;
    private static final int TAG9 = 8;
    private static final int TAG10 = 9;
    private static final int TAG11 = 10;
    private String[] tagNames;
    private Switch[] switches;
    private TextView[] textViews;


    public ManageTagsActivityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_manage_tags_activity, container, false);

        switches = new Switch[NUM_TAGS];
        textViews = new TextView[NUM_TAGS];
        tagNames = getContext().getResources().getStringArray(R.array.tag_default_names);

        // tag 1
        switches[TAG1] = (Switch) rootView.findViewById(R.id.manage_tag01_active);
        rootView.findViewById(R.id.manage_tag01_name).setOnClickListener(this);
        textViews[TAG1] = (TextView) rootView.findViewById(R.id.manage_tag01_text);

        // tag 2
        switches[TAG2] = (Switch) rootView.findViewById(R.id.manage_tag02_active);
        rootView.findViewById(R.id.manage_tag02_name).setOnClickListener(this);
        textViews[TAG2] = (TextView) rootView.findViewById(R.id.manage_tag02_text);

        // tag 3
        switches[TAG3] = (Switch) rootView.findViewById(R.id.manage_tag03_active);
        rootView.findViewById(R.id.manage_tag03_name).setOnClickListener(this);
        textViews[TAG3] = (TextView) rootView.findViewById(R.id.manage_tag03_text);

        // tag 4
        switches[TAG4] = (Switch) rootView.findViewById(R.id.manage_tag04_active);
        rootView.findViewById(R.id.manage_tag04_name).setOnClickListener(this);
        textViews[TAG4] = (TextView) rootView.findViewById(R.id.manage_tag04_text);

        // tag 5
        switches[TAG5] = (Switch) rootView.findViewById(R.id.manage_tag05_active);
        rootView.findViewById(R.id.manage_tag05_name).setOnClickListener(this);
        textViews[TAG5] = (TextView) rootView.findViewById(R.id.manage_tag05_text);

        // tag 6
        switches[TAG6] = (Switch) rootView.findViewById(R.id.manage_tag06_active);
        rootView.findViewById(R.id.manage_tag06_name).setOnClickListener(this);
        textViews[TAG6] = (TextView) rootView.findViewById(R.id.manage_tag06_text);

        // tag 7
        switches[TAG7] = (Switch) rootView.findViewById(R.id.manage_tag07_active);
        rootView.findViewById(R.id.manage_tag07_name).setOnClickListener(this);
        textViews[TAG7] = (TextView) rootView.findViewById(R.id.manage_tag07_text);

        // tag 8
        switches[TAG8] = (Switch) rootView.findViewById(R.id.manage_tag08_active);
        rootView.findViewById(R.id.manage_tag08_name).setOnClickListener(this);
        textViews[TAG8] = (TextView) rootView.findViewById(R.id.manage_tag08_text);

        // tag 9
        switches[TAG9] = (Switch) rootView.findViewById(R.id.manage_tag09_active);
        rootView.findViewById(R.id.manage_tag09_name).setOnClickListener(this);
        textViews[TAG9] = (TextView) rootView.findViewById(R.id.manage_tag09_text);

        // tag 10
        switches[TAG10] = (Switch) rootView.findViewById(R.id.manage_tag10_active);
        rootView.findViewById(R.id.manage_tag10_name).setOnClickListener(this);
        textViews[TAG10] = (TextView) rootView.findViewById(R.id.manage_tag10_text);

        // tag 8
        switches[TAG11] = (Switch) rootView.findViewById(R.id.manage_tag11_active);
        rootView.findViewById(R.id.manage_tag11_name).setOnClickListener(this);
        textViews[TAG11] = (TextView) rootView.findViewById(R.id.manage_tag11_text);

        // setup stuff for all 8 tags
        for (int i = 0; i < NUM_TAGS; i++) {
            switches[i].setOnClickListener(this);
            textViews[i].setText(tagNames[i]);
        }

        // update views based on database values
        updateViews();

        return rootView;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            // tag 1
            case R.id.manage_tag01_active:
                setActive(TAG1);
                break;

            case R.id.manage_tag01_name:
                startDialog(TAG1);
                break;

            // tag 2
            case R.id.manage_tag02_active:
                setActive(TAG2);
                break;

            case R.id.manage_tag02_name:
                startDialog(TAG2);
                break;

            // tag 3
            case R.id.manage_tag03_active:
                setActive(TAG3);
                break;

            case R.id.manage_tag03_name:
                startDialog(TAG3);
                break;

            // tag 4
            case R.id.manage_tag04_active:
                setActive(TAG4);
                break;

            case R.id.manage_tag04_name:
                startDialog(TAG4);
                break;

            // tag 5
            case R.id.manage_tag05_active:
                setActive(TAG5);
                break;

            case R.id.manage_tag05_name:
                startDialog(TAG5);
                break;

            // tag 6
            case R.id.manage_tag06_active:
                setActive(TAG6);
                break;

            case R.id.manage_tag06_name:
                startDialog(TAG6);
                break;

            // tag 7
            case R.id.manage_tag07_active:
                setActive(TAG7);
                break;

            case R.id.manage_tag07_name:
                startDialog(TAG7);
                break;

            // tag 8
            case R.id.manage_tag08_active:
                setActive(TAG8);
                break;

            case R.id.manage_tag08_name:
                startDialog(TAG8);
                break;

            // tag 9
            case R.id.manage_tag09_active:
                setActive(TAG9);
                break;

            case R.id.manage_tag09_name:
                startDialog(TAG9);
                break;

            // tag 10
            case R.id.manage_tag10_active:
                setActive(TAG10);
                break;

            case R.id.manage_tag10_name:
                startDialog(TAG10);
                break;

            // tag 11
            case R.id.manage_tag11_active:
                setActive(TAG11);
                break;

            case R.id.manage_tag11_name:
                startDialog(TAG11);
                break;
        }

        updateViews();
    }

    private void updateViews() {

        String[] names = new String[NUM_TAGS];
        boolean[] actives = new boolean[NUM_TAGS];

        Cursor cursor = getContext().getContentResolver().query(
                InventoryContract.TagEntry.buildTagUri(),
                new String[]{
                        InventoryContract.TagEntry._ID,
                        InventoryContract.TagEntry.COLUMN_NAME,
                        InventoryContract.TagEntry.COLUMN_ACTIVE
                },
                null,
                null,
                InventoryContract.TagEntry._ID
        );

        if (cursor != null && cursor.moveToFirst()) {
            for (int i = 0; i < NUM_TAGS; i++) {
                names[i] = tagNames[i] + ": " + cursor.getString(1);
                actives[i] = cursor.getInt(2) > 0;
                cursor.moveToNext();
            }

            cursor.close();
        }

        for (int i = 0; i < NUM_TAGS; i++) {
            textViews[i].setText(names[i]);
            switches[i].setChecked(actives[i]);
        }
    }

    private void setActive(int tagId) {

        // get database id
        long tag = (long) tagId + 1;
        boolean active = switches[tagId].isChecked();

        // get values read
        ContentValues contentValues = new ContentValues();
        contentValues.put(InventoryContract.TagEntry.COLUMN_ACTIVE, active);
        String selection = InventoryContract.TagEntry._ID + " = ? ";
        String[] selectionArgs = {Long.toString(tag)};

        // update database
        getContext().getContentResolver().update(
                InventoryContract.TagEntry.buildTagUri(),
                contentValues,
                selection,
                selectionArgs
        );
    }

    private void startDialog(int id) {
        FragmentManager fragmentManager = getFragmentManager();
        UpdateTagNameDialogFragment dialog = new UpdateTagNameDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(UpdateTagNameDialogFragment.ID_KEY, (long) id + 1);
        dialog.setArguments(bundle);
        dialog.setTargetFragment(ManageTagsActivityFragment.this, 300);
        dialog.show(fragmentManager, "open update tag name dialog");
    }

    @Override
    public void onNameUpdated() {
        updateViews();
    }
}
