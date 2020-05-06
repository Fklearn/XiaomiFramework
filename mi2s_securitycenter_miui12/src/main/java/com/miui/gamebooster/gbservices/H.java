package com.miui.gamebooster.gbservices;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.f.a;
import com.miui.gamebooster.b.a.a;
import com.miui.gamebooster.m.C0384o;
import com.miui.gamebooster.m.E;
import com.miui.gamebooster.service.GameBoxWindowManagerService;
import com.miui.gamebooster.service.IFreeformWindow;
import com.miui.gamebooster.service.IGameBoosterWindow;
import com.miui.gamebooster.videobox.settings.f;
import com.miui.gamebooster.videobox.utils.e;
import com.miui.luckymoney.stats.MiStatUtil;

public class H implements a.C0046a {

    /* renamed from: a  reason: collision with root package name */
    private static H f4327a;

    /* renamed from: b  reason: collision with root package name */
    private boolean f4328b;

    /* renamed from: c  reason: collision with root package name */
    private boolean f4329c;

    /* renamed from: d  reason: collision with root package name */
    public IGameBoosterWindow f4330d;
    private final Object e = new Object();
    /* access modifiers changed from: private */
    public Context f;
    private a g = new a((F) null);
    private boolean h = false;
    /* access modifiers changed from: private */
    public Handler i;
    /* access modifiers changed from: private */
    public IFreeformWindow j;
    private ServiceConnection k = new F(this);
    private ContentObserver l;

    private static class a {

        /* renamed from: a  reason: collision with root package name */
        public String f4331a;

        /* renamed from: b  reason: collision with root package name */
        public int f4332b;

        private a() {
        }

        /* synthetic */ a(F f) {
            this();
        }

        public String a() {
            if (TextUtils.isEmpty(this.f4331a)) {
                this.f4331a = "";
            }
            return this.f4331a + "," + this.f4332b;
        }

        public void a(String str, int i) {
            this.f4331a = str;
            this.f4332b = i;
        }
    }

    private class b extends ContentObserver {
        public b(Handler handler) {
            super(handler);
        }

        public void onChange(boolean z) {
            super.onChange(z);
            if (C0384o.a(H.this.f.getContentResolver(), "quick_reply", 0, -2) == 0 && H.this.i != null) {
                H.this.i.sendEmptyMessage(3);
            }
        }
    }

    private class c implements Runnable {

        /* renamed from: a  reason: collision with root package name */
        private int f4334a;

        public c(int i) {
            this.f4334a = i;
        }

        public void run() {
            try {
                int i = this.f4334a;
                if (i != 0) {
                    if (i == 1) {
                        if (H.this.f4330d != null) {
                            H.this.f4330d.a(false, false);
                        }
                        E.a();
                    }
                } else if (H.this.f4330d != null) {
                    H.this.f4330d.a(true, false);
                }
            } catch (Exception unused) {
            }
        }
    }

    private H(Context context, Handler handler) {
        this.f = context.getApplicationContext();
        this.i = handler;
        this.l = new b(this.i);
        com.miui.gamebooster.mutiwindow.a.a(this.f).a((a.C0027a) new G(this));
    }

    public static synchronized H a(Context context, Handler handler) {
        H h2;
        synchronized (H.class) {
            if (f4327a == null) {
                f4327a = new H(context, handler);
            }
            h2 = f4327a;
        }
        return h2;
    }

    private void b(boolean z) {
        try {
            if (this.j != null) {
                this.j.c(z);
            }
        } catch (Exception unused) {
        }
    }

    public void a() {
        Log.i("VideoBoxServiceManager", "VideoBoxServiceManager: Open");
        synchronized (this.e) {
            Log.i("VideoBoxServiceManager", "open");
            Intent intent = new Intent(this.f, GameBoxWindowManagerService.class);
            intent.setAction("com.miui.gamebooster.service.GameBoxService");
            intent.putExtra("intent_gamebooster_window_type", 3);
            intent.putExtra("intent_gamebooster_coldstart", this.f4329c);
            intent.putExtra("intent_booster_type", "intent_booster_type_video_all");
            this.f4328b = this.f.bindService(intent, this.k, 1);
        }
    }

    public void a(String str, int i2) {
        this.g.a(str, i2);
    }

    public void a(boolean z) {
        this.f4329c = z;
    }

    public void b() {
        Log.i("VideoBoxServiceManager", "startVideoBox: isDuringVideoBoxMode=" + this.h);
        if (!this.h) {
            f.f(true);
            com.miui.common.persistence.b.b("key_currentbooster_pkg_uid", this.g.a());
            f.a(this.g.f4331a);
            if (!e.c()) {
                b(true);
            }
            this.f.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("quick_reply"), true, this.l);
            this.h = true;
            com.miui.gamebooster.videobox.utils.f.a(true);
            C0384o.b(this.f.getContentResolver(), "gb_boosting", 1, -2);
            a();
            com.miui.gamebooster.b.a.a.a((a.C0046a) this, com.miui.gamebooster.d.b.VIDEO_ALL);
        }
    }

    public void c() {
        if (this.h) {
            this.h = false;
            Log.i("VideoBoxServiceManager", "video box exit app...");
            this.f.getContentResolver().unregisterContentObserver(this.l);
            if (!e.c()) {
                b(false);
            }
            f.f(false);
            f.a(this.g.f4331a);
            C0384o.b(this.f.getContentResolver(), "gb_boosting", 0, -2);
            d();
            com.miui.gamebooster.b.a.a.a();
        }
    }

    public void d() {
        synchronized (this.e) {
            Log.i("VideoBoxServiceManager", MiStatUtil.CLOSE);
            try {
                if (this.f4328b) {
                    this.f.unbindService(this.k);
                    this.f4328b = false;
                }
            } catch (Exception e2) {
                Log.e("VideoBoxServiceManager", "unbind error:" + e2);
            }
        }
    }

    public void onSlideChanged(int i2) {
        Handler handler = this.i;
        if (handler != null) {
            handler.post(new c(i2));
        }
    }
}
