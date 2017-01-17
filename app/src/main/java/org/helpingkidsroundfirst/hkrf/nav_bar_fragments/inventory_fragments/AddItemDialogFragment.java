package org.helpingkidsroundfirst.hkrf.nav_bar_fragments.inventory_fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;

import org.helpingkidsroundfirst.hkrf.R;

/**
 * Created by Alex on 1/16/2017.
 */

public class AddItemDialogFragment extends android.support.v4.app.DialogFragment {

    public interface AddItemDialogListener {
        void onFinishAddItemDialog(String inputText);
    }

    public void sendBackResult() {
        AddItemDialogListener listener = (AddItemDialogListener) getTargetFragment();
        listener.onFinishAddItemDialog("new item text");
        dismiss();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // use builder class to construct dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.dialog_add_inventory_item, null));
        return builder.create();
    }
}
