package com.miui.powercenter.quickoptimize;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import b.b.c.j.d;
import com.miui.permcenter.s;
import com.miui.powercenter.batteryhistory.C0501e;
import com.miui.powercenter.batteryhistory.C0514s;
import com.miui.powercenter.batteryhistory.C0520y;
import com.miui.powercenter.batteryhistory.aa;
import com.miui.powercenter.c.a;
import com.miui.powercenter.c.f;
import com.miui.powercenter.quickoptimize.z;
import com.miui.powercenter.utils.b;
import com.miui.powercenter.utils.g;
import com.miui.powercenter.utils.n;
import com.miui.powercenter.utils.o;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.List;

public class v {

    /* renamed from: a  reason: collision with root package name */
    private static List<String> f7265a = new ArrayList();

    /* renamed from: b  reason: collision with root package name */
    private static List<m> f7266b = new ArrayList();

    /* renamed from: c  reason: collision with root package name */
    private static List<m> f7267c = new ArrayList();

    /* renamed from: d  reason: collision with root package name */
    private static List<m> f7268d = new ArrayList();
    private static List<String> e = new ArrayList();
    private static v f = null;
    /* access modifiers changed from: private */
    public boolean g = false;
    private long h = 0;
    private long i = 0;
    private long j = -1;
    private long k = -1;
    private long l = -1;
    private long m = -1;
    private long n = -1;
    private boolean o;
    private boolean p;
    private boolean q;
    private boolean r;

    public interface a {
        void a(a.C0063a aVar);

        void a(f.a aVar, boolean z);

        void c();

        void d();

        boolean isCancelled();
    }

    private v() {
    }

    private int a(Context context, int i2) {
        int d2 = d(context, i2);
        if (d2 != 3) {
            m mVar = new m();
            mVar.f7231a = i2;
            mVar.f7232b = c(context, i2);
            (d2 == 2 ? f7267c : f7266b).add(mVar);
        }
        return d2;
    }

    private int a(List<m> list) {
        int i2 = 0;
        for (m a2 : list) {
            i2 += o.a(a2);
        }
        return i2;
    }

    private List<com.miui.powercenter.f.a> a(Context context, List<String> list) {
        ArrayList arrayList = new ArrayList();
        for (String next : list) {
            com.miui.powercenter.f.a aVar = new com.miui.powercenter.f.a();
            aVar.f7062a = next;
            aVar.f7063b = b.a(context, next);
            arrayList.add(aVar);
        }
        return arrayList;
    }

    /* access modifiers changed from: private */
    public void a(a aVar, Context context) {
        if (aVar.isCancelled()) {
            aVar.d();
            this.g = false;
        } else if (m()) {
            z.b(context, (z.a) new u(this, aVar, context));
        } else {
            a(aVar, context, (List<String>) new ArrayList());
        }
    }

