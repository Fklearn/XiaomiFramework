package com.miui.securityscan.c;

import android.content.SharedPreferences;
import android.util.Log;

class d implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ String f7630a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ boolean f7631b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ e f7632c;

    d(e eVar, String str, boolean z) {
        this.f7632c = eVar;
        this.f7630a = str;
        this.f7631b = z;
    }

    public void run() {
        try {
            SharedPreferences.Editor edit = this.f7632c.f7635c.edit();
            edit.putBoolean(this.f7630a, this.f7631b);
            edit.commit();
        } catch (Exception e) {
            Log.e("SharedPreferenceHelper", "saveInThread boolean:", e);
        }
    }
}
