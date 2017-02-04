package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory.current_inventory;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.helpingkidsroundfirst.hkrf.R;

/**
 * Created by Alex on 2/1/2017.
 */

public class ViewCurrentInventoryAdapter extends CursorAdapter {

    // Cache of the children views
    public static class ViewHolder {
        public final TextView nameView;
        public final TextView descView;
        public final TextView catView;
        public final TextView qtyView;

        // View ids for holder
        public ViewHolder(View view) {
            nameView = (TextView) view.findViewById(R.id.view_current_inventory_name);
            descView = (TextView) view.findViewById(R.id.view_current_inventory_desc);
            catView = (TextView) view.findViewById(R.id.view_current_inventory_cat);
            qtyView = (TextView) view.findViewById(R.id.view_current_inventory_qty);
        }
    }

    // constructor
    public ViewCurrentInventoryAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layoutID = R.layout.fragment_view_current_inventory;
        View view = LayoutInflater.from(context).inflate(layoutID, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        //get data from cursor
        String name = cursor.getString(ViewCurrentInventoryListFragment.COL_ITEM_NAME);
        String description = cursor.getString(ViewCurrentInventoryListFragment.COL_ITEM_DESCRIPTION);
        String category = cursor.getString(ViewCurrentInventoryListFragment.COL_CATEGORY_NAME);
        int qty = cursor.getInt(ViewCurrentInventoryListFragment.COL_CURRENT_QTY);
        String qty_str = "" + qty;

        //place cursor data into view
        viewHolder.nameView.setText(name);
        viewHolder.descView.setText(description);
        viewHolder.catView.setText(category);
        viewHolder.qtyView.setText(qty_str);
    }
}
