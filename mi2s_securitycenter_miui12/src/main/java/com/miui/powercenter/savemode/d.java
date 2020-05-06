package com.miui.powercenter.savemode;

import androidx.preference.Preference;
import com.miui.powercenter.y;
import miui.app.TimePickerDialog;

class d implements Preference.c {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ e f7278a;

    d(e eVar) {
        this.f7278a = eVar;
    }

    public boolean onPreferenceClick(Preference preference) {
        int i;
        TimePickerDialog timePickerDialog;
        if (preference == this.f7278a.g) {
            i = y.w();
            timePickerDialog = new TimePickerDialog(this.f7278a.getActivity(), this.f7278a.l, 0, 0, true);
        } else {
            i = y.v();
            timePickerDialog = new TimePickerDialog(this.f7278a.getActivity(), this.f7278a.m, 0, 0, true);
        }
        timePickerDialog.updateTime(i / 60, i % 60);
        timePickerDialog.show();
        return false;
    }
}
