package com.example.androidchess;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;

import com.example.androidchess.Chess;


import androidx.appcompat.app.AppCompatDialogFragment;



    public class PopUp extends AppCompatDialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle("Information").setMessage("Game Saved");

            return builder.create();
        }

    }


