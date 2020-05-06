package com.miui.networkassistant.ui.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import b.b.c.c.b.c;
import com.miui.securitycenter.R;
import miui.app.AlertDialog;

public class MessageDialog extends c {
    public MessageDialog(Activity activity) {
        super(activity);
    }

    public void buildShowDialog(String str, String str2) {
        setTitle(str);
        setMessage(str2);
        showDialog();
    }

    /* access modifiers changed from: protected */
    public int getNegativeButtonText() {
        return 0;
    }

    /* access modifiers changed from: protected */
    public int getPositiveButtonText() {
        return R.string.ok_button;
    }

    /* access modifiers changed from: protected */
    public void onBuild(AlertDialog alertDialog) {
    }

    /* access modifiers changed from: protected */
    public void onClick(DialogInterface dialogInterface, int i) {
    }

    /* access modifiers changed from: protected */
    public void onShow(AlertDialog alertDialog) {
    }
}
