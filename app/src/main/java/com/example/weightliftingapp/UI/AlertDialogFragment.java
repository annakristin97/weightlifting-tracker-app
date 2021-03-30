package com.example.weightliftingapp.UI;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;

import com.example.weightliftingapp.R;


public class AlertDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(R.string.error_title)
                .setMessage(R.string.error_message)
                .setPositiveButton(R.string.error_ok_button_text,null);

        AlertDialog dialog = builder.create();
        return dialog;
    }
}