    /* access modifiers changed from: private */
    public void a(a aVar, Context context, List<String> list) {
        synchronized (this) {
            if (aVar.isCancelled()) {
                aVar.d();
                this.g = false;
                return;
            }
            f7265a.clear();
            if (!list.isEmpty()) {
                List<String> a2 = C0522a.a(context);
                for (String next : list) {
                    if (!a2.contains(next)) {
                        f7265a.add(next);
                    }
                }
            }
            m mVar = new m();
            mVar.f7231a = 1;
            mVar.f7232b = context.getResources().getString(R.string.power_optimize_close_app);
            mVar.f7233c = a(context, f7265a);
            if (f7265a.isEmpty()) {
                f7267c.add(0, mVar);
                aVar.a(f.a.RUNNINGAPP, false);
            } else {
                f7266b.add(0, mVar);
                aVar.a(f.a.RUNNINGAPP, true);
            }
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("com.miui.powercenter.action.LOAD_OPTIMIZE_TASK"));
            aVar.c();
            com.miui.powercenter.a.a.d(c());
            for (String f2 : f7265a) {
                com.miui.powercenter.a.a.f(f2);
            }
            com.miui.powercenter.a.a.b((SystemClock.elapsedRealtime() - this.i) / 1000);
            this.g = false;
        }
    }

    public static synchronized v b() {
        v vVar;
        synchronized (v.class) {
            if (f == null) {
                f = new v();
            }
            vVar = f;
        }
        return vVar;
    }

    /* access modifiers changed from: private */
    public void b(Context context, a aVar) {
        try {
            boolean z = false;
            if (aVar.isCancelled()) {
                aVar.d();
                this.g = false;
                return;
            }
            long currentTimeMillis = System.currentTimeMillis();
            aVar.a(a.C0063a.ABNORMAL);
            aVar.a(f.a.ABNORMALISSUE, false);
            aVar.a(f.a.ABNORMALAPPS, false);
            aVar.a(f.a.BLANK, false);
            aVar.a(f.a.BLANK2, false);
            this.p = false;
            this.k = System.currentTimeMillis() - currentTimeMillis;
            if (aVar.isCancelled()) {
                aVar.d();
                this.g = false;
                return;
            }
            aVar.a(a.C0063a.SYSTEM);
            int a2 = a(context, 2);
            int a3 = a(context, 3);
            int a4 = a(context, 8);
            int a5 = a(context, 6);
            int a6 = a(context, 7);
            int a7 = a(context, 10);
            int a8 = a(context, 11);
            int a9 = a(context, 12);
            aVar.a(f.a.CLEANMEMORY, a2 == 1);
            aVar.a(f.a.BRIGHTNESS, a3 == 1);
            aVar.a(f.a.GPS, a4 == 1);
            if (g.b() && g.a() && !g.a(context)) {
                aVar.a(f.a.FIVEG, a(context, 9) == 1);
            }
            this.q = a2 == 2 && a3 == 2 && a4 == 2 && a5 == 2 && a6 == 2 && a7 == 2 && a8 == 2 && a9 == 2;
            if (aVar.isCancelled()) {
                aVar.d();
                this.g = false;
                return;
            }
            aVar.a(a.C0063a.DETAILS);
            List<aa> b2 = C0514s.c().b();
            int i2 = ((o.k(context) ? C0501e.a(context, b2).f6879a : C0520y.a(context, b2)) > 0 ? 1 : ((o.k(context) ? C0501e.a(context, b2).f6879a : C0520y.a(context, b2)) == 0 ? 0 : -1));
            aVar.a(f.a.DETAILS, i2 < 0);
            this.r = i2 >= 0;
            if (aVar.isCancelled()) {
                aVar.d();
                this.g = false;
                return;
            }
            long currentTimeMillis2 = System.currentTimeMillis();
            aVar.a(a.C0063a.APP);
            int a10 = a(context, 4);
            aVar.a(f.a.AUTOSTART, a10 == 1);
            int a11 = a(context, 5);
            aVar.a(f.a.INOVKE, a11 == 1);
            if (a10 == 2 && a11 == 2) {
                z = true;
            }
            this.o = z;
            this.j = System.currentTimeMillis() - currentTimeMillis2;
        } catch (Exception e2) {
            Log.e("QuickOptimizeManager", "loadTaskListInThread scan item exception: ", e2);
        }
    }

    private boolean b(Context context, int i2) {
        if (i2 == 1 && !f7265a.isEmpty()) {
            z.a(f7265a, b.b.c.j.g.a(context));
            if (b.b.c.j.g.a(context) == 0) {
                List<String> a2 = z.a(context, f7265a);
                if (!a2.isEmpty()) {
                    z.a(a2, 999);
                }
            }
            C0533l.a(SystemClock.elapsedRealtime());
            return true;
        } else if (i2 == 2) {
            o.q(context);
            return true;
        } else if (i2 == 3) {
            n.a(context).a(50);
            return true;
        } else if (i2 == 4) {
            C0522a.b(context, e);
            return true;
        } else if (i2 == 5) {
            for (String a3 : e) {
                s.a(context, a3, false);
            }
            return true;
        } else if (i2 == 8) {
            if (Build.VERSION.SDK_INT > 28) {
                o.a(context, 0);
            } else {
                o.a(context, 2);
            }
            return true;
        } else if (i2 == 9) {
            g.a(false);
            return true;
        } else if (i2 == 6) {
            n.a(context).b(1);
            return true;
        } else if (i2 == 7) {
            o.a(context);
            return true;
        } else if (i2 == 10) {
            o.b(context);
            return true;
        } else if (i2 == 11) {
            n.a(context).d(15);
            return true;
        } else if (i2 != 12) {
            return false;
        } else {
            n.a(context).b(false);
            return true;
        }
    }

    private String c(Context context, int i2) {
        switch (i2) {
            case 2:
                return context.getString(R.string.power_optimize_save_on_lockscreen);
            case 3:
                return context.getString(R.string.power_optimize_brightness);
            case 4:
                return context.getString(R.string.power_optimize_disable_app_autostart);
            case 5:
                return context.getString(R.string.power_optimize_disable_start_other_app);
            case 6:
                return context.getString(R.string.power_center_list_item_auto_brightness);
            case 7:
                return context.getString(R.string.power_center_list_item_haptic_feedback);
            case 8:
                return Build.VERSION.SDK_INT <= 28 ? context.getString(R.string.power_optimize_disable_gps_high_accuracy) : context.getString(R.string.power_optimize_disable_gps);
            case 9:
                return context.getString(R.string.power_optimize_disable_5g);
            case 10:
                return context.getString(R.string.power_center_list_item_wake_notification);
            case 11:
                return context.getString(R.string.power_center_list_item_set_lockscreen_time_holder, new Object[]{15});
            case 12:
                return context.getString(R.string.power_center_list_item_close_bluetooth);
            default:
                return "";
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x002d, code lost:
        if (com.miui.powercenter.utils.g.c(r5) != false) goto L_0x0012;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0048, code lost:
        if (com.miui.powercenter.utils.o.j(r5) != false) goto L_0x0012;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0053, code lost:
        if (com.miui.powercenter.utils.n.a(r5).m() != false) goto L_0x0078;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x006a, code lost:
        if (e.isEmpty() != false) goto L_0x0078;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0075, code lost:
        if (com.miui.powercenter.utils.n.a(r5).c() < 0) goto L_0x0012;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:?, code lost:
        return 2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:?, code lost:
        return 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:3:0x0010, code lost:
        if (com.miui.powercenter.utils.n.a(r5).b() != false) goto L_0x0012;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:6:0x001f, code lost:
        if (com.miui.powercenter.utils.n.a(r5).g() <= 15) goto L_0x0078;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0026, code lost:
        if (com.miui.powercenter.utils.o.o(r5) != false) goto L_0x0012;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int d(android.content.Context r5, int r6) {
        /*
            r4 = this;
            r0 = 3
            r1 = 1
            r2 = 2
            switch(r6) {
                case 2: goto L_0x0078;
                case 3: goto L_0x006d;
                case 4: goto L_0x0056;
                case 5: goto L_0x0056;
                case 6: goto L_0x004b;
                case 7: goto L_0x0044;
                case 8: goto L_0x0030;
                case 9: goto L_0x0029;
                case 10: goto L_0x0022;
                case 11: goto L_0x0015;
                case 12: goto L_0x0008;
                default: goto L_0x0006;
            }
        L_0x0006:
            goto L_0x0079
        L_0x0008:
            com.miui.powercenter.utils.n r5 = com.miui.powercenter.utils.n.a((android.content.Context) r5)
            boolean r5 = r5.b()
            if (r5 == 0) goto L_0x0078
        L_0x0012:
            r0 = r1
            goto L_0x0079
        L_0x0015:
            com.miui.powercenter.utils.n r5 = com.miui.powercenter.utils.n.a((android.content.Context) r5)
            int r5 = r5.g()
            r6 = 15
            if (r5 > r6) goto L_0x0012
            goto L_0x0078
        L_0x0022:
            boolean r5 = com.miui.powercenter.utils.o.o(r5)
            if (r5 == 0) goto L_0x0078
            goto L_0x0012
        L_0x0029:
            boolean r5 = com.miui.powercenter.utils.g.c(r5)
            if (r5 == 0) goto L_0x0078
            goto L_0x0012
        L_0x0030:
            int r6 = android.os.Build.VERSION.SDK_INT
            r3 = 28
            if (r6 <= r3) goto L_0x003d
            int r5 = com.miui.powercenter.utils.o.g(r5)
            if (r5 != 0) goto L_0x0012
            goto L_0x0078
        L_0x003d:
            int r5 = com.miui.powercenter.utils.o.g(r5)
            if (r5 != r0) goto L_0x0078
            goto L_0x0012
        L_0x0044:
            boolean r5 = com.miui.powercenter.utils.o.j(r5)
            if (r5 == 0) goto L_0x0078
            goto L_0x0012
        L_0x004b:
            com.miui.powercenter.utils.n r5 = com.miui.powercenter.utils.n.a((android.content.Context) r5)
            boolean r5 = r5.m()
            if (r5 == 0) goto L_0x0012
            goto L_0x0078
        L_0x0056:
            java.util.List r5 = com.miui.powercenter.quickoptimize.C0522a.b(r5)
            java.util.List<java.lang.String> r6 = e
            r6.clear()
            java.util.List<java.lang.String> r6 = e
            r6.addAll(r5)
            java.util.List<java.lang.String> r5 = e
            boolean r5 = r5.isEmpty()
            if (r5 == 0) goto L_0x0012
            goto L_0x0078
        L_0x006d:
            com.miui.powercenter.utils.n r5 = com.miui.powercenter.utils.n.a((android.content.Context) r5)
            int r5 = r5.c()
            if (r5 >= 0) goto L_0x0078
            goto L_0x0012
        L_0x0078:
            r0 = r2
        L_0x0079:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.powercenter.quickoptimize.v.d(android.content.Context, int):int");
    }

    public static int f() {
        return 11;
    }

    /* access modifiers changed from: private */
    public void l() {
        synchronized (this) {
            f7265a.clear();
            f7266b.clear();
            f7267c.clear();
            f7268d.clear();
            e.clear();
            this.h = 0;
        }
    }

    private boolean m() {
        long a2 = C0533l.a();
        return a2 == 0 || SystemClock.elapsedRealtime() - a2 > 300000;
    }

    public int a(Context context, m mVar) {
        if (f7266b.indexOf(mVar) == -1 || !b().b(context, mVar.f7231a)) {
            return 0;
        }
        int a2 = o.a(mVar);
        f7268d.add(mVar);
        f7266b.remove(mVar);
        this.h += o.a(context, mVar);
        return a2;
    }

    public List<m> a() {
        return f7267c;
    }

    public void a(Context context, a aVar) {
        this.i = SystemClock.elapsedRealtime();
        d.a(new t(this, context.getApplicationContext(), aVar));
    }

    public int c() {
        return a(f7266b);
    }

    public List<m> d() {
        return f7266b;
    }

    public long e() {
        return this.h;
    }

    public int g() {
        return a(f7268d);
    }

    public boolean h() {
        return this.p;
    }

    public boolean i() {
        return this.r;
    }

    public boolean j() {
        return this.g;
    }

    public boolean k() {
        return this.q;
    }
}
