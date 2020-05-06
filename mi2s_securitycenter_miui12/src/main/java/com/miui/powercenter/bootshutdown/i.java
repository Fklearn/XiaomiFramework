package com.miui.powercenter.bootshutdown;

import com.miui.powercenter.bootshutdown.PowerShutdownOnTime;
import com.miui.powercenter.utils.u;
import miui.app.TimePickerDialog;
import miui.widget.TimePicker;

class i implements TimePickerDialog.OnTimeSetListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ PowerShutdownOnTime.a f6956a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ PowerShutdownOnTime.a.C0062a f6957b;

    i(PowerShutdownOnTime.a.C0062a aVar, PowerShutdownOnTime.a aVar2) {
        this.f6957b = aVar;
        this.f6956a = aVar2;
    }

    public void onTimeSet(TimePicker timePicker, int i, int i2) {
        int unused = this.f6956a.h = (i * 60) + i2;
        this.f6956a.f6943c.a(u.b(this.f6956a.h));
    }
}
