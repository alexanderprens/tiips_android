package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import org.helpingkidsroundfirst.hkrf.R;

/**
 * Created by Alex on 1/16/2017.
 */

public class AddItemDialogFragment extends android.support.v4.app.DialogFragment
    implements View.OnClickListener {

    public interface AddItemDialogListener {
        void onButtonOK();
        void onButtonCancel();
    }

    private AddItemDialogListener caller;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        try {
            caller = (AddItemDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Must implement AddItemDialogFragment listener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // use builder class to construct dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_inventory_item, null);

        // set click listeners on buttons
        view.findViewById(R.id.new_item_ok).setOnClickListener(this);
        view.findViewById(R.id.new_item_cancel).setOnClickListener(this);

        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.new_item_ok:
                caller.onButtonOK();
                this.dismiss();
                break;
            case R.id.new_item_cancel:
                caller.onButtonCancel();
                this.dismiss();
                break;
            default:
                break;
        }
    }
}
