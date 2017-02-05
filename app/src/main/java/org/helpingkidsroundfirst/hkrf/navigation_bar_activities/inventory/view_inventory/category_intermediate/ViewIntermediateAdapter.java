package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory.category_intermediate;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.helpingkidsroundfirst.hkrf.R;

/**
 * Created by Alex on 2/5/2017.
 */

public class ViewIntermediateAdapter extends CursorAdapter {

    public ViewIntermediateAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layoutId = R.layout.fragment_view_intermediate;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // get data form cursor
        String category = cursor.getString(ViewIntermediateListFragment.COL_CATEGORY_CAT);

        // place data into view
        viewHolder.categoryView.setText(category);
    }

    // cache of children views
    public static class ViewHolder {
        public final TextView categoryView;

        // view ids for holder
        public ViewHolder(View view) {
            categoryView = (TextView) view.findViewById(R.id.view_intermediate_category);
        }
    }
}
