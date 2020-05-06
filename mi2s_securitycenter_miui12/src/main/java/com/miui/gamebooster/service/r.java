package com.miui.gamebooster.service;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import b.b.c.f.a;
import b.b.c.h.e;
import b.b.c.h.j;
import b.b.c.j.B;
import b.b.c.j.f;
import com.google.android.exoplayer2.util.MimeTypes;
import com.miui.gamebooster.d.b;
import com.miui.gamebooster.gbservices.C;
import com.miui.gamebooster.gbservices.C0358a;
import com.miui.gamebooster.gbservices.C0369l;
import com.miui.gamebooster.gbservices.E;
import com.miui.gamebooster.gbservices.L;
import com.miui.gamebooster.gbservices.m;
import com.miui.gamebooster.gbservices.n;
import com.miui.gamebooster.gbservices.o;
import com.miui.gamebooster.gbservices.p;
import com.miui.gamebooster.gbservices.q;
import com.miui.gamebooster.gbservices.x;
import com.miui.gamebooster.m.C0373d;
import com.miui.gamebooster.m.C0382m;
import com.miui.gamebooster.m.C0384o;
import com.miui.gamebooster.m.C0388t;
import com.miui.gamebooster.m.D;
import com.miui.gamebooster.m.H;
import com.miui.gamebooster.m.S;
import com.miui.gamebooster.m.ma;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.powerkeeper.feedbackcontrol.IFeedbackControl;
import com.miui.securitycenter.h;
import com.xiaomi.stat.MiStat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import org.json.JSONObject;

public class r {

    /* renamed from: a  reason: collision with root package name */
    private static r f4830a;
    private ContentObserver A = new o(this, this.f4833d);
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public CopyOnWriteArrayList<m> f4831b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public Context f4832c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public Handler f4833d;
    private Handler e;
    private boolean f = true;
    private boolean g = false;
    /* access modifiers changed from: private */
    public int h;
    private PackageManager i;
    private AudioManager j;
    private a k;
    private String l;
    private int m;
    private long n;
    /* access modifiers changed from: private */
    public int o = 131072;
    /* access modifiers changed from: private */
    public int p = 30000;
    /* access modifiers changed from: private */
    public long q = 600000;
    /* access modifiers changed from: private */
    public IFeedbackControl r;
    /* access modifiers changed from: private */
    public E s;
    private b t = b.GAME;
    private ArrayList<String> u;
    /* access modifiers changed from: private */
    public String[] v;
    /* access modifiers changed from: private */
    public IFreeformWindow w;
    /* access modifiers changed from: private */
    public ServiceConnection x = new C0411l(this);
    private ContentObserver y = new C0412m(this, this.f4833d);
    private ContentObserver z = new n(this, this.f4833d);

    class a implements Runnable {
        a() {
        }

        public void run() {
            try {
                if (h.i() && f.c(r.this.f4832c)) {
                    JSONObject jSONObject = new JSONObject(e.b(r.this.f4832c, com.miui.gamebooster.d.a.f4247b, (JSONObject) null, DeviceUtil.getImeiMd5(), new j("gamebooster_gameboosterservicemanager")));
                    H.a(r.this.f4832c, jSONObject.getInt("speedValue"), jSONObject.getInt("restrictTime"), jSONObject.getInt("queryTime"), jSONObject.getInt("backstageTime"));
                    com.miui.common.persistence.b.b("game_booster_networkping_url", jSONObject.getString("gbPingUrl"));
                    r.this.u();
                    Log.i("GameBoosterService", MiStat.Param.VALUE + r.this.o + " " + r.this.p + " " + r.this.q);
                }
            } catch (Exception e) {
                Log.e("GameBoosterService", "loadlimitparamsfromnet failed!" + e.toString());
            }
        }
    }

    private r(Context context, Handler handler) {
        this.f4832c = context;
        this.f4833d = handler;
        this.e = ((GameBoosterService) context).b();
        t();
        l();
        a(context);
        com.miui.gamebooster.mutiwindow.a.a(this.f4832c).a((a.C0027a) new p(this));
        this.j = (AudioManager) this.f4832c.getSystemService(MimeTypes.BASE_TYPE_AUDIO);
    }

