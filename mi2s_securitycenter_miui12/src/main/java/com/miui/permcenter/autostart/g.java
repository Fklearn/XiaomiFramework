package com.miui.permcenter.autostart;

import android.app.ActivityManager;
import b.b.c.j.x;
import com.miui.permcenter.autostart.AutoStartManagementActivity;

class g implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AutoStartManagementActivity f6073a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ AutoStartManagementActivity.c f6074b;

    g(AutoStartManagementActivity.c cVar, AutoStartManagementActivity autoStartManagementActivity) {
        this.f6074b = cVar;
        this.f6073a = autoStartManagementActivity;
    }

    public void run() {
        if (!this.f6074b.e) {
            x.a((ActivityManager) this.f6073a.getSystemService("activity"), this.f6074b.f6056c);
        }
    }
}
