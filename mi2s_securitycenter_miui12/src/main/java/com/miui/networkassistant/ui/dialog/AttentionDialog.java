package com.miui.networkassistant.ui.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import b.b.c.c.b.c;
import com.miui.securitycenter.R;
import miui.app.AlertDialog;

public class AttentionDialog extends c {
    private AttentionDialogListener mListener = null;

    public interface AttentionDialogListener {
        void onAttentionChanged(boolean z);
    }

    public AttentionDialog(Activity activity, AttentionDialogListener attentionDialogListener) {
        super(activity);
        this.mListener = attentionDialogListener;
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
        return R.string.text_button;
    }

    /* access modifiers changed from: protected */
    public void onBuild(AlertDialog alertDialog) {
        alertDialog.setCancelable(false);
    }

    /* access modifiers changed from: protected */
    public void onClick(DialogInterface dialogInterface, int i) {
        this.mListener.onAttentionChanged(-1 == i);
        dialogInterface.dismiss();
    }

    /* access modifiers changed from: protected */
    public void onShow(AlertDialog alertDialog) {
    }
}