    public static synchronized r a(Context context, Handler handler) {
        r rVar;
        synchronized (r.class) {
            if (f4830a == null) {
                f4830a = new r(context, handler);
            }
            rVar = f4830a;
        }
        return rVar;
    }

    private void a(Context context) {
        Intent intent = new Intent();
        intent.setClassName("com.miui.powerkeeper", "com.miui.powerkeeper.feedbackcontrol.FeedbackControlService");
        context.bindService(intent, this.x, 1);
    }

    /* access modifiers changed from: private */
    public void a(m mVar) {
        if (mVar.b()) {
            this.f4831b.add(mVar);
        }
    }

    private void c(boolean z2) {
        try {
            if (this.w != null) {
                this.w.c(z2);
            }
        } catch (Exception unused) {
        }
    }

    /* access modifiers changed from: private */
    public void s() {
        if (C0384o.c(this.f4832c.getContentResolver(), "diving_mode", 0, -2) == 1) {
            S.d(this.f4832c);
            com.miui.gamebooster.c.a.E(true);
            return;
        }
        S.a(this.f4832c);
        com.miui.gamebooster.c.a.E(false);
    }

    private void t() {
        this.i = this.f4832c.getPackageManager();
        this.f4831b = new CopyOnWriteArrayList<>();
        this.s = new E(this.f4832c, this);
        a((m) new q(this.f4832c, this));
        a((m) new L(this.f4832c, this));
        a((m) new C0358a(this.f4832c, this));
        a((m) new C0369l(this.f4832c, this));
        a((m) new p(this.f4832c, this));
        a((m) new C(this.f4832c, this));
        a((m) new x(this.f4832c, this));
        a((m) new n(this.f4832c, this));
        a((m) new o(this.f4832c, this));
        a((m) new com.miui.gamebooster.gbservices.r(this.f4832c, this));
    }

    /* access modifiers changed from: private */
    public void u() {
        Map<String, Integer> a2 = H.a(this.f4832c);
        this.o = a2.get("game_booster_limit_speed").intValue();
        this.p = a2.get("game_booster_limit_time").intValue();
        this.q = (long) a2.get("game_booster_close_service_time").intValue();
    }

    public m a(int i2) {
        if (q.f4829a[this.t.ordinal()] != 1) {
            return null;
        }
        Iterator<m> it = this.f4831b.iterator();
        while (it.hasNext()) {
            m next = it.next();
            if (next.e() == i2) {
                return next;
            }
        }
        return null;
    }

    public String a() {
        return this.l;
    }

    public void a(long j2) {
        this.n = j2;
    }

    public void a(b bVar) {
        this.t = bVar;
    }

    public void a(String str) {
        if (str != null) {
            this.l = str;
        }
    }

    public void a(boolean z2) {
        this.g = z2;
    }

    public void a(String[] strArr) {
        this.v = strArr;
        a(this.f4832c);
    }

    public b b() {
        return this.t;
    }

    public void b(int i2) {
        m a2 = a(i2);
        if (a2 != null && !this.f) {
            if (8 != i2) {
                a2.a();
            }
            a2.d();
            a2.c();
        }
    }

    public void b(boolean z2) {
        ((GameBoosterService) this.f4832c).a(z2);
    }

    public Handler c() {
        return this.f4833d;
    }

    public void c(int i2) {
        this.m = i2;
    }

    public boolean d() {
        return this.f;
    }

    public int e() {
        return this.m;
    }

    public ArrayList<String> f() {
        return this.u;
    }

    public int g() {
        return this.h;
    }

    public Handler h() {
        return this.e;
    }

    public boolean i() {
        return this.g;
    }

    public void j() {
        this.k = new a();
        this.f4833d.post(this.k);
    }

    public void k() {
        C0384o.b(this.f4832c.getContentResolver(), "gb_notification", 0, -2);
        com.miui.common.persistence.b.b("game_IsAntiMsg", false);
        C0384o.b(this.f4832c.getContentResolver(), "gb_handsfree", 0, -2);
        C0384o.b(this.f4832c.getContentResolver(), "gb_boosting", 0, -2);
        D.a(this.f4832c, false);
        Settings.System.putInt(this.f4832c.getContentResolver(), (String) C0384o.b("android.provider.MiuiSettings$ScreenEffect", "GAME_MODE"), 0);
        C0384o.b(this.f4832c.getContentResolver(), "disable_voicetrigger", 0, -2);
        com.miui.gamebooster.c.a.S(false);
        if (C0388t.w()) {
            C0384o.b(this.f4832c.getContentResolver(), "gb_gwsd", 0, -2);
        }
    }

