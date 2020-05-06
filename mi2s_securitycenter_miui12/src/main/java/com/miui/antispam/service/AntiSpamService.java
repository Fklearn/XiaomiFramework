package com.miui.antispam.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import com.miui.antispam.service.b;
import java.util.HashMap;
import java.util.Map;

public class AntiSpamService extends Service {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public static final String f2389a = "AntiSpamService";

    /* renamed from: b  reason: collision with root package name */
    public static String f2390b = "timingUpdate";
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public Map<String, b> f2391c = new HashMap();
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public int f2392d = 0;
    public a e;
    /* access modifiers changed from: private */
    public b.a f = new a(this);

    public class a extends Binder {
        public a() {
        }

        public synchronized b a(String str) {
            synchronized (AntiSpamService.this.f2391c) {
                if (AntiSpamService.this.f2391c.containsKey(str)) {
                    b bVar = (b) AntiSpamService.this.f2391c.get(str);
                    return bVar;
                }
                if ("CloudPhoneList".equals(str)) {
                    AntiSpamService.this.f2391c.put(str, new com.miui.antispam.service.b.b(AntiSpamService.this, AntiSpamService.this.f));
                }
                b bVar2 = (b) AntiSpamService.this.f2391c.get(str);
                return bVar2;
            }
        }

        public synchronized void b(String str) {
            synchronized (AntiSpamService.this.f2391c) {
                if (AntiSpamService.this.f2391c.containsKey(str) && ((b) AntiSpamService.this.f2391c.get(str)).a(false)) {
                    AntiSpamService.this.f2391c.remove(str);
                }
            }
        }
    }

    static /* synthetic */ int c(AntiSpamService antiSpamService) {
        int i = antiSpamService.f2392d;
        antiSpamService.f2392d = i + 1;
        return i;
    }

    static /* synthetic */ int d(AntiSpamService antiSpamService) {
        int i = antiSpamService.f2392d;
        antiSpamService.f2392d = i - 1;
        return i;
    }

    public IBinder onBind(Intent intent) {
        return this.e;
    }

    public void onCreate() {
        super.onCreate();
        this.e = new a();
        Log.i(f2389a, "service create");
    }

    public void onDestroy() {
        Log.i(f2389a, "service onDestroy");
        super.onDestroy();
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        if (intent == null) {
            return super.onStartCommand(intent, i, i2);
        }
        if (f2390b.equals(intent.getAction())) {
            ((com.miui.antispam.service.b.b) this.e.a("CloudPhoneList")).e();
            this.e.b("CloudPhoneList");
        }
        return super.onStartCommand(intent, i, i2);
    }
}
