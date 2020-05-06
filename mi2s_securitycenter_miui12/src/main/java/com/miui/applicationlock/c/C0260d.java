package com.miui.applicationlock.c;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;
import com.miui.securitycenter.service.RemoteService;

/* renamed from: com.miui.applicationlock.c.d  reason: case insensitive filesystem */
class C0260d extends AsyncTask<Void, Void, Void> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C0267k f3300a;

    C0260d(C0267k kVar) {
        this.f3300a = kVar;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Void doInBackground(Void... voidArr) {
        if (!C0267k.b(this.f3300a.f3311b)) {
            return null;
        }
        Intent intent = new Intent(this.f3300a.f3310a, RemoteService.class);
        intent.putExtra("cmd", "app_lock");
        intent.putExtra("param", "handle_notifycation");
        ((AlarmManager) this.f3300a.f3310a.getSystemService("alarm")).setRepeating(2, SystemClock.elapsedRealtime() + o.w(), 1296000000, PendingIntent.getService(this.f3300a.f3310a, 0, intent, 0));
        return null;
    }
}
