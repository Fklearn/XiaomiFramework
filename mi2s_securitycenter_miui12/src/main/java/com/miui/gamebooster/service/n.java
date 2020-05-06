package com.miui.gamebooster.service;

import android.database.ContentObserver;
import android.os.Handler;
import com.miui.gamebooster.m.C0384o;

class n extends ContentObserver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ r f4826a;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    n(r rVar, Handler handler) {
        super(handler);
        this.f4826a = rVar;
    }

    public void onChange(boolean z) {
        super.onChange(z);
        if (C0384o.a(this.f4826a.f4832c.getContentResolver(), "quick_reply", 0, -2) == 0) {
            this.f4826a.f4833d.sendEmptyMessage(128);
        }
    }
}
