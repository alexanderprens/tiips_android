package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory.inventory_items;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.helpingkidsroundfirst.hkrf.R;

/**
 * Created by alexa on 1/24/2017.
 */

public class ViewInventoryItemAdapter extends CursorAdapter {
    
    // Cache of the children views
    public static class ViewHolder {
        public final TextView nameView;
        public final TextView descriptionView;
        public final TextView categoryView;
        public final TextView barcodeView;

        // View ids for holder
        public ViewHolder(View view){
            nameView = (TextView) view.findViewById(R.id.inventory_item_name);
            descriptionView = (TextView) view.findViewById(R.id.inventory_item_description);
            categoryView = (TextView) view.findViewById(R.id.inventory_item_category);
            barcodeView = (TextView) view.findViewById(R.id.inventory_item_barcode);
        }
    }

    public ViewInventoryItemAdapter(Context context, Cursor c, int flags){
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layoutID = R.layout.fragment_view_inventory_item;
        View view = LayoutInflater.from(context).inflate(layoutID, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor){
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // get data from cursor
        String name = cursor.getString(ViewInventoryItemListFragment.COL_ITEM_NAME);
        String description = cursor.getString(ViewInventoryItemListFragment.COL_ITEM_DESC);
        String category = cursor.getString(ViewInventoryItemListFragment.COL_ITEM_CAT);
        String barcode = cursor.getString(ViewInventoryItemListFragment.COL_ITEM_BARCODE);

        // place cursor data into view
        viewHolder.nameView.setText(name);
        viewHolder.descriptionView.setText(description);
        viewHolder.categoryView.setText(category);
        viewHolder.barcodeView.setText(barcode);
    }
}
