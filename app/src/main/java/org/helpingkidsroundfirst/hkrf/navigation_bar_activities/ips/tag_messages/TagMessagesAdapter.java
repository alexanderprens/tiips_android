package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.ips.tag_messages;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.helpingkidsroundfirst.hkrf.R;

import java.util.Locale;

/**
 * Created by alexa on 4/2/2017.
 */

public class TagMessagesAdapter extends CursorAdapter {

    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_TAG = 0;
    private static final int VIEW_TYPE_BEACON = 1;
    private static final int NUM_TAGS = 11;
    private static final double BATTERY_RANGE = 3.0 - 2.4;


    public TagMessagesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view;
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;

        switch (viewType) {
            case VIEW_TYPE_TAG:
                layoutId = R.layout.fragment_tag_messages_list_tag;
                view = LayoutInflater.from(context).inflate(layoutId, parent, false);
                TagHolder tagHolder = new TagHolder(view);
                view.setTag(tagHolder);
                break;

            case VIEW_TYPE_BEACON:
                layoutId = R.layout.fragment_tag_messages_list_beacon;
                view = LayoutInflater.from(context).inflate(layoutId, parent, false);
                BeaconHolder beaconHolder = new BeaconHolder(view);
                view.setTag(beaconHolder);
                break;

            default:
                view = LayoutInflater.from(context).inflate(layoutId, parent, false);
                TagHolder tagHolder1 = new TagHolder(view);
                view.setTag(tagHolder1);
        }

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        int viewType = getItemViewType(cursor.getPosition());

        String name = cursor.getString(TagMessagesListFragment.COL_TAG_NAME);
        String dateScanned = cursor.getString(TagMessagesListFragment.COL_DATE_SCANNED);
        double batteryLevel = cursor.getDouble(TagMessagesListFragment.COL_BATTERY);
        double batteryPercent = (batteryLevel - 2.4) / (BATTERY_RANGE) * 100.0;
        if (batteryPercent <= 0) {
            batteryPercent = 0;
        } else if (batteryPercent > 100.0) {
            batteryPercent = 100.0;
        }
        String batteryString = String.format(Locale.US, "%3.0f", batteryPercent) + "%";

        switch (viewType) {

            case VIEW_TYPE_TAG:

                TagHolder tagHolder = (TagHolder) view.getTag();

                // name
                tagHolder.nameView.setText(name);

                // date scanned
                tagHolder.dateScannedView.setText(dateScanned);

                // battery
                tagHolder.batteryView.setText(batteryString);

                // active
                boolean active = cursor.getInt(TagMessagesListFragment.COL_TAG_ACTIVE) > 0;
                String activeString;
                if (active) {
                    activeString = context.getString(R.string.active);
                } else {
                    activeString = context.getString(R.string.inactive);
                }
                tagHolder.activeView.setText(activeString);

                // missing
                String missing;
                if (batteryLevel == 0 && active) {
                    missing = context.getString(R.string.missing);
                } else {
                    missing = context.getString(R.string.not_missing);
                }
                tagHolder.missingView.setText(missing);

                break;

            case VIEW_TYPE_BEACON:

                BeaconHolder beaconHolder = (BeaconHolder) view.getTag();

                // name
                beaconHolder.nameView.setText(name);

                // date scanned
                beaconHolder.batteryView.setText(batteryString);

                // battery
                beaconHolder.dateScannedView.setText(dateScanned);

                break;
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (position < NUM_TAGS) {
            return VIEW_TYPE_TAG;
        } else {
            return VIEW_TYPE_BEACON;
        }
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    public static class TagHolder {
        public final TextView nameView;
        public final TextView batteryView;
        public final TextView dateScannedView;
        public final TextView activeView;
        public final TextView missingView;

        public TagHolder(View view) {
            nameView = (TextView) view.findViewById(R.id.tag_messages_name);
            batteryView = (TextView) view.findViewById(R.id.tag_messages_battery_level);
            dateScannedView = (TextView) view.findViewById(R.id.tag_messages_date_scanned);
            activeView = (TextView) view.findViewById(R.id.tag_messages_active);
            missingView = (TextView) view.findViewById(R.id.tag_messages_missing);
        }
    }

    public static class BeaconHolder {
        public final TextView nameView;
        public final TextView batteryView;
        public final TextView dateScannedView;

        public BeaconHolder(View view) {
            nameView = (TextView) view.findViewById(R.id.tag_messages_name);
            batteryView = (TextView) view.findViewById(R.id.tag_messages_battery_level);
            dateScannedView = (TextView) view.findViewById(R.id.tag_messages_date_scanned);
        }
    }
}
