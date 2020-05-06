package com.miui.gamebooster.ui;

import android.os.AsyncTask;
import android.util.Log;
import com.miui.gamebooster.c.a;

/* renamed from: com.miui.gamebooster.ui.z  reason: case insensitive filesystem */
class C0462z extends AsyncTask<Void, Void, Void> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ N f5130a;

    C0462z(N n) {
        this.f5130a = n;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Void doInBackground(Void... voidArr) {
        Boolean unused = this.f5130a.F = Boolean.valueOf(a.o(true));
        try {
            Boolean unused2 = this.f5130a.G = Boolean.valueOf(this.f5130a.o.getSettingEx("xunyou", "xunyou_wifi_accel_switch", "false"));
            return null;
        } catch (Exception e) {
            Log.i(N.f4939a, e.toString());
            return null;
        }
    }
}
