package com.miui.gamebooster.mutiwindow;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;
import com.miui.gamebooster.m.C0375f;
import com.miui.gamebooster.mutiwindow.l;
import com.miui.gamebooster.service.IFreeformWindow;
import java.util.List;

public class FreeformWindowService extends Service {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public d f4617a;

    /* renamed from: b  reason: collision with root package name */
    private Handler f4618b;

    /* renamed from: c  reason: collision with root package name */
    private Handler f4619c;

    /* renamed from: d  reason: collision with root package name */
    private HandlerThread f4620d;
    private FreeformWindowHandlerBinder e;

    public class FreeformWindowHandlerBinder extends IFreeformWindow.Stub {
        public FreeformWindowHandlerBinder() {
        }

        public void c(boolean z) {
            Log.i("FreeformWindowService", "setGameBoosterMode: " + z);
            if (FreeformWindowService.this.f4617a != null) {
                FreeformWindowService.this.f4617a.a(z);
            }
        }

        public void d(List<String> list) {
            Log.i("FreeformWindowService", "setQuickReplyApps: " + list);
            if (FreeformWindowService.this.f4617a != null) {
                FreeformWindowService.this.f4617a.a(list);
            }
        }
    }

    public static void a(Context context) {
        Log.i("FreeformWindowService", "startProcessMonitorService");
        try {
            context.startService(new Intent(context, FreeformWindowService.class));
        } catch (Exception e2) {
            Log.e("FreeformWindowService", "startProcessMonitorService: " + e2.toString());
        }
    }

    public IBinder onBind(Intent intent) {
        return this.e;
    }

    public void onCreate() {
        super.onCreate();
        l.a().b();
        this.f4618b = new Handler(getMainLooper());
        this.f4620d = new HandlerThread("freeform_window_bg_service");
        this.f4620d.start();
        this.f4619c = new Handler(this.f4620d.getLooper());
        this.f4617a = new d(this, this.f4619c);
        if (!f.c()) {
            l.a().a((l.a) this.f4617a);
        }
        this.e = new FreeformWindowHandlerBinder();
        if (!C0375f.a()) {
            i a2 = i.a((Context) this);
            a2.a(this.f4619c);
            a2.b();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        l a2 = l.a();
        a2.c();
        d dVar = this.f4617a;
        if (dVar != null) {
            dVar.b();
            if (!f.c()) {
                a2.b(this.f4617a);
            }
        }
        if (!C0375f.a()) {
            i.a((Context) this).c();
        }
    }
}
