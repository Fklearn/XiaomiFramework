package com.xiaomi.stat;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import com.miui.networkassistant.config.Constants;
import com.xiaomi.stat.a.c;
import com.xiaomi.stat.a.l;
import com.xiaomi.stat.c.i;
import com.xiaomi.stat.d.k;
import com.xiaomi.stat.d.m;
import com.xiaomi.stat.d.n;
import com.xiaomi.stat.d.r;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class e {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public boolean f8569a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public String f8570b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public String f8571c;

    /* renamed from: d  reason: collision with root package name */
    private Context f8572d;
    /* access modifiers changed from: private */
    public Executor e;
    /* access modifiers changed from: private */
    public long f;
    private Map<String, Long> g;
    private al h;
    /* access modifiers changed from: private */
    public int i;
    /* access modifiers changed from: private */
    public int j;
    /* access modifiers changed from: private */
    public int k;
    /* access modifiers changed from: private */
    public long l;

    public e(Context context, String str, String str2, String str3, boolean z) {
        this.i = 0;
        this.j = 0;
        this.k = 0;
        this.f8569a = false;
        this.f8570b = str3;
        a(context, str, str2, z, (String) null);
    }

    public e(Context context, String str, String str2, boolean z) {
        this(context, str, str2, z, (String) null);
    }

    public e(Context context, String str, String str2, boolean z, String str3) {
        this.i = 0;
        this.j = 0;
        this.k = 0;
        this.f8569a = true;
        a(context, str, str2, z, str3);
    }

    /* access modifiers changed from: private */
    public void a(int i2, int i3, long j2, long j3) {
        this.e.execute(new p(this, i2, i3, j2, j3));
    }

    private void a(Context context, String str, String str2, boolean z, String str3) {
        this.f8572d = context.getApplicationContext();
        ak.a(context.getApplicationContext(), str, str2);
        if (!this.f8569a) {
            str = this.f8570b;
        }
        this.f8571c = str;
        this.e = new ThreadPoolExecutor(0, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue());
        if (this.f8569a) {
            e();
        }
        r.a();
        this.e.execute(new f(this, str3, z));
    }

    private void a(MiStatParams miStatParams, boolean z) {
        if (miStatParams == null || miStatParams.isEmpty()) {
            k.e("set user profile failed: empty property !");
        } else if (c(miStatParams)) {
            this.e.execute(new aa(this, z, miStatParams));
        }
    }

    /* access modifiers changed from: private */
    public void a(l lVar) {
        c.a().a(lVar);
        i.a().d();
    }

    /* access modifiers changed from: private */
    public void a(String str, long j2, long j3) {
        this.e.execute(new u(this, str, j2, j3));
    }

    private void a(String str, String str2, MiStatParams miStatParams, boolean z) {
        if (!n.a(str)) {
            n.e(str);
        } else if (str2 != null && !n.a(str2)) {
            n.e(str2);
        } else if (miStatParams == null || c(miStatParams)) {
            this.e.execute(new y(this, z, str, str2, miStatParams));
        }
    }

    /* access modifiers changed from: private */
    public boolean a(long j2, long j3) {
        if (j2 == -1) {
            return true;
        }
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(j2);
        Calendar instance2 = Calendar.getInstance();
        instance2.setTimeInMillis(j3);
        return (instance.get(1) == instance2.get(1) && instance.get(6) == instance2.get(6)) ? false : true;
    }

    private boolean c(MiStatParams miStatParams) {
        return miStatParams.getClass().equals(MiStatParams.class) && miStatParams.getParamsNumber() <= 30;
    }

    /* access modifiers changed from: private */
    public long d() {
        return r.b();
    }

    private void e() {
        ((Application) this.f8572d).registerActivityLifecycleCallbacks(new r(this));
    }

    /* access modifiers changed from: private */
    public void f() {
        if (this.f8569a) {
            int p = b.p();
            int a2 = com.xiaomi.stat.d.c.a();
            if (p == -1) {
                b.e(a2);
            } else if (p < a2) {
                this.e.execute(new j(this, a2, p));
            }
        }
    }

    static /* synthetic */ int g(e eVar) {
        int i2 = eVar.i;
        eVar.i = i2 + 1;
        return i2;
    }

    /* access modifiers changed from: private */
    public boolean g() {
        return !b.d(this.f8571c) || b.e(this.f8571c) != 2;
    }

    /* access modifiers changed from: private */
    public boolean g(boolean z) {
        if (b.d(this.f8571c)) {
            return b.e(this.f8571c) != 2;
        }
        if (!b.e() || z) {
            return m.b(this.f8572d);
        }
        return true;
    }

    static /* synthetic */ int h(e eVar) {
        int i2 = eVar.j;
        eVar.j = i2 + 1;
        return i2;
    }

    /* access modifiers changed from: private */
    public void h() {
        this.e.execute(new k(this));
    }

    private boolean i() {
        boolean z;
        boolean z2 = (this.f8572d.getApplicationInfo().flags & 1) == 1;
        PackageManager packageManager = this.f8572d.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(this.f8572d.getPackageName(), 64);
            PackageInfo packageInfo2 = packageManager.getPackageInfo(Constants.System.ANDROID_PACKAGE_NAME, 64);
            if (!(packageInfo == null || packageInfo.signatures == null || packageInfo.signatures.length <= 0 || packageInfo2 == null || packageInfo2.signatures == null || packageInfo2.signatures.length <= 0)) {
                z = packageInfo2.signatures[0].equals(packageInfo.signatures[0]);
                return z2 || z;
            }
        } catch (PackageManager.NameNotFoundException unused) {
        }
        z = false;
        if (z2) {
            return true;
        }
    }

    static /* synthetic */ int j(e eVar) {
        int i2 = eVar.k;
        eVar.k = i2 + 1;
        return i2;
    }

    static /* synthetic */ int m(e eVar) {
        int i2 = eVar.i;
        eVar.i = i2 - 1;
        return i2;
    }

    public int a() {
        return b.i();
    }

    public void a(int i2) {
        if (this.f8569a) {
            b.a(i2);
        }
    }

    public void a(HttpEvent httpEvent) {
        if (httpEvent != null) {
            this.e.execute(new m(this, httpEvent));
        }
    }

    public void a(MiStatParams miStatParams) {
        a(miStatParams, false);
    }

    public void a(NetAvailableEvent netAvailableEvent) {
        if (netAvailableEvent != null) {
            this.e.execute(new n(this, netAvailableEvent));
        }
    }

    public void a(String str) {
        if (this.g == null) {
            this.g = new HashMap();
        }
        this.g.put(str, Long.valueOf(SystemClock.elapsedRealtime()));
    }

    public void a(String str, MiStatParams miStatParams) {
        Long l2;
        Map<String, Long> map = this.g;
        if (map != null && (l2 = map.get(str)) != null) {
            this.g.remove(str);
            if (n.a(str)) {
                if (miStatParams == null || c(miStatParams)) {
                    String str2 = str;
                    this.e.execute(new x(this, str2, d(), SystemClock.elapsedRealtime() - l2.longValue(), miStatParams));
                }
            }
        }
    }

    public void a(String str, String str2) {
        a(str, str2, (MiStatParams) null);
    }

    public void a(String str, String str2, MiStatParams miStatParams) {
        a(str, str2, miStatParams, false);
    }

    public void a(Throwable th) {
        a(th, (String) null);
    }

    public void a(Throwable th, String str) {
        a(th, str, true);
    }

    /* access modifiers changed from: package-private */
    public void a(Throwable th, String str, boolean z) {
        if (th != null) {
            this.e.execute(new z(this, th, str, z));
        }
    }

    public void a(boolean z) {
        if (this.f8569a) {
            this.e.execute(new v(this, z));
        }
    }

    public void a(boolean z, String str) {
        if (this.f8569a) {
            this.e.execute(new l(this, z, str));
        }
    }

    public boolean a(boolean z, boolean z2) {
        if (!i()) {
            return false;
        }
        b.f(z);
        b.g(z2);
        return true;
    }

    public int b() {
        return b.j();
    }

    public void b(int i2) {
        if (this.f8569a) {
            b.b(i2);
        }
    }

    public void b(MiStatParams miStatParams) {
        a(miStatParams, true);
    }

    public void b(String str) {
        a(str, (MiStatParams) null);
    }

    public void b(String str, MiStatParams miStatParams) {
        a(str, (String) null, miStatParams);
    }

    public void b(String str, String str2) {
        b(str, str2, (MiStatParams) null);
    }

    public void b(String str, String str2, MiStatParams miStatParams) {
        a(str, str2, miStatParams, true);
    }

    public void b(boolean z) {
        if (this.f8569a) {
            this.e.execute(new w(this, z));
        }
    }

    public String c() {
        FutureTask futureTask = new FutureTask(new o(this));
        com.xiaomi.stat.b.e.a().execute(futureTask);
        try {
            return (String) futureTask.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException unused) {
            return null;
        }
    }

    public void c(String str) {
        a(str, (String) null, (MiStatParams) null);
    }

    public void c(String str, MiStatParams miStatParams) {
        a(str, (String) null, miStatParams);
    }

    public void c(String str, String str2) {
        if (!n.a(str)) {
            n.e(str);
        } else if (!n.b(str2)) {
            n.f(str2);
        } else {
            MiStatParams miStatParams = new MiStatParams();
            miStatParams.putString(str, str2);
            a(miStatParams);
        }
    }

    public void c(boolean z) {
        if (this.f8569a) {
            b.d(z);
            al alVar = this.h;
            if (alVar != null) {
                alVar.a(z);
            } else if (z) {
                this.h = new al(this);
                this.h.a();
            }
        }
    }

    public void d(String str) {
        b(str, (String) null, (MiStatParams) null);
    }

    public void d(String str, String str2) {
        if (!n.a(str)) {
            n.e(str);
        } else if (!n.b(str2)) {
            n.f(str2);
        } else {
            MiStatParams miStatParams = new MiStatParams();
            miStatParams.putString(str, str2);
            b(miStatParams);
        }
    }

    public void d(boolean z) {
        this.e.execute(new i(this, z));
    }

    public void e(String str) {
        if (this.f8569a) {
            this.e.execute(new h(this, str));
        }
    }

    public void e(String str, String str2) {
        if (!n.a(str)) {
            n.e(str);
        } else if (!n.d(str2)) {
            k.e("invalid plain text value for event: " + str);
        } else {
            this.e.execute(new q(this, str, str2));
        }
    }

    public boolean e(boolean z) {
        return a(z, false);
    }

    public void f(boolean z) {
        k.a(z);
    }
}
