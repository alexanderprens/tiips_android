package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.ips.tag_messages;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.helpingkidsroundfirst.hkrf.R;
import org.helpingkidsroundfirst.hkrf.data.InventoryContract;

/**
 * A simple {@link Fragment} subclass.
 */
public class TagMessagesListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final int COL_TAG_NAME = 1;
    public static final int COL_TAG_ACTIVE = 2;
    public static final int COL_BATTERY = 3;
    public static final int COL_DATE_SCANNED = 4;
    private static final String[] TAG_COLUMNS = {
            InventoryContract.TagEntry._ID,
            InventoryContract.TagEntry.COLUMN_NAME,
            InventoryContract.TagEntry.COLUMN_ACTIVE,
            InventoryContract.TagEntry.COLUMN_BATTERY,
            InventoryContract.TagEntry.COLUMN_DATE
    };
    private static final String SELECTED_KEY = "selected_position";
    private static final int TAG_LOADER = 20;
    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;
    private TagMessagesAdapter mAdapter;


    public TagMessagesListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAdapter = new TagMessagesAdapter(getActivity(), null, 0);

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_tag_messages_list, container, false);

        // get list view
        mListView = (ListView) rootView.findViewById(R.id.tag_messages_list);
        mListView.setAdapter(mAdapter);

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(TAG_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                InventoryContract.TagEntry.buildTagUri(),
                TAG_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            mListView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
