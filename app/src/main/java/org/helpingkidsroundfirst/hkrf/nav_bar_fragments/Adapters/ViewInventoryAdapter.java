package org.helpingkidsroundfirst.hkrf.nav_bar_fragments.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import org.helpingkidsroundfirst.hkrf.R;
import org.helpingkidsroundfirst.hkrf.nav_bar_fragments.inventory_fragments.ViewInventoryFragment;

/**
 * Created by Alex on 1/10/2017.
 */

public class ViewInventoryAdapter extends CursorAdapter {

    public static class ViewHolder {
        public final TextView nameView;
        public final TextView qtyView;
        public final TextView descView;
        public final TextView catView;

        public ViewHolder(View view) {
            nameView = (TextView) view.findViewById(R.id.inv_list_name_textview);
            qtyView = (TextView) view.findViewById(R.id.inv_list_qty_textview);
            descView = (TextView) view.findViewById(R.id.inv_list_desc_textview);
            catView = (TextView) view.findViewById(R.id.inv_list_cat_textview);
        }
    }

    public ViewInventoryAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        View view = LayoutInflater.from(context).inflate(R.layout.inventory_list_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        long itemID = cursor.getLong(ViewInventoryFragment.COL_ITEM_ID);

        // TODO: 1/10/2017 find out how to get item and cat data
        String name = "";
        viewHolder.nameView.setText(name);

        String desc = "";
        viewHolder.descView.setText(desc);

        String cat = "";
        viewHolder.catView.setText(cat);

        int qty = cursor.getInt(ViewInventoryFragment.COL_QTY);
        viewHolder.qtyView.setText(Integer.toString(qty));
    }
}
