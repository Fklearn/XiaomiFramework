package com.miui.powercenter;

import android.database.ContentObserver;
import android.os.Handler;
import miuix.preference.DropDownPreference;
import miuix.preference.TextPreference;

class s extends ContentObserver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ x f7275a;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    s(x xVar, Handler handler) {
        super(handler);
        this.f7275a = xVar;
    }

    public void onChange(boolean z) {
        if (this.f7275a.a()) {
            DropDownPreference v = this.f7275a.f7369d;
            x xVar = this.f7275a;
            v.b(xVar.c(xVar.f()));
            return;
        }
        TextPreference k = this.f7275a.e;
        x xVar2 = this.f7275a;
        k.a(xVar2.c(xVar2.f()));
    }
}
