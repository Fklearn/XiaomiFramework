package com.miui.powercenter.autotask;

import com.miui.powercenter.autotask.ChooseConditionActivity;
import miui.app.TimePickerDialog;
import miui.widget.TimePicker;

class G implements TimePickerDialog.OnTimeSetListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ChooseConditionActivity.a f6711a;

    G(ChooseConditionActivity.a aVar) {
        this.f6711a = aVar;
    }

    public void onTimeSet(TimePicker timePicker, int i, int i2) {
        this.f6711a.f6698c.f6675a = (i * 60) + i2;
        this.f6711a.i();
    }
}
