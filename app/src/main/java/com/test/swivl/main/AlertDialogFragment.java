package com.test.swivl.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.test.swivl.R;

public class AlertDialogFragment extends DialogFragment {
    public static AlertDialogFragment newInstance(String titleIdKey, String title,
                                                  String showOkButtonKey, boolean showOkButton) {
        AlertDialogFragment dialogFragment = new AlertDialogFragment();
        dialogFragment.setCancelable(false);

        // put arguments in order to use them in creation of dialog
        Bundle arguments = new Bundle();
        arguments.putString(titleIdKey, title);
        arguments.putBoolean(showOkButtonKey, showOkButton);
        dialogFragment.setArguments(arguments);

        return dialogFragment;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString(getString(R.string.dialog_fragment_title_key));
        boolean showOkButton = getArguments().getBoolean(getString(R.string.dialog_fragment_show_ok_button_key));

        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity()).setTitle(title);

        if (showOkButton) {
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
        }

        return builder.create();
    }
}
