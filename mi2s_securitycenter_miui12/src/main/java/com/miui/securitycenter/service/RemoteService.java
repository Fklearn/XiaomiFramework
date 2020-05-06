package com.miui.securitycenter.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;
import b.b.c.j.v;
import b.b.n.j;
import b.b.o.g.e;
import b.b.p.g;
import com.miui.applicationlock.c.C0267k;
import com.miui.permcenter.install.PackageVerificationRecevier;
import com.miui.securitycenter.R;
import com.miui.securitycenter.dynamic.DynamicServiceManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class RemoteService extends Service {

    /* renamed from: a  reason: collision with root package name */
    private C0267k f7523a;

    /* renamed from: b  reason: collision with root package name */
    private Binder f7524b;

    private void a() {
        try {
            Object a2 = e.a(Class.forName("android.app.ActivityManagerNative"), "getDefault", (Class<?>[]) null, new Object[0]);
            if (Build.VERSION.SDK_INT >= 26) {
                e.a(a2, "setProcessImportant", (Class<?>[]) new Class[]{IBinder.class, Integer.TYPE, Boolean.TYPE, String.class}, this.f7524b, Integer.valueOf(Process.myPid()), true, "securitycenter");
                return;
            }
            e.a(a2, "setProcessForeground", (Class<?>[]) new Class[]{IBinder.class, Integer.TYPE, Boolean.TYPE}, this.f7524b, Integer.valueOf(Process.myPid()), true);
        } catch (Exception e) {
            Log.e("RemoteService", "setProcessForeground", e);
        }
    }

    /* access modifiers changed from: protected */
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("RemoteService dump");
        DynamicServiceManager.getInstance(getApplicationContext()).dump(fileDescriptor, printWriter, strArr);
        PackageVerificationRecevier.a(printWriter);
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        Notification build = v.a((Context) this, "securitycenter_resident_notification").build();
        v.a((NotificationManager) getSystemService("notification"), "securitycenter_resident_notification", getResources().getString(R.string.notify_channel_optimize), 5);
        startForeground(20006, build);
        Log.d("RemoteService", "RemoteService startForeground");
        a();
        this.f7523a = new C0267k(this);
        j.a(this);
    }

    public void onDestroy() {
        g.a();
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        if (intent != null) {
            String stringExtra = intent.getStringExtra("cmd");
            if ("recommend_app_installed".equals(stringExtra)) {
                C0267k.e(this);
            }
            if ("app_lock".equals(stringExtra)) {
                this.f7523a.a(intent, (Context) this);
            }
            if ("competitive_app_installed".equals(stringExtra)) {
                C0267k.d(this);
            }
            if ("app_installed_scan".equals(stringExtra)) {
                C0267k.c(this);
            }
            if ("boot_recommend_app_scan".equals(stringExtra)) {
                C0267k.a((Context) this, intent);
            }
        }
        return super.onStartCommand(intent, i, i2);
    }
}
