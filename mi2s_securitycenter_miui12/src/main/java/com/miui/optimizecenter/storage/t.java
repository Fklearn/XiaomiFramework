package com.miui.optimizecenter.storage;

import android.content.Context;
import android.util.Log;
import com.miui.optimizecenter.storage.model.b;
import com.miui.optimizecenter.storage.o;
import com.miui.securitycenter.n;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class t implements Runnable, o.a {

    /* renamed from: a  reason: collision with root package name */
    private Context f5775a;

    /* renamed from: b  reason: collision with root package name */
    private s f5776b;

    /* renamed from: c  reason: collision with root package name */
    private AppSystemDataManager f5777c;

    /* renamed from: d  reason: collision with root package name */
    private o f5778d;
    private m e;
    private long f;
    private List<b> g = new ArrayList();
    private List<String> h = new ArrayList();
    private List<String> i = new ArrayList();
    HashMap<String, List<String>> j = new HashMap<>();

    public t(Context context) {
        this.f5775a = context;
        this.f5776b = s.a(context);
        this.f5777c = AppSystemDataManager.a(context);
        this.f5778d = o.a();
        this.e = m.a(context);
    }

    private boolean e() {
        long currentTimeMillis = System.currentTimeMillis() - this.f5775a.getSharedPreferences("storagePathDB", 0).getLong("lastUpdateTime", 0);
        Log.i("StorageScanTask", "timeInterval=" + (currentTimeMillis / 86400000));
        return currentTimeMillis >= 604800000;
    }

    private void f() {
        this.f5776b.e();
        s sVar = this.f5776b;
        u uVar = u.OTHER;
        sVar.a(uVar, uVar.a().f5755c);
    }

    public void a() {
    }

    public void a(long j2, long j3) {
        f();
    }

    public /* synthetic */ void a(n nVar) {
        nVar.a(this.h);
    }

    public void a(String str, long j2, long j3) {
        Iterator<b> it = this.g.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            b next = it.next();
            if (next.f5762d.equals(str)) {
                next.o += j2;
                next.k += j2;
                break;
            }
        }
        this.f += j2;
        this.f5776b.a(u.APP_DATA, u.APP_DATA.a().f5755c + j2);
        Log.i("StorageScanTask", "onPackageScanFinished: sdcard size = " + j2);
    }

    public void b() {
        this.f5776b.d();
    }

    public List<String> c() {
        return this.i;
    }

    public void d() {
        this.g.addAll(this.f5777c.a());
        for (b bVar : this.g) {
            this.h.add(bVar.f5762d);
        }
        long c2 = 0 + this.f5777c.c();
        this.f += c2;
        this.f5776b.a(u.APP_DATA, c2);
        this.f5776b.a(this.g);
        long e2 = this.f5777c.e() - this.f5777c.d();
        this.f += e2;
        this.f5776b.a(u.SYSTEM, e2);
        if (e()) {
            try {
                n.a().a(new d(this, n.a()));
            } catch (Exception e3) {
                Log.e("StorageScanTask", "startScan: update db failed", e3);
            }
        }
        this.j = this.f5778d.a(this.f5775a, this.h);
        for (List<String> addAll : this.j.values()) {
            this.i.addAll(addAll);
        }
        long d2 = this.e.d(this.i);
        this.f += d2;
        this.f5776b.a(u.PICTURE, d2);
        long b2 = this.e.b(this.i);
        this.f += b2;
        this.f5776b.a(u.AUDIO, b2);
        long e4 = this.e.e(this.i);
        this.f += e4;
        this.f5776b.a(u.VIDEO, e4);
        long a2 = this.e.a(this.i);
        this.f += a2;
        this.f5776b.a(u.APK, a2);
        long c3 = this.e.c(this.i);
        this.f += c3;
        this.f5776b.a(u.DOC, c3);
        if (this.j.size() == 0) {
            f();
            this.f5776b.d();
            return;
        }
        this.f5778d.a(this.j, (o.a) this);
    }

    public void run() {
        d();
    }
}
