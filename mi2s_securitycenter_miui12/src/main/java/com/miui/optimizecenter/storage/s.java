package com.miui.optimizecenter.storage;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import b.b.c.j.B;
import com.miui.luckymoney.config.AppConstants;
import com.miui.optimizecenter.storage.model.StorageItemInfo;
import com.miui.optimizecenter.storage.model.b;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.n;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class s {

    /* renamed from: a  reason: collision with root package name */
    private static s f5770a;

    /* renamed from: b  reason: collision with root package name */
    public static final u[] f5771b = {u.OTHER, u.APP_DATA, u.PICTURE, u.AUDIO, u.VIDEO, u.APK, u.DOC, u.SYSTEM};
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public Context f5772c;

    /* renamed from: d  reason: collision with root package name */
    private Set<u> f5773d = new HashSet();
    private List<b> e;
    private List<b> f;
    private boolean g;
    /* access modifiers changed from: private */
    public a h;
    /* access modifiers changed from: private */
    public t i;
    /* access modifiers changed from: private */
    public WeakReference<l> j;

    private class a extends Handler {
        private a() {
        }

        /* synthetic */ a(s sVar, r rVar) {
            this();
        }

        public void handleMessage(Message message) {
            l lVar = (l) s.this.j.get();
            if (lVar != null) {
                int i = message.what;
                if (i == 0) {
                    lVar.a((u) message.obj);
                } else if (i == 1) {
                    lVar.j();
                } else if (i == 2) {
                    lVar.i();
                } else if (i == 4) {
                    lVar.g();
                }
            }
        }
    }

    private s(Context context) {
        this.f5772c = context;
        this.h = new a(this, (r) null);
    }

    public static s a(Context context) {
        if (f5770a == null) {
            f5770a = new s(context);
        }
        return f5770a;
    }

    public List<b> a() {
        if (this.f == null) {
            this.f = new ArrayList();
        }
        this.f.clear();
        int j2 = B.j();
        for (b next : this.e) {
            if (B.c(next.f5760b) == j2 && ((!next.i || !AppConstants.Package.PACKAGE_NAME_MM.equals(next.f5762d)) && !"com.miui.cleanmaster".equals(next.f5762d))) {
                this.f.add(next);
            }
        }
        return this.f;
    }

    public void a(long j2, long j3) {
        StorageItemInfo a2 = u.APP_DATA.a();
        if (j2 > 0) {
            a2.f5755c -= j2;
        }
        if (j3 > 0) {
            a2.f5755c -= j3;
        }
        e();
        Message.obtain(this.h, 2).sendToTarget();
    }

    public void a(l lVar) {
        this.j = new WeakReference<>(lVar);
    }

    public void a(u uVar, long j2) {
        uVar.a().f5755c = j2;
        this.f5773d.add(uVar);
        Message.obtain(this.h, 0, uVar).sendToTarget();
    }

    public void a(String str) {
        long j2;
        Iterator<b> it = this.e.iterator();
        while (true) {
            if (!it.hasNext()) {
                j2 = 0;
                break;
            }
            b next = it.next();
            if (next.f5762d.equals(str)) {
                j2 = next.k;
                this.e.remove(next);
                this.f.remove(next);
                break;
            }
        }
        u.APP_DATA.a().f5755c -= j2;
        e();
        Message.obtain(this.h, 2).sendToTarget();
    }

    public void a(List<b> list) {
        this.e = list;
    }

    public void a(boolean z) {
        this.g = z;
    }

    public List<u> b() {
        return new ArrayList(this.f5773d);
    }

    public boolean c() {
        return this.g;
    }

    public void d() {
        Message.obtain(this.h, 1).sendToTarget();
    }

    public void e() {
        long j2 = 0;
        for (u uVar : f5771b) {
            if (uVar != u.OTHER) {
                j2 += uVar.a().a();
            }
        }
        AppSystemDataManager a2 = AppSystemDataManager.a(this.f5772c);
        long e2 = (a2.e() - a2.b()) - j2;
        if (e2 < 0) {
            e2 = 0;
        }
        u.OTHER.a().f5755c = e2;
    }

    public void f() {
        n.a().b(new r(this));
    }

    public void g() {
        Collections.sort(this.f, new b.a());
    }

    public void h() {
        this.i = new t(Application.d());
        n.a().a(this.i);
    }
}
