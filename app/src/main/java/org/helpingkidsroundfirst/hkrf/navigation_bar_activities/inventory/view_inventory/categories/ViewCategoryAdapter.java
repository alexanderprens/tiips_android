package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory.categories;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.helpingkidsroundfirst.hkrf.R;


/**
 * Created by Alex on 2/4/2017.
 */

public class ViewCategoryAdapter extends CursorAdapter {

    public ViewCategoryAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layoutID = R.layout.fragment_view_category;
        View view = LayoutInflater.from(context).inflate(layoutID, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // get data from cursor
        String category = cursor.getString(ViewCategoryListFragment.COL_CATEGORY_NAME);
        String barcode = cursor.getString(ViewCategoryListFragment.COL_CATEGORY_BARCODE);

        // place cursor data into view
        viewHolder.categoryView.setText(category);
        viewHolder.barcodeView.setText(barcode);
    }

    // cache of the children views
    public static class ViewHolder {
        public final TextView categoryView;
        public final TextView barcodeView;

        // view ids for holder
        public ViewHolder(View view) {
            categoryView = (TextView) view.findViewById(R.id.view_category_text_name);
            barcodeView = (TextView) view.findViewById(R.id.view_category_text_barcode);
        }
    }
}
