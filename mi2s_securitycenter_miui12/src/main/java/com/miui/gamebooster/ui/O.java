package com.miui.gamebooster.ui;

import android.app.Activity;
import android.os.RemoteException;
import android.util.Log;
import com.miui.common.persistence.b;
import com.miui.gamebooster.m.ga;
import com.miui.gamebooster.service.IGameBooster;
import com.miui.gamebooster.service.M;
import com.miui.gamebooster.ui.N;

class O implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ N f4950a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ int f4951b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ String f4952c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ N.e f4953d;

    O(N.e eVar, N n, int i, String str) {
        this.f4953d = eVar;
        this.f4950a = n;
        this.f4951b = i;
        this.f4952c = str;
    }

    public void run() {
        N n;
        IGameBooster n2;
        try {
            if (!this.f4950a.A) {
                if (this.f4951b == 100) {
                    String unused = this.f4950a.C = this.f4950a.o.getSetting("detailUrl", (String) null);
                    boolean unused2 = this.f4950a.A = true;
                }
            }
            if (this.f4950a.A) {
                int i = C.f4862a[this.f4950a.H.ordinal()];
                if (i == 1) {
                    this.f4950a.o.connectVpn(this.f4950a.z.packageName);
                    M unused3 = this.f4950a.H = M.INIT;
                    M unused4 = this.f4950a.I = M.CONNECTVPN;
                    n = this.f4950a;
                } else if (i == 2) {
                    this.f4950a.o.refreshUserState();
                    M unused5 = this.f4950a.H = M.GETREFRESHTIME;
                    return;
                } else if (i != 3) {
                    if (i == 4) {
                        String unused6 = this.f4950a.C = this.f4950a.o.getSetting("detailUrl", (String) null);
                        M unused7 = this.f4950a.H = M.INIT;
                        this.f4950a.q.a(111, new Object());
                        n = this.f4950a;
                    } else {
                        return;
                    }
                } else if (this.f4951b == 1003) {
                    Long a2 = ga.a(this.f4952c, "yyyy-MM-dd HH:mm:ss");
                    String e = N.f4939a;
                    Log.i(e, "时间戳：" + ga.a(this.f4952c, "yyyy-MM-dd HH:mm:ss"));
                    if (a2 != null) {
                        b.b("gamebooster_xunyou_cache_time", a2.longValue());
                        Activity activity = this.f4950a.getActivity();
                        if (!(activity == null || (n2 = ((GameBoosterRealMainActivity) activity).n()) == null)) {
                            n2.K();
                        }
                    }
                    boolean unused8 = this.f4950a.A = false;
                    M unused9 = this.f4950a.H = M.INIT;
                    this.f4950a.q.a(112, new Object());
                    return;
                } else {
                    return;
                }
                boolean unused10 = n.A = false;
            }
        } catch (RemoteException e2) {
            Log.i(N.f4939a, e2.toString());
        }
    }
}
