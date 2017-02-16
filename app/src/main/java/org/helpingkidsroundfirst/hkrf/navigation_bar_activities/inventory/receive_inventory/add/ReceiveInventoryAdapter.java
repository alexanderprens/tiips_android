package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.receive_inventory.add;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.helpingkidsroundfirst.hkrf.R;

/**
 * Created by Alex on 2/8/2017.
 */

public class ReceiveInventoryAdapter extends CursorAdapter {
    // constructor
    public ReceiveInventoryAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layoutID = R.layout.fragment_receive_inventory_item;
        View view = LayoutInflater.from(context).inflate(layoutID, parent, false);
        ReceiveViewHolder receiveViewHolder = new ReceiveViewHolder(view);
        view.setTag(receiveViewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ReceiveViewHolder receiveViewHolder = (ReceiveViewHolder) view.getTag();

        //get data from cursor
        String name = cursor.getString(ReceiveInventoryListFragment.COL_ITEM_NAME);
        String description = cursor.getString(ReceiveInventoryListFragment.COL_ITEM_DESCRIPTION);
        String category = cursor.getString(ReceiveInventoryListFragment.COL_CATEGORY_NAME);
        int qty = cursor.getInt(ReceiveInventoryListFragment.COL_RECEIVE_QTY);
        String qty_str = "" + qty;

        //place cursor data into view
        receiveViewHolder.nameView.setText(name);
        receiveViewHolder.descView.setText(description);
        receiveViewHolder.catView.setText(category);
        receiveViewHolder.qtyView.setText(qty_str);
    }

    // Cache of the children views
    public static class ReceiveViewHolder {
        public final TextView nameView;
        public final TextView descView;
        public final TextView catView;
        public final TextView qtyView;

        // View ids for holder
        public ReceiveViewHolder(View view) {
            nameView = (TextView) view.findViewById(R.id.receive_inventory_list_name);
            descView = (TextView) view.findViewById(R.id.receive_inventory_list_desc);
            catView = (TextView) view.findViewById(R.id.receive_inventory_list_cat);
            qtyView = (TextView) view.findViewById(R.id.receive_inventory_list_qty);
        }
    }
}
