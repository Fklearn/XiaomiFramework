package com.miui.gamebooster.service;

import android.database.ContentObserver;
import android.os.Handler;
import com.miui.gamebooster.m.C0384o;

/* renamed from: com.miui.gamebooster.service.m  reason: case insensitive filesystem */
class C0412m extends ContentObserver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ r f4825a;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    C0412m(r rVar, Handler handler) {
        super(handler);
        this.f4825a = rVar;
    }

    public void onChange(boolean z) {
        super.onChange(z);
        if (C0384o.a(this.f4825a.f4832c.getContentResolver(), "gb_boosting", 0, -2) == 0) {
            this.f4825a.f4833d.sendEmptyMessage(128);
        }
    }
}
