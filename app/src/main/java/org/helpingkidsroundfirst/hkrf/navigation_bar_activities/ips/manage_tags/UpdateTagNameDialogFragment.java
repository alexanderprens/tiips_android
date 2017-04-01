package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.ips.manage_tags;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.helpingkidsroundfirst.hkrf.R;
import org.helpingkidsroundfirst.hkrf.data.InventoryContract;

import java.util.Locale;

/**
 * Created by alexa on 4/1/2017.
 */

public class UpdateTagNameDialogFragment extends DialogFragment implements
        View.OnClickListener {

    public static final String ID_KEY = "id_long";
    private String nameInput;
    private long tagId;
    private UpdateTagNameListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            mListener = (UpdateTagNameListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Must implement UpdateTagNameListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // use builder ot create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_update_tag_name, null);

        // set click listeners on buttons
        view.findViewById(R.id.update_tag_name_cancel).setOnClickListener(this);
        view.findViewById(R.id.update_tag_name_ok).setOnClickListener(this);

        // init inputs
        nameInput = "";
        tagId = -1;

        // get arguments from bundle
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            tagId = bundle.getLong(ID_KEY);
        }

        if (tagId == -1) {
            this.dismiss();
        }

        // header
        TextView header = (TextView) view.findViewById(R.id.dialog_update_tag_name_text);
        String headerString = String.format(Locale.US, "Tag %2d", tagId);
        header.setText(headerString);

        // edit text
        final EditText nameText = (EditText) view.findViewById(R.id.update_tag_name_edit);
        nameText.setText(getPreviousName());
        nameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nameInput = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.update_tag_name_ok:
                if (updateName()) {
                    mListener.onNameUpdated();
                    this.dismiss();
                }
                break;

            case R.id.update_tag_name_cancel:
                this.dismiss();
                break;
        }
    }

    private String getPreviousName() {

        String name = "";

        if (tagId == -1) {
            return name;
        }

        Cursor cursor = getContext().getContentResolver().query(
                InventoryContract.TagEntry.buildTagWithIdUri(tagId),
                null,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndex(InventoryContract.TagEntry.COLUMN_NAME));
            cursor.close();
        }

        return name;
    }

    private boolean updateName() {

        if (nameInput.isEmpty()) {
            Toast.makeText(getContext(), getContext().getResources()
                    .getString(R.string.validation_name_empty), Toast.LENGTH_SHORT).show();
            return false;
        }

        // update database
        ContentValues contentValues = new ContentValues();
        contentValues.put(InventoryContract.TagEntry.COLUMN_NAME, nameInput);
        String selection = InventoryContract.TagEntry._ID + " = ? ";
        String[] selectionArgs = {Long.toString(tagId)};

        // update database
        getContext().getContentResolver().update(
                InventoryContract.TagEntry.buildTagUri(),
                contentValues,
                selection,
                selectionArgs
        );

        return true;
    }

    public interface UpdateTagNameListener {
        void onNameUpdated();
    }
}
