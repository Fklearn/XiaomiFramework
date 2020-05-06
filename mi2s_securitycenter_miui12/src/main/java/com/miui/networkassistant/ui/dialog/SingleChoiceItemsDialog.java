package com.miui.networkassistant.ui.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import b.b.c.c.b.c;
import com.miui.securitycenter.R;
import miui.app.AlertDialog;

public class SingleChoiceItemsDialog extends c {
    private int mActionFlag;
    private String[] mItems;
    private SingleChoiceItemsDialogListener mListener;
    private int mSelectedIndex;

    public interface SingleChoiceItemsDialogListener {
        void onSelectItemUpdate(int i, int i2);
    }

    public SingleChoiceItemsDialog(Activity activity, SingleChoiceItemsDialogListener singleChoiceItemsDialogListener) {
        super(activity);
        this.mListener = singleChoiceItemsDialogListener;
    }

    public void buildDialog(int i, String[] strArr, int i2, int i3) {
        buildDialog(this.mActivity.getString(i), strArr, i2, i3);
    }

    public void buildDialog(String str, String[] strArr, int i, int i2) {
        this.mActionFlag = i2;
        clearDialog();
        setTitle(str);
        this.mItems = strArr;
        this.mSelectedIndex = i;
        showDialog();
    }

    /* access modifiers changed from: protected */
    public int getNegativeButtonText() {
        return R.string.cancel_button;
    }

    /* access modifiers changed from: protected */
    public int getPositiveButtonText() {
        return 0;
    }

    /* access modifiers changed from: protected */
    public void onBuild(AlertDialog alertDialog) {
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (i >= 0) {
            this.mSelectedIndex = i;
        }
        this.mListener.onSelectItemUpdate(this.mSelectedIndex, this.mActionFlag);
        dialogInterface.dismiss();
    }

    /* access modifiers changed from: protected */
    public void onPrepareBuild(AlertDialog.Builder builder) {
        builder.setSingleChoiceItems(this.mItems, this.mSelectedIndex, getOnClickListener());
    }

    /* access modifiers changed from: protected */
    public void onShow(AlertDialog alertDialog) {
    }
}
