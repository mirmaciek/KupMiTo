package com.mirkiewicz.kupmito;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.mirkiewicz.kupmito.R;

public class AddListDialog extends AppCompatDialogFragment {

    private EditText et_listname, et_listdesc;
    private AddListDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.addlist_dialog, null);
        builder.setView(view)
                .setTitle(getString(R.string.list_add))
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String name = et_listname.getText().toString();
                        String description = et_listdesc.getText().toString();
                        listener.applyTexts(name,description);
                    }
                });

        et_listname = view.findViewById(R.id.edit_listname);
        et_listdesc = view.findViewById(R.id.edit_listdesc);




        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (AddListDialogListener) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

    }

    public interface AddListDialogListener{
        void applyTexts(String name, String description);
    }

}
