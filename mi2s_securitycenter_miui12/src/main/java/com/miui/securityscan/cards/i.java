package com.miui.securityscan.cards;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import b.b.a.e.c;
import b.b.c.j.u;
import com.miui.appmanager.AppManageUtils;
import com.miui.common.persistence.b;
import com.miui.powercenter.batteryhistory.C0501e;
import com.miui.powercenter.batteryhistory.C0514s;
import com.miui.powercenter.batteryhistory.C0520y;
import com.miui.powercenter.batteryhistory.aa;
import com.miui.powercenter.utils.o;
import com.miui.powercenter.utils.s;
import com.miui.securitycenter.R;
import com.miui.securitycenter.h;
import com.miui.securitycenter.p;
import com.miui.securityscan.cards.n;
import com.xiaomi.stat.MiStat;
import java.lang.ref.WeakReference;
import java.util.List;

public class i extends AsyncTask<Void, Void, Void> {

    /* renamed from: a  reason: collision with root package name */
    private WeakReference<n> f7655a;

    /* renamed from: b  reason: collision with root package name */
    private WeakReference<Context> f7656b;

    public i(Context context, n nVar) {
        this.f7655a = new WeakReference<>(nVar);
        this.f7656b = new WeakReference<>(context.getApplicationContext());
    }

    private static boolean a(Context context) {
        if (p.a() < 11) {
            return true;
        }
        com.miui.appmanager.i iVar = new com.miui.appmanager.i(context);
        boolean z = b.a("app_manager_click_time", AppManageUtils.a(86400000)).compareTo(AppManageUtils.a(86400000)) <= 0;
        return !iVar.a() || Boolean.valueOf(b.a("app_manager_click", false) && !z).booleanValue() || !z;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Void doInBackground(Void... voidArr) {
        Object[] objArr;
        int i;
        if (isCancelled()) {
            return null;
        }
        n nVar = (n) this.f7655a.get();
        Context context = (Context) this.f7656b.get();
        if (!(nVar == null || context == null)) {
            Intent registerReceiver = context.registerReceiver((BroadcastReceiver) null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
            boolean z = false;
            if (registerReceiver != null) {
                int intExtra = registerReceiver.getIntExtra(MiStat.Param.LEVEL, 0);
                int intExtra2 = registerReceiver.getIntExtra("scale", 0);
                if (intExtra2 != 0) {
                    nVar.j = (intExtra * 100) / intExtra2;
                    nVar.k = nVar.j > 10;
                    nVar.m = o.k(context);
                    List<aa> b2 = C0514s.c().b();
                    if (nVar.m) {
                        long j = C0501e.a(context, b2).f6879a;
                        i = R.string.menu_summary_power_manager_4;
                        objArr = new Object[]{s.d(context, j)};
                    } else {
                        long a2 = C0520y.a(context, b2);
                        i = R.string.menu_summary_power_manager_2;
                        objArr = new Object[]{s.d(context, a2)};
                    }
                    nVar.l = context.getString(i, objArr);
                }
            }
            nVar.h = !h.h(context);
            nVar.i = h.b(context);
            u.a c2 = u.c(context);
            if (c2 != null) {
                long j2 = c2.f1763b;
                if (j2 >= 0 && c2.f1762a > 0) {
                    if (j2 < c2.f1764c) {
                        z = true;
                    }
                    nVar.q = z;
                    nVar.r = true;
                    nVar.s = c2.f1762a - c2.f1763b;
                }
                nVar.t = c2.f1765d;
            }
            nVar.o = a(context);
            nVar.p = !c.c(context);
        }
        return null;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public void onPostExecute(Void voidR) {
        n nVar = (n) this.f7655a.get();
        if (nVar != null) {
            for (n.c next : nVar.v) {
                next.onGarbageChange(nVar.h, nVar.i);
                n.c cVar = next;
                cVar.onNetworkAssistChange(nVar.q, nVar.r, nVar.s, nVar.t);
                cVar.onPowerCenterChange(nVar.m, nVar.j, nVar.k, 1, nVar.l);
                next.onSecurityScanChange(nVar.n);
                next.onAppManagerChange(nVar.o);
                next.onAntiSpamChange(nVar.p);
            }
            nVar.w = true;
        }
    }
}
