package com.example.androidchess;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialogFragment;
import com.example.androidchess.Chess;
import com.example.androidchess.pieces.Board;

public class Draw extends AppCompatDialogFragment {

    //Button[][] buttonBoard = new Button[8][8];

    public static boolean drawPress;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("ALERT").setMessage("Do you want to draw? If so press 'ok' then 'draw'. If not click away (DON'T PRESS OK).").setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                System.out.println("YOOOOOOOOOOOOO");
                drawPress = true;
            }
        });
            return builder.create();

    }
}
