package com.miui.networkassistant.ui.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import b.b.c.c.b.c;
import com.miui.securitycenter.R;
import miui.app.AlertDialog;
import miui.widget.DatePicker;

public class DatePickerDialog extends c implements DatePicker.OnDateChangedListener {
    private DatePicker mDatePicker;
    private int mDay;
    private DatePickerDialogListener mListener;
    private int mMonth;
    private int mYear;

    public interface DatePickerDialogListener {
        void onDateChanged(int i, int i2, int i3);
    }

    public DatePickerDialog(Activity activity, DatePickerDialogListener datePickerDialogListener) {
        super(activity);
        this.mListener = datePickerDialogListener;
    }

    public void buildDatePickerDialog(String str) {
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
        View inflate = LayoutInflater.from(this.mActivity).inflate(R.layout.view_date_dialog, (ViewGroup) null);
        alertDialog.setView(inflate);
        this.mDatePicker = inflate.findViewById(R.id.date_picker);
    }

    /* access modifiers changed from: protected */
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -1) {
            this.mListener.onDateChanged(this.mYear, this.mMonth, this.mDay);
        }
        dialogInterface.dismiss();
    }

    public void onDateChanged(DatePicker datePicker, int i, int i2, int i3, boolean z) {
        this.mYear = i;
        this.mMonth = i2;
        this.mDay = i3;
    }

    /* access modifiers changed from: protected */
    public void onShow(AlertDialog alertDialog) {
    }

    public void setData(int i, int i2, int i3) {
        this.mYear = i;
        this.mMonth = i2;
        this.mDay = i3;
        DatePicker datePicker = this.mDatePicker;
        int i4 = this.mYear;
        int i5 = this.mMonth;
        datePicker.init(i4, i5, i5, this);
    }
}
