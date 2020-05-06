package com.miui.googlebase;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import b.b.o.g.d;
import miui.security.ISecurityCallback;

public class GoogleBaseClientProxy extends ISecurityCallback.Stub {

    /* renamed from: a  reason: collision with root package name */
    private static GoogleBaseClientProxy f5432a;

    /* renamed from: b  reason: collision with root package name */
    private Context f5433b;

    /* renamed from: c  reason: collision with root package name */
    private b f5434c = null;

    private GoogleBaseClientProxy(Context context) {
        Log.d("GoogleBaseClientProxy", "new GoogleBaseProxy created!");
        this.f5433b = context;
        a();
    }

    public static GoogleBaseClientProxy a(Context context) {
        if (f5432a == null) {
            f5432a = new GoogleBaseClientProxy(context);
        }
        return f5432a;
    }

    private void a() {
        try {
            IBinder iBinder = (IBinder) d.a("GoogleBaseClientProxy", Class.forName("android.os.ServiceManager"), "getService", (Class<?>[]) new Class[]{String.class}, "security");
            Object a2 = d.a("GoogleBaseClientProxy", Class.forName("miui.security.ISecurityManager$Stub"), "asInterface", (Class<?>[]) new Class[]{IBinder.class}, iBinder);
            if (a2 == null) {
                Log.d("GoogleBaseClientProxy", "ism is null");
                return;
            }
            d.a("GoogleBaseClientProxy", a2, "offerGoogleBaseCallBack", (Class<?>[]) new Class[]{ISecurityCallback.class}, this);
        } catch (Exception unused) {
            Log.e("GoogleBaseClientProxy", "ISecurityManager work abnormal");
        }
    }

    public boolean checkPreInstallNeeded(String str) {
        return false;
    }

    public void preInstallApps() {
        if (this.f5434c.a()) {
            Log.d("GoogleBaseClientProxy", "pre-install app: blacklistfalse");
            return;
        }
        Log.d("GoogleBaseClientProxy", "start preinstall apps");
        Intent intent = new Intent();
        intent.setClass(this.f5433b, GoogleBaseAppInstallService.class);
        intent.setAction("install");
        b bVar = this.f5434c;
        String str = null;
        String c2 = bVar == null ? null : bVar.c();
        b bVar2 = this.f5434c;
        if (bVar2 != null) {
            str = bVar2.d();
        }
        intent.putExtra("appName", c2);
        intent.putExtra("packageName", str);
        this.f5433b.startService(intent);
    }
}
