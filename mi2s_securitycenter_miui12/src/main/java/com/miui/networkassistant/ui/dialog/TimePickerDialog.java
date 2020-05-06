package com.miui.networkassistant.ui.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import b.b.c.c.b.c;
import com.miui.securitycenter.R;
import miui.app.AlertDialog;
import miui.widget.TimePicker;

public class TimePickerDialog extends c implements TimePicker.OnTimeChangedListener {
    private int mActionFlag;
    private int mHour;
    private int mMinute;
    private TimePicker mTimePicker;
    private TimePickerDialogListener mTimePickerDialogListener;

    public interface TimePickerDialogListener {
        void onTimeUpdated(int i, int i2, int i3);
    }

    public TimePickerDialog(Activity activity, TimePickerDialogListener timePickerDialogListener) {
        super(activity);
        this.mTimePickerDialogListener = timePickerDialogListener;
    }

    public void buildTimePickerdialog(String str, int i) {
        this.mActionFlag = i;
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
        View inflate = LayoutInflater.from(this.mActivity).inflate(R.layout.view_time_dialog, (ViewGroup) null);
        alertDialog.setView(inflate);
        this.mTimePicker = inflate.findViewById(R.id.time_picker);
        this.mTimePicker.setIs24HourView(true);
        this.mTimePicker.setOnTimeChangedListener(this);
    }

    /* access modifiers changed from: protected */
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -1) {
            this.mTimePickerDialogListener.onTimeUpdated(this.mHour, this.mMinute, this.mActionFlag);
        }
        dialogInterface.dismiss();
    }

    /* access modifiers changed from: protected */
    public void onShow(AlertDialog alertDialog) {
    }

    public void onTimeChanged(TimePicker timePicker, int i, int i2) {
        this.mHour = i;
        this.mMinute = i2;
    }

    public void setTimePicker(int i, int i2) {
        this.mTimePicker.setCurrentHour(Integer.valueOf(i));
        this.mTimePicker.setCurrentMinute(Integer.valueOf(i2));
    }
}
