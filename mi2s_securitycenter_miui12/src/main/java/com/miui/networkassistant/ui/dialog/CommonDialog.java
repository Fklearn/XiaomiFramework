package com.miui.networkassistant.ui.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.View;
import b.b.c.c.b.c;
import miui.app.AlertDialog;

public class CommonDialog extends c {
    private DialogInterface.OnClickListener mClickListener;
    private View mView;

    public CommonDialog(Activity activity, DialogInterface.OnClickListener onClickListener) {
        super(activity);
        this.mClickListener = onClickListener;
    }

    public void dismiss() {
        clearDialog();
    }

    /* access modifiers changed from: protected */
    public int getNegativeButtonText() {
        return 17039360;
    }

    /* access modifiers changed from: protected */
    public int getPositiveButtonText() {
        return 17039370;
    }

    /* access modifiers changed from: protected */
    public void onBuild(AlertDialog alertDialog) {
        View view = this.mView;
        if (view != null) {
            alertDialog.setView(view);
        }
        alertDialog.setCancelable(false);
    }

    /* access modifiers changed from: protected */
    public void onClick(DialogInterface dialogInterface, int i) {
        DialogInterface.OnClickListener onClickListener = this.mClickListener;
        if (onClickListener != null) {
            onClickListener.onClick(dialogInterface, i);
        }
    }

    /* access modifiers changed from: protected */
    public void onShow(AlertDialog alertDialog) {
    }

    public void setView(View view) {
        this.mView = view;
    }

    public void setWeakReferenceEnabled(boolean z) {
        super.setWeakReferenceEnabled(z);
    }

    public void show() {
        showDialog();
    }
}
