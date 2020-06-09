package com.batura.stas.notesaplication;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.batura.stas.notesaplication.AuthFunc.AuthMainActivity;

public class SyncDialogFragment extends DialogFragment
{

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String message = getString(R.string.sync_dialog_text);
        String button1String = getString(R.string.ok_but);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message); // сообщение
        builder.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(getActivity(), R.string.start,
                        Toast.LENGTH_SHORT).show();
                ((MainActivity) getActivity()).startAuthActivity();
            }
        });
//        builder.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                Toast.makeText(getActivity(), R.string.dialogNoThx, Toast.LENGTH_SHORT)
//                        .show();
//                ((MainActivity) getActivity()).cancelClicked();
//            }
//        });
        builder.setCancelable(true);

        return builder.create();
    }



}
