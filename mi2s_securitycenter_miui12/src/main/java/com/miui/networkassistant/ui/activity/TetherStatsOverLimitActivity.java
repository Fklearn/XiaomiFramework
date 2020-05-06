package com.miui.networkassistant.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import b.b.c.h.l;
import com.miui.securitycenter.R;

public class TetherStatsOverLimitActivity extends Activity {
    private Dialog mDialog;

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        finish();
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.tether_over_limit_dialog_title);
        builder.setMessage(R.string.tether_over_limit_dialog_message);
        builder.setPositiveButton(17039370, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                TetherStatsOverLimitActivity.this.finish();
            }
        });
        builder.setNegativeButton(R.string.tether_over_limit_dialog_cancle_btn, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                l.a(TetherStatsOverLimitActivity.this.getApplicationContext(), true);
                TetherStatsOverLimitActivity.this.finish();
            }
        });
        this.mDialog = builder.create();
        this.mDialog.setCancelable(false);
        this.mDialog.getWindow().setType(2003);
        this.mDialog.show();
    }
}
