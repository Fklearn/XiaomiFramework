package com.miui.antivirus.service;

import android.content.ComponentName;
import android.content.Context;
import android.os.UserHandle;
import android.util.Log;
import b.b.a.e.q;
import b.b.b.a.b;
import b.b.b.d.n;
import b.b.b.p;
import java.util.ArrayList;
import miui.os.Build;
import miui.process.IActivityChangeListener;

class c extends IActivityChangeListener.Stub {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ GuardService f2891a;

    c(GuardService guardService) {
        this.f2891a = guardService;
    }

    public void onActivityChanged(ComponentName componentName, ComponentName componentName2) {
        if (UserHandle.myUserId() == q.a()) {
            if (!Build.IS_INTERNATIONAL_BUILD || (Build.checkRegion("IN") && n.b())) {
                Log.i("GuardService", "notifyChange! preName = " + componentName.toString() + "; curName = " + componentName2.toString());
                if (this.f2891a.p == null) {
                    GuardService guardService = this.f2891a;
                    ArrayList unused = guardService.p = p.b((Context) guardService);
                }
                if (this.f2891a.q == null) {
                    GuardService guardService2 = this.f2891a;
                    ArrayList unused2 = guardService2.q = p.a((Context) guardService2);
                }
                if (this.f2891a.r == null) {
                    GuardService guardService3 = this.f2891a;
                    ArrayList unused3 = guardService3.r = n.g(guardService3);
                }
                boolean z = true;
                boolean z2 = (this.f2891a.p.contains(componentName2.getPackageName()) && !this.f2891a.r.contains(componentName2.getPackageName())) || this.f2891a.q.contains(componentName2.getClassName());
                if ((!this.f2891a.p.contains(componentName.getPackageName()) || this.f2891a.r.contains(componentName.getPackageName())) && !this.f2891a.q.contains(componentName.getClassName())) {
                    z = false;
                }
                if (z != z2) {
                    this.f2891a.a(z2, componentName2.getPackageName());
                    if (z2 && this.f2891a.q.contains(componentName2.getClassName())) {
                        b.C0023b.a(componentName.getPackageName(), n.a(this.f2891a, componentName2.getClassName()));
                    }
                }
            }
        }
    }
}