    public void l() {
        this.u = C0382m.a("xunyou_support", this.f4832c.getApplicationContext());
        ArrayList<String> c2 = C0382m.c("gamebooster", "xunyousupportlist", this.f4832c);
        if (c2 != null && c2.size() > 5) {
            this.u = c2;
        }
    }

    public void m() {
        if (q.f4829a[this.t.ordinal()] == 1) {
            if (com.miui.gamebooster.provider.a.a(this.f4832c, this.l, B.c(this.m), 0)) {
                n();
            }
        }
    }

    public void n() {
        if (this.h == 0) {
            a(this.f4832c);
        }
        if (this.f) {
            Iterator<m> it = this.f4831b.iterator();
            while (it.hasNext()) {
                it.next().d();
            }
            com.miui.common.persistence.b.b("key_currentbooster_pkg_uid", this.l + "," + this.m);
            C0373d.a("game_service_open");
            c(true);
            C0384o.b(this.f4832c.getContentResolver(), "gb_boosting", 1, -2);
            this.f4832c.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("gb_boosting"), true, this.y);
            this.f4832c.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("quick_reply"), true, this.z);
            this.f = false;
            Log.i("GameBoosterService", "start app... value" + this.o + " " + this.p + " ");
            Iterator<m> it2 = this.f4831b.iterator();
            while (it2.hasNext()) {
                it2.next().c();
            }
            if (this.g) {
                c().sendEmptyMessageDelayed(122, 650);
                this.g = false;
            }
            String d2 = ma.d();
            if (!"original".equals(d2)) {
                ma.a(this.j, this.f4832c, d2, this.l, this.m);
                C0373d.A(d2, this.l);
                ma.a(System.currentTimeMillis());
            }
        }
    }

    public void o() {
        if (q.f4829a[this.t.ordinal()] == 1) {
            p();
        }
    }

    public void p() {
        if (!this.f) {
            this.n = SystemClock.elapsedRealtime();
            this.f = true;
            Log.i("GameBoosterService", "game exit app...");
            c().removeMessages(122);
            this.f4832c.sendBroadcast(new Intent("action_toast_booster_fail"));
            Iterator<m> it = this.f4831b.iterator();
            while (it.hasNext()) {
                it.next().a();
            }
            this.f4832c.getContentResolver().unregisterContentObserver(this.y);
            this.f4832c.getContentResolver().unregisterContentObserver(this.z);
            C0384o.b(this.f4832c.getContentResolver(), "gb_boosting", 0, -2);
            c(false);
            com.miui.gamebooster.mutiwindow.f.b();
            String d2 = ma.d();
            if (!"original".equals(d2)) {
                ma.a(this.j, this.f4832c, this.l, this.m);
                long b2 = ma.b();
                if (b2 != 0) {
                    long currentTimeMillis = (System.currentTimeMillis() - b2) / 60000;
                    String str = this.l;
                    C0373d.a(d2, str, currentTimeMillis + "");
                    ma.b(ma.c() + currentTimeMillis);
                }
            }
        }
    }

    public void q() {
        if (q.f4829a[this.t.ordinal()] == 1) {
            Iterator<m> it = this.f4831b.iterator();
            while (it.hasNext()) {
                m next = it.next();
                if ((next instanceof C0369l) && !this.f) {
                    com.miui.gamebooster.c.a.a(this.f4832c);
                    if (C0388t.m()) {
                        com.miui.gamebooster.c.a.A(!com.miui.gamebooster.c.a.b(false));
                    } else {
                        com.miui.gamebooster.c.a.a(this.f4832c);
                        com.miui.gamebooster.c.a.z(!com.miui.gamebooster.c.a.c(false));
                    }
                    if (((C0369l) next).f()) {
                        next.a();
                    }
                    next.d();
                    next.c();
                }
            }
        }
    }

    public void r() {
        c(false);
        com.miui.gamebooster.mutiwindow.a.a(this.f4832c).a();
    }
}
