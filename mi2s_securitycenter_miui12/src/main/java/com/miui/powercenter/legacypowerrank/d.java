package com.miui.powercenter.legacypowerrank;

import android.content.DialogInterface;
import android.content.pm.IPackageDeleteObserver;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import b.b.c.j.x;
import b.b.o.b.a.a;
import b.b.o.g.e;
import com.miui.powercenter.legacypowerrank.PowerDetailActivity;
import com.miui.powercenter.utils.j;

class d implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ PowerDetailActivity.a f7088a;

    d(PowerDetailActivity.a aVar) {
        this.f7088a = aVar;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        try {
            int a2 = j.a(this.f7088a.f7078b);
            Object a3 = e.a(Class.forName("android.content.pm.IPackageManager$Stub"), Object.class, "asInterface", (Class<?>[]) new Class[]{IBinder.class}, (IBinder) e.a(Class.forName("android.os.ServiceManager"), Binder.class, "getService", (Class<?>[]) new Class[]{String.class}, "package"));
            int e = x.e(this.f7088a.getContext(), this.f7088a.f7079c[0]);
            if (a2 == 0 && a.a((Object) a3, this.f7088a.f7079c[0])) {
                a.a(a3, this.f7088a.f7079c[0], e, (IPackageDeleteObserver) null, 999, 0);
            }
            if (b.b.f.a.a(this.f7088a.f7079c[0])) {
                this.f7088a.a((Object) a3, e, a2, 4);
            } else {
                this.f7088a.a((Object) a3, e, a2, 0);
            }
        } catch (Exception e2) {
            Log.e("PowerDetailActivity", "uninstallApp: ", e2);
        }
    }
}
