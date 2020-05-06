package com.miui.powercenter.savemode;

import android.content.Context;
import b.b.c.j.A;
import com.miui.powercenter.y;
import com.miui.securitycenter.R;
import miui.app.TimePickerDialog;
import miui.widget.TimePicker;

class c implements TimePickerDialog.OnTimeSetListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ e f7277a;

    c(e eVar) {
        this.f7277a = eVar;
    }

    public void onTimeSet(TimePicker timePicker, int i, int i2) {
        int i3 = (i * 60) + i2;
        if (i3 == y.w()) {
            A.a((Context) this.f7277a.getActivity(), (int) R.string.prompt_input_time_illegal);
            return;
        }
        this.f7277a.a(i3);
        y.i(i3);
        a.b(this.f7277a.getActivity());
    }
}
