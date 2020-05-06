package com.miui.gamebooster.m;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import b.b.o.g.e;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import com.milink.api.v1.MiLinkClientScanListCallback;
import com.milink.api.v1.MilinkClientManager;
import com.miui.gamebooster.c.a;
import com.miui.gamebooster.d;
import com.miui.gamebooster.m.a.b;

public class N {

    /* renamed from: a  reason: collision with root package name */
    private static N f4454a;

    /* renamed from: b  reason: collision with root package name */
    private MiLinkClientScanListCallback f4455b;

    /* renamed from: c  reason: collision with root package name */
    private MilinkClientManager f4456c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public boolean f4457d = false;
    /* access modifiers changed from: private */
    public int e;
    /* access modifiers changed from: private */
    public Context f;
    /* access modifiers changed from: private */
    public Handler g;
    /* access modifiers changed from: private */
    public d h;
    /* access modifiers changed from: private */
    public String i = ((String) C0384o.b("android.provider.MiuiSettings$Secure", "SCREEN_PROJECT_IN_SCREENING"));
    /* access modifiers changed from: private */
    public String j = ((String) C0384o.b("android.provider.MiuiSettings$Secure", "SCREEN_PROJECT_PRIVACY_ON"));
    private ContentObserver k;
    /* access modifiers changed from: private */
    public boolean l = false;
    /* access modifiers changed from: private */
    public int m;

    private N(Context context) {
        this.f = context;
        this.g = new J(this, Looper.getMainLooper());
        this.f4456c = new MilinkClientManager(context);
        this.f4456c.setDelegate(new K(this));
        this.k = new L(this, this.g);
        this.f.getContentResolver().registerContentObserver(Settings.Secure.getUriFor(this.i), true, this.k);
        this.f4455b = new M(this);
    }

    public static synchronized N a(Context context) {
        N n;
        synchronized (N.class) {
            if (f4454a == null) {
                f4454a = new N(context);
            }
            n = f4454a;
        }
        return n;
    }

    /* access modifiers changed from: private */
    public void i() {
        this.f4457d = false;
        C0384o.b(this.f.getContentResolver(), this.j, this.e, -2);
        a.S(false);
        d dVar = this.h;
        if (dVar != null) {
            dVar.a();
        }
        a();
        Log.i("MiLinkUtils", "onConnectFailAndClose");
    }

    public void a() {
        if (this.l) {
            Log.i("MiLinkUtils", "Close");
            try {
                this.f4456c.close();
            } catch (Exception e2) {
                Log.i("MiLinkUtils", e2.toString());
            }
            this.l = false;
        }
    }

    public void a(int i2) {
        Log.i("MiLinkUtils", "dismissScanList");
        try {
            e.a((Object) this.f4456c, "dismissScanList", (Class<?>[]) null, new Object[0]);
            e.a((Object) this.f4456c, "disconnectWifiDisplay", (Class<?>[]) null, new Object[0]);
            this.f4457d = false;
            com.miui.gamebooster.m.a.a.b(0);
            C0384o.b(this.f.getContentResolver(), this.j, this.e, -2);
            a.S(false);
            Toast.makeText(this.f, this.f.getResources().getString(i2), 0).show();
            if (this.h != null) {
                this.h.a();
            }
            a();
        } catch (Exception e2) {
            Log.e("MiLinkUtils", e2.toString());
        }
    }

    public void a(d dVar) {
        this.h = dVar;
    }

    public boolean b() {
        return this.f4457d;
    }

    public String c() {
        String string = Settings.Secure.getString(this.f.getContentResolver(), "screen_project_caller");
        return string == null ? "" : string;
    }

    public boolean d() {
        if (!(C0384o.a(this.f.getContentResolver(), this.i, 0, -2) == 1)) {
            return false;
        }
        String c2 = c();
        return TextUtils.isEmpty(c2) ? !this.f4457d : !"com.miui.securitycenter:ui".equals(c2) || com.miui.gamebooster.m.a.a.b() != b.a(com.miui.gamebooster.m.a.a.a());
    }

    public boolean e() {
        return C0384o.a(this.f.getContentResolver(), "screen_project_small_window_on", 0, -2) == 1;
    }

    public void f() {
        if (this.l) {
            h();
            return;
        }
        Log.i("MiLinkUtils", "mOpen");
        this.f4456c.open();
    }

    public boolean g() {
        if (!(C0384o.a(this.f.getContentResolver(), this.i, 0, -2) == 1)) {
            return false;
        }
        if ("com.miui.securitycenter:ui".equals(c())) {
            this.g.sendEmptyMessage(TsExtractor.TS_STREAM_TYPE_AC3);
            return true;
        }
        Log.i("MiLinkUtils", "resumeConnectStateIfNeed: MiLinkState=" + this.f4457d);
        return false;
    }

    public void h() {
        try {
            this.m = b.a(com.miui.gamebooster.m.a.a.a());
            Log.i("MiLinkUtils", "showScanList : " + this.m);
            e.a((Object) this.f4456c, "showScanList", (Class<?>[]) new Class[]{MiLinkClientScanListCallback.class, Integer.TYPE}, this.f4455b, 1);
        } catch (Exception e2) {
            Log.e("MiLinkUtils", e2.toString());
        }
    }
}
