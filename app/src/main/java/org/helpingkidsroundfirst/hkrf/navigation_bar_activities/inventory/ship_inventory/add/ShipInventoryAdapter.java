package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.ship_inventory.add;


import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.helpingkidsroundfirst.hkrf.R;

/**
 * Created by alexa on 2/15/2017.
 */

public class ShipInventoryAdapter extends CursorAdapter {

    // constructor
    public ShipInventoryAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layoutId = R.layout.fragment_ship_inventory_item;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ShipViewHolder shipViewHolder = new ShipViewHolder(view);
        view.setTag(shipViewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ShipViewHolder shipViewHolder = (ShipViewHolder) view.getTag();

        // get data from cursor
        String name = cursor.getString(ShipInventoryListFragment.COL_SHIP_NAME);
        String description = cursor.getString(ShipInventoryListFragment.COL_SHIP_DESCRIPTION);
        String category = cursor.getString(ShipInventoryListFragment.COL_SHIP_CATEGORY);
        int qty = cursor.getInt(ShipInventoryListFragment.COL_SHIP_QTY);
        String qty_str = "" + qty;

        // place cursor data into view
        shipViewHolder.nameView.setText(name);
        shipViewHolder.descView.setText(description);
        shipViewHolder.catView.setText(category);
        shipViewHolder.qtyView.setText(qty_str);
    }

    public static class ShipViewHolder {
        public final TextView nameView;
        public final TextView descView;
        public final TextView catView;
        public final TextView qtyView;

        // View ids for holder
        public ShipViewHolder(View view) {
            nameView = (TextView) view.findViewById(R.id.ship_inventory_list_name);
            descView = (TextView) view.findViewById(R.id.ship_inventory_list_desc);
            catView = (TextView) view.findViewById(R.id.ship_inventory_list_cat);
            qtyView = (TextView) view.findViewById(R.id.ship_inventory_list_qty);
        }
    }
}
