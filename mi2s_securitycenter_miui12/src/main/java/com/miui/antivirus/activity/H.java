package com.miui.antivirus.activity;

import android.widget.CompoundButton;
import com.miui.antivirus.activity.SignExceptionActivity;
import com.miui.antivirus.model.e;

class H implements CompoundButton.OnCheckedChangeListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ SignExceptionActivity.b f2663a;

    H(SignExceptionActivity.b bVar) {
        this.f2663a = bVar;
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        e eVar = (e) compoundButton.getTag();
        if (eVar != null) {
            if (z) {
                this.f2663a.f2704b.add(eVar.m());
            } else {
                this.f2663a.f2704b.remove(eVar.m());
            }
            SignExceptionActivity.this.f2698d.setEnabled(!this.f2663a.f2704b.isEmpty());
        }
    }
}
