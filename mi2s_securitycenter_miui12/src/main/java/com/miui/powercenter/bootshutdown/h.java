package com.miui.powercenter.bootshutdown;

import com.miui.powercenter.bootshutdown.PowerShutdownOnTime;
import com.miui.powercenter.utils.u;
import miui.app.TimePickerDialog;
import miui.widget.TimePicker;

class h implements TimePickerDialog.OnTimeSetListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ PowerShutdownOnTime.a f6954a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ PowerShutdownOnTime.a.C0062a f6955b;

    h(PowerShutdownOnTime.a.C0062a aVar, PowerShutdownOnTime.a aVar2) {
        this.f6955b = aVar;
        this.f6954a = aVar2;
    }

    public void onTimeSet(TimePicker timePicker, int i, int i2) {
        int unused = this.f6954a.i = (i * 60) + i2;
        this.f6954a.f.a(u.b(this.f6954a.i));
    }
}
