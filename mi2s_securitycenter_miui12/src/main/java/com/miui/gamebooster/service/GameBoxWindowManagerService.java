package com.miui.gamebooster.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.hardware.display.DisplayManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import b.b.c.f.a;
import b.b.l.b;
import com.miui.gamebooster.f.d;
import com.miui.gamebooster.m.C0387s;
import com.miui.gamebooster.m.C0390v;
import com.miui.gamebooster.m.F;
import com.miui.gamebooster.m.N;
import com.miui.gamebooster.m.ja;
import com.miui.gamebooster.m.na;
import com.miui.gamebooster.p.c;
import com.miui.gamebooster.p.r;
import com.miui.gamebooster.service.IGameBoosterWindow;
import com.miui.gamebooster.videobox.utils.e;
import com.miui.gamebooster.view.k;
import com.miui.networkassistant.config.Constants;
import com.miui.securitycenter.R;
import com.xiaomi.migameservice.IGameCenterInterface;
import com.xiaomi.migameservice.IGameServiceCallback;

public class GameBoxWindowManagerService extends Service {

    /* renamed from: a  reason: collision with root package name */
    private DisplayManager f4772a;

    /* renamed from: b  reason: collision with root package name */
    private GameBoosterWindowBinder f4773b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public r f4774c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public c f4775d;
    public boolean e;
    public boolean f;
    /* access modifiers changed from: private */
    public Handler g;
    /* access modifiers changed from: private */
    public int h;
    /* access modifiers changed from: private */
    public int i;
    private String j;
    /* access modifiers changed from: private */
    public IGameBooster k;
    /* access modifiers changed from: private */
    public String l;
    /* access modifiers changed from: private */
    public boolean m = true;
    private Runnable n = new y(this);
    /* access modifiers changed from: private */
    public Runnable o = new z(this);
    private Runnable p = new A(this);
    private Runnable q = new B(this);
    /* access modifiers changed from: private */
    public IGameServiceCallback.Stub r = new C(this);
    private ServiceConnection s = new D(this);
    /* access modifiers changed from: private */
    public IGameCenterInterface t;
    private BroadcastReceiver u = new E(this);
    private DisplayManager.DisplayListener v = new F(this);
    a.C0027a w = new G(this);

    public class GameBoosterWindowBinder extends IGameBoosterWindow.Stub {
        public GameBoosterWindowBinder() {
        }

        public void D() {
        }

        public void a(boolean z, boolean z2) {
            GameBoxWindowManagerService.this.g.post(new H(this, z, z2));
        }
    }

    /* access modifiers changed from: private */
    public void a(Context context, int i2, boolean z) {
        com.miui.gamebooster.c.a.E(z);
        Toast.makeText(context, context.getResources().getString(i2), 1).show();
    }

    /* access modifiers changed from: private */
    public void a(Intent intent) {
        try {
            boolean booleanExtra = intent.getBooleanExtra("IsFinished", true);
            if (this.f4774c != null) {
                this.f4774c.b(booleanExtra);
            }
        } catch (Exception unused) {
        }
    }

    private void j() {
        b.b().e(this.l);
        C0387s.b().d();
    }

    /* access modifiers changed from: private */
    public String k() {
        String a2 = com.miui.common.persistence.b.a("key_currentbooster_pkg_uid", (String) null);
        return a2.contains("com.tencent.tmgp.sgame") ? "kpl" : a2.contains("com.tencent.tmgp.pubgmhd") ? "pubg" : "";
    }

    private void l() {
        com.miui.gamebooster.m.a.a.a(this.h);
        int b2 = com.miui.gamebooster.m.a.a.b();
        int a2 = com.miui.gamebooster.m.a.b.a(this.h);
        if (b2 != 0 && a2 == b2) {
            N.a(getApplicationContext()).g();
        }
    }

    /* access modifiers changed from: private */
    public void m() {
        if (this.e) {
            this.f4774c.h();
            this.f4774c.j();
            this.f4774c.b();
        }
    }

    /* access modifiers changed from: private */
    public void n() {
        if (this.e) {
            this.f4774c.h();
            this.f4774c.a(true, true);
        }
    }

    /* access modifiers changed from: private */
    public void o() {
        this.f4775d.b();
        c cVar = this.f4775d;
        boolean z = true;
        if (na.g(this) != 1) {
            z = false;
        }
        cVar.a(z);
    }

    private void p() {
        N a2 = N.a(getApplicationContext());
        if (a2.b() && !a2.e() && com.miui.gamebooster.m.a.a.b() != 0) {
            Log.i("GameBoxWindowManager", "Small Window Screening do not disconnect!!!");
            a2.a(this.h == 3 ? R.string.vtb_stop_milink_connect : R.string.stop_milink_connect);
        }
        a2.a();
        com.miui.gamebooster.m.a.a.a(0);
    }

    private void q() {
        IGameCenterInterface iGameCenterInterface = this.t;
        if (iGameCenterInterface != null) {
            try {
                iGameCenterInterface.q();
                this.t.w();
                this.t.a(this.r);
                getApplicationContext().unbindService(this.s);
            } catch (Exception e2) {
                Log.e("GameBoxWindowManager", "stop service", e2);
            }
        }
        r rVar = this.f4774c;
        if (rVar != null) {
            rVar.g();
        }
        k.b();
    }

