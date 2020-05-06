package com.miui.securityscan.c;

import android.content.SharedPreferences;
import android.util.Log;
import java.util.Set;

class c implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ String f7627a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Set f7628b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ e f7629c;

    c(e eVar, String str, Set set) {
        this.f7629c = eVar;
        this.f7627a = str;
        this.f7628b = set;
    }

    public void run() {
        try {
            SharedPreferences.Editor edit = this.f7629c.f7635c.edit();
            edit.putStringSet(this.f7627a, this.f7628b);
            edit.commit();
        } catch (Exception e) {
            Log.e("SharedPreferenceHelper", "saveInThread Set<String>:", e);
        }
    }
}
