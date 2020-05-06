package com.miui.powercenter.autotask;

import com.miui.powercenter.autotask.ChooseConditionActivity;
import miui.app.TimePickerDialog;
import miui.widget.TimePicker;

class H implements TimePickerDialog.OnTimeSetListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ChooseConditionActivity.a f6712a;

    H(ChooseConditionActivity.a aVar) {
        this.f6712a = aVar;
    }

    public void onTimeSet(TimePicker timePicker, int i, int i2) {
        this.f6712a.f6698c.f6676b = (i * 60) + i2;
        this.f6712a.i();
    }
}
