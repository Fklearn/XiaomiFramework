package com.miui.applicationlock;

import android.content.Context;
import android.os.SystemClock;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import com.miui.applicationlock.ConfirmAccessControl;
import com.miui.applicationlock.a.h;
import com.miui.applicationlock.c.o;
import com.miui.applicationlock.c.p;

/* renamed from: com.miui.applicationlock.ia  reason: case insensitive filesystem */
class C0280ia implements p {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ConfirmAccessControl f3354a;

    C0280ia(ConfirmAccessControl confirmAccessControl) {
        this.f3354a = confirmAccessControl;
    }

    /* JADX WARNING: type inference failed for: r0v10, types: [android.content.Context, com.miui.applicationlock.ConfirmAccessControl] */
    public void a() {
        o.c(this.f3354a.getApplicationContext(), ConfirmAccessControl.m(this.f3354a));
        int a2 = o.a(this.f3354a.v);
        Log.d("ConfirmAccessControl", "wrong attempts: " + this.f3354a.v + ", retryTimeout: " + a2);
        if (a2 > 0) {
            long elapsedRealtime = SystemClock.elapsedRealtime() + ((long) a2);
            o.a(elapsedRealtime, (Context) this.f3354a);
            this.f3354a.a(elapsedRealtime);
            h.a(this.f3354a.C.b() != null ? "binding" : "no_binding");
            return;
        }
        o.b((View) this.f3354a.f3137c);
        this.f3354a.a(ConfirmAccessControl.d.NeedToUnlockWrong);
        this.f3354a.f3136b.a();
    }

    public void a(Editable editable) {
    }

    public void a(String str) {
    }

    public void b() {
        this.f3354a.N();
        this.f3354a.a(false);
        o.a(this.f3354a.P, this.f3354a.O);
    }
}
