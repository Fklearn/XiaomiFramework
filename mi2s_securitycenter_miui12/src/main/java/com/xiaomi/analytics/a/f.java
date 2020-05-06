package com.xiaomi.analytics.a;

import android.os.Process;
import com.xiaomi.analytics.a.a.a;
import com.xiaomi.analytics.a.a.b;
import com.xiaomi.analytics.a.l;

class f implements l.a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ i f8315a;

    f(i iVar) {
        this.f8315a = iVar;
    }

    public void a(String str, boolean z) {
        if (this.f8315a.f == null) {
            a.a("SdkManager", "download finished, use new analytics.");
            com.xiaomi.analytics.a.b.a d2 = this.f8315a.t();
            if (d2 != null) {
                d2.init();
            }
            com.xiaomi.analytics.a.b.a unused = this.f8315a.f = d2;
            i iVar = this.f8315a;
            iVar.a(iVar.f);
        } else if (z && !b.b(this.f8315a.e)) {
            Process.killProcess(Process.myPid());
        }
    }
}
