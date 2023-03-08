package com.example.colectivos_interurbanos_aguilar.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.example.colectivos_interurbanos_aguilar.R;

public class LoadingDialog {
    private Activity activity;
    private AlertDialog dialog;

    public LoadingDialog(Activity myActivity){
        activity = myActivity;
    }

    public void startLoadingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.custom_dialog, null));
        builder.setCancelable(false);

        dialog= builder.create();
        dialog.show();
    }

    public void dismissDialog(){
        dialog.dismiss();
    }


    // declarar en oncreate = final LoadingDialog loadingDialog = new LoadingDialog(MainActivity.this);

    //llamar= loadiangDialog.startLoadingDialog();
    //cerrar= loadingDialog.dismissDialog();

}
