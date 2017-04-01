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

    private static final int NUM_TAGS = 8;
    private static final int TAG1 = 0;
    private static final int TAG2 = 1;
    private static final int TAG3 = 2;
    private static final int TAG4 = 3;
    private static final int TAG5 = 4;
    private static final int TAG6 = 5;
    private static final int TAG7 = 6;
    private static final int TAG8 = 7;
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
        switches[TAG1].setOnClickListener(this);
        rootView.findViewById(R.id.manage_tag01_name).setOnClickListener(this);
        textViews[TAG1] = (TextView) rootView.findViewById(R.id.manage_tag01_text);
        textViews[TAG1].setText(tagNames[TAG1]);

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
        }

        updateViews();
    }

    private void updateViews() {

        String[] names = new String[8];
        boolean[] actives = new boolean[8];

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

        textViews[TAG1].setText(names[TAG1]);
        switches[TAG1].setChecked(actives[TAG1]);
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
