package com.miui.powercenter.autotask;

import com.miui.powercenter.autotask.ChooseConditionActivity;
import miui.app.TimePickerDialog;
import miui.widget.TimePicker;

class F implements TimePickerDialog.OnTimeSetListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ChooseConditionActivity.a f6710a;

    F(ChooseConditionActivity.a aVar) {
        this.f6710a = aVar;
    }

    public void onTimeSet(TimePicker timePicker, int i, int i2) {
        int unused = this.f6710a.f6696a = (i * 60) + i2;
        this.f6710a.h();
    }
}
