package com.miui.antispam.service.a;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.miui.antispam.service.a.b;

class a extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ boolean[] f2395a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ long[] f2396b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ boolean[] f2397c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ DownloadManager f2398d;
    final /* synthetic */ Runnable e;
    final /* synthetic */ b.C0036b f;
    final /* synthetic */ String g;
    final /* synthetic */ b h;

    a(b bVar, boolean[] zArr, long[] jArr, boolean[] zArr2, DownloadManager downloadManager, Runnable runnable, b.C0036b bVar2, String str) {
        this.h = bVar;
        this.f2395a = zArr;
        this.f2396b = jArr;
        this.f2397c = zArr2;
        this.f2398d = downloadManager;
        this.e = runnable;
        this.f = bVar2;
        this.g = str;
    }

    public void onReceive(Context context, Intent intent) {
        long longExtra = intent.getLongExtra("extra_download_id", -1);
        boolean z = false;
        int i = 0;
        while (true) {
            if (i >= 5) {
                break;
            }
            boolean[] zArr = this.f2395a;
            if (zArr[i] && this.f2396b[i] == longExtra) {
                zArr[i] = false;
                break;
            }
            i++;
        }
        int i2 = 0;
        while (true) {
            if (i2 >= 5) {
                z = true;
                break;
            } else if (this.f2395a[i2]) {
                break;
            } else {
                i2++;
            }
        }
        if (z) {
            Log.e("SmsEngineUpdateManager", " download finish !");
            this.h.a(this.f2397c, this.f2396b, this.f2398d, context, this.e, this.f, this.g);
            context.getApplicationContext().unregisterReceiver(this);
        }
    }
}
