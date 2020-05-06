package com.miui.networkassistant.ui.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import b.b.c.c.b.c;
import com.miui.securitycenter.R;
import miui.app.AlertDialog;

public class OptionTipDialog extends c {
    private String mMessage;
    private OptionDialogListener mOptionDialogListener;

    public interface OptionDialogListener {
        void onOptionUpdated(boolean z);
    }

    public OptionTipDialog(Activity activity, OptionDialogListener optionDialogListener) {
        super(activity);
        this.mOptionDialogListener = optionDialogListener;
    }

    public void buildShowDialog(String str, String str2) {
        buildShowDialog(str, str2, (String) null, (String) null);
    }

    public void buildShowDialog(String str, String str2, String str3, String str4) {
        setTitle(str);
        setPostiveText(str4);
        setNagetiveText(str3);
        this.mMessage = str2;
        setWeakReferenceEnabled(false);
        showDialog();
    }

    /* access modifiers changed from: protected */
    public int getNegativeButtonText() {
        return R.string.cancel_button;
    }

    /* access modifiers changed from: protected */
    public int getPositiveButtonText() {
        return R.string.ok_button;
    }

    /* access modifiers changed from: protected */
    public void onBuild(AlertDialog alertDialog) {
        View inflate = View.inflate(this.mActivity, R.layout.dialog_privacy_declare, (ViewGroup) null);
        TextView textView = (TextView) inflate.findViewById(R.id.dialog_message);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(Html.fromHtml(this.mMessage));
        alertDialog.setView(inflate);
        alertDialog.setCancelable(false);
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.mOptionDialogListener.onOptionUpdated(-1 == i);
        dialogInterface.dismiss();
    }

    /* access modifiers changed from: protected */
    public void onShow(AlertDialog alertDialog) {
    }
}
