package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.ips.locate_item.show_location;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.helpingkidsroundfirst.hkrf.R;
import org.helpingkidsroundfirst.hkrf.data.InventoryContract;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowLocationFragment extends Fragment {

    public static final String URI_KEY = "uri_key";
    private Uri mUri;

    public ShowLocationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_show_location, container, false);

        // init variables
        mUri = null;

        // get id
        Bundle bundle = this.getArguments();
        if (bundle != null && bundle.containsKey(URI_KEY)) {
            mUri = bundle.getParcelable(URI_KEY);
        }

        // header
        String header = getContext().getResources().getString(R.string.location_of) + " " +
                String.format(Locale.US, "%2d", InventoryContract.TagEntry.getTagIdFromUri(mUri));
        final TextView headerView = (TextView) rootView.findViewById(R.id.show_location_header);
        headerView.setText(header);

        // tag alert button
        rootView.findViewById(R.id.show_location_alert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // attempt to write to tag
                ((OnShowLocation) getActivity()).onAlertButtonClick(mUri);
            }
        });

        return rootView;
    }

    public interface OnShowLocation {
        void onAlertButtonClick(Uri uri);
    }
}