    public void a() {
        try {
            if (this.t != null && !TextUtils.isEmpty(this.l)) {
                this.t.w();
            }
        } catch (RemoteException e2) {
            Log.e("GameBoxWindowManager", "close ai", e2);
        }
    }

    public void b() {
        try {
            if (this.t != null && !TextUtils.isEmpty(this.l)) {
                this.t.q();
            }
        } catch (RemoteException e2) {
            Log.e("GameBoxWindowManager", "close manual", e2);
        }
    }

    public String c() {
        return this.l;
    }

    public String d() {
        return this.j;
    }

    public boolean e() {
        return this.t != null;
    }

    public void f() {
        try {
            if (this.t != null && !TextUtils.isEmpty(this.l)) {
                this.t.h(this.l);
            }
        } catch (RemoteException e2) {
            Log.e("GameBoxWindowManager", "open ai", e2);
        }
    }

    public void g() {
        try {
            if (this.t != null && !TextUtils.isEmpty(this.l)) {
                this.t.i(this.l);
            }
        } catch (RemoteException e2) {
            Log.e("GameBoxWindowManager", "open manual", e2);
        }
    }

    public void h() {
        try {
            if (this.t != null && !TextUtils.isEmpty(this.l)) {
                this.t.m(this.l);
            }
        } catch (RemoteException e2) {
            Log.e("GameBoxWindowManager", "save manual", e2);
        }
    }

    public void i() {
        boolean a2 = ja.a("key_gb_record_ai", this.l);
        boolean a3 = ja.a("key_gb_record_manual", this.l);
        if (F.a(this.l) && !na.c()) {
            if (a2 || a3) {
                Intent intent = new Intent();
                intent.setAction("com.xiaomi.migameservice.MiTimeControl");
                intent.setPackage("com.xiaomi.migameservice");
                getApplicationContext().bindService(intent, this.s, 1);
            }
        }
    }

    public IBinder onBind(Intent intent) {
        Log.i("GameBoxWindowManager", "onBind type=" + this.h);
        boolean z = true;
        this.h = intent.getIntExtra("intent_gamebooster_window_type", 1);
        this.f4773b = new GameBoosterWindowBinder();
        this.f = intent.getBooleanExtra("intent_gamebooster_coldstart", false);
        this.l = intent.getStringExtra("intent_gamebooster_game_package");
        this.g = new Handler(Looper.myLooper());
        int i2 = this.h;
        if (i2 == 1) {
            this.g.postDelayed(this.n, 300);
            this.e = true;
            this.f4774c = new r(this, this.g);
            this.j = intent.getStringExtra("intent_booster_type");
        } else if (i2 == 2) {
            this.i = na.g(this);
            this.f4775d = new c(this);
            this.g.postDelayed(this.q, 500);
            this.g.postDelayed(this.p, 500);
            c cVar = this.f4775d;
            if (this.i != 1) {
                z = false;
            }
            cVar.a(z);
            Log.i("GameBoxWindowManager", "WINDOWTYPE_FIRSTENTERDIALOG");
        } else if (i2 == 3) {
            this.g.postDelayed(this.n, 300);
            this.e = true;
            this.f4774c = new r(this, this.g);
            this.j = intent.getStringExtra("intent_booster_type");
            e.b(this);
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.miui.FREEFORM_WINDOW_CLOSED");
        intentFilter.addAction("com.miui.securitycenter.intent.action.NOTIFY_DIVING_MODE_EXCEPTION");
        if (this.h == 3) {
            intentFilter.addAction(Constants.System.ACTION_USER_PRESENT);
            intentFilter.addAction(Constants.System.ACTION_SCREEN_OFF);
        }
        intentFilter.addAction("miui.intent.TAKE_SCREENSHOT");
        registerReceiver(this.u, intentFilter);
        this.f4772a = (DisplayManager) getSystemService("display");
        this.f4772a.registerDisplayListener(this.v, this.g);
        C0390v.a((Context) this).a(this.w);
        d.a(getApplicationContext()).b();
        i();
        j();
        l();
        return this.f4773b;
    }

    public boolean onUnbind(Intent intent) {
        Log.i("GameBoxWindowManager", "unbindService type=" + this.h);
        this.f4772a.unregisterDisplayListener(this.v);
        unregisterReceiver(this.u);
        Handler handler = this.g;
        if (handler != null) {
            handler.removeCallbacks(this.o);
        }
        int i2 = this.h;
        if (i2 == 1) {
            this.e = false;
            this.g.removeCallbacks(this.n);
            this.f4774c.h();
            this.f4774c.j();
        } else if (i2 == 2) {
            this.g.removeCallbacks(this.q);
            this.f4775d.b();
        } else if (i2 == 3) {
            this.e = false;
            this.g.removeCallbacks(this.n);
            this.f4774c.h();
            this.f4774c.j();
            e.a(this);
        }
        C0390v.a((Context) this).a();
        p();
        d.a(getApplicationContext()).c();
        q();
        return super.onUnbind(intent);
    }
}
