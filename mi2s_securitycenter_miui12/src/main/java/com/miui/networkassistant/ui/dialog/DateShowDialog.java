package com.miui.networkassistant.ui.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import b.b.c.c.b.c;
import com.miui.securitycenter.R;
import miui.app.AlertDialog;
import miui.widget.NumberPicker;

public class DateShowDialog extends c {
    private int mCurrentDate = 1;
    private DateDialogListener mDateDialogListener;
    private NumberPicker mNumberPicker;

    public interface DateDialogListener {
        void onDateUpdated(int i);
    }

    public DateShowDialog(Activity activity, DateDialogListener dateDialogListener) {
        super(activity);
        this.mDateDialogListener = dateDialogListener;
    }

    public void buildDateDialog(String str) {
        buildDateDialog(str, 1);
    }

    public void buildDateDialog(String str, int i) {
        this.mCurrentDate = i;
        setTitle(str);
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
        View inflate = LayoutInflater.from(this.mActivity).inflate(R.layout.dialog_date_pref, (ViewGroup) null);
        alertDialog.setView(inflate);
        this.mNumberPicker = inflate.findViewById(R.id.date_numberPicker);
        this.mNumberPicker.setMaxValue(31);
        this.mNumberPicker.setMinValue(1);
        this.mNumberPicker.setValue(this.mCurrentDate);
    }

    /* access modifiers changed from: protected */
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -1) {
            this.mDateDialogListener.onDateUpdated(this.mNumberPicker.getValue());
        }
        dialogInterface.dismiss();
    }

    /* access modifiers changed from: protected */
    public void onShow(AlertDialog alertDialog) {
    }
}
