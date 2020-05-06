package com.miui.gamebooster.ui;

import android.os.AsyncTask;
import android.util.Log;
import com.miui.gamebooster.c.a;

/* renamed from: com.miui.gamebooster.ui.k  reason: case insensitive filesystem */
class C0433k extends AsyncTask<Void, Void, Void> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C0454v f5079a;

    C0433k(C0454v vVar) {
        this.f5079a = vVar;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Void doInBackground(Void... voidArr) {
        try {
            Boolean unused = this.f5079a.f5120a.G = Boolean.valueOf(this.f5079a.f5120a.o.getSettingEx("xunyou", "xunyou_wifi_accel_switch", "false"));
        } catch (Exception e) {
            Log.i(N.f4939a, e.toString());
        }
        a.U(this.f5079a.f5120a.G.booleanValue());
        return null;
    }
}
