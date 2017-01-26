package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.inventory.view_inventory;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.helpingkidsroundfirst.hkrf.R;

/**
 * Created by Alex on 1/16/2017.
 */

public class AddItemDialogFragment extends android.support.v4.app.DialogFragment
    implements View.OnClickListener {

    // dialog inputs
    private String nameInput;
    private String descInput;
    private String categoryInput;
    private String valueInString;
    private int valueInput;
    private String barcodeInput;

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

        //init inputs
        nameInput = "";
        descInput = "";
        categoryInput = "";
        valueInput = 0;
        valueInString = "";
        barcodeInput = "";

        // listen to name input
        final EditText nameText = (EditText) view.findViewById(R.id.new_item_name);
        nameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // required stub

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nameInput = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //listen to description input


        // listen to category input

        // listen to value input
        EditText valueText = (EditText) view.findViewById(R.id.new_item_value);
        valueText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                valueInString = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // listen to barcode input


        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.new_item_ok:
                if(dialogValidation()){
                    caller.onButtonOK();
                    this.dismiss();
                }
                break;
            case R.id.new_item_cancel:
                caller.onButtonCancel();
                this.dismiss();
                break;
            default:
                break;
        }
    }

    private boolean dialogValidation(){
        boolean check = true;

        if(nameInput.isEmpty()){
            check = false;
            Toast.makeText(getActivity(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), nameInput, Toast.LENGTH_SHORT).show();
        }

        if(!valueInString.isEmpty()) {
            valueInput = Integer.parseInt(valueInString);

            if(valueInput < 1) {
                check = false;
                Toast.makeText(getActivity(), "Value must be greater than zero",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), Integer.toString(valueInput), Toast.LENGTH_SHORT).show();
            }

        } else {
            check = false;
            Toast.makeText(getActivity(), "Value must be greater than zero",
                    Toast.LENGTH_SHORT).show();
        }

        /*valueInput = Integer.valueOf(valueInString);

        if(valueInput < 1){
            check = false;
            Toast.makeText(getActivity(), "Value must be greater than zero",
                    Toast.LENGTH_SHORT).show();
        }*/

        return check;
    }
}
