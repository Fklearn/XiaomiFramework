package com.miui.antivirus.activity;

import android.database.ContentObserver;
import android.os.Handler;

class s extends ContentObserver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MainActivity f2735a;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    s(MainActivity mainActivity, Handler handler) {
        super(handler);
        this.f2735a = mainActivity;
    }

    public void onChange(boolean z) {
        this.f2735a.z();
    }
}
