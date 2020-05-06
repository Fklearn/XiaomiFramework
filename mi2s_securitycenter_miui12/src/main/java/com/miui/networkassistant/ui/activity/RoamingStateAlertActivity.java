package com.miui.networkassistant.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import b.b.c.c.b.g;
import com.miui.networkassistant.ui.fragment.RoamingWhiteListFragment;
import com.miui.securitycenter.R;

public class RoamingStateAlertActivity extends Activity {
    public static final String DIALOG_MESSAGE = "dialog_message";
    public static final String DIALOG_TITLE = "dialog_title";
    private static final String TAG = "RoamingStateAlertActivity";
    public Dialog mDialog;

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        finish();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            finish();
            return;
        }
        String string = extras.getString(DIALOG_TITLE);
        String string2 = extras.getString(DIALOG_MESSAGE);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(string);
        builder.setMessage(string2);
        builder.setPositiveButton(R.string.add_to_whitelist_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                g.startWithFragment(RoamingStateAlertActivity.this, RoamingWhiteListFragment.class);
            }
        });
        builder.setNegativeButton(17039369, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        this.mDialog = builder.create();
        this.mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialogInterface) {
                RoamingStateAlertActivity.this.finish();
            }
        });
        this.mDialog.getWindow().setType(2003);
        this.mDialog.show();
    }
}
