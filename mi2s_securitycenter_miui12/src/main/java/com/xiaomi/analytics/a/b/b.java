package com.xiaomi.analytics.a.b;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.util.Log;
import com.xiaomi.analytics.a.a.a;
import com.xiaomi.analytics.a.m;
import dalvik.system.DexClassLoader;

public class b implements a {

    /* renamed from: a  reason: collision with root package name */
    private Context f8299a;

    /* renamed from: b  reason: collision with root package name */
    private ClassLoader f8300b;

    /* renamed from: c  reason: collision with root package name */
    private int f8301c;

    /* renamed from: d  reason: collision with root package name */
    private String f8302d = "";
    private String e;
    private String f;
    private volatile boolean g;

    public b(Context context, String str, String str2) {
        this.f8299a = com.xiaomi.analytics.a.a.b.a(context);
        this.e = str;
        this.f = str2;
        PackageInfo packageArchiveInfo = context.getPackageManager().getPackageArchiveInfo(str, 1);
        this.f8301c = packageArchiveInfo.versionCode;
        this.f8302d = packageArchiveInfo.versionName;
    }

    private void a() {
        try {
            this.f8300b.loadClass("com.miui.analytics.Analytics").getDeclaredMethod("initialize", new Class[]{Context.class, Integer.TYPE, String.class}).invoke((Object) null, new Object[]{this.f8299a, Integer.valueOf(this.f8301c), this.f8302d});
        } catch (Throwable th) {
            Log.w(a.a("DexAnalytics"), "initAnalytics exception", th);
        }
    }

    public boolean a(String str) {
        try {
            init();
            return ((Boolean) this.f8300b.loadClass("com.miui.analytics.Analytics").getDeclaredMethod("isPolicyReady", new Class[]{String.class, String.class}).invoke((Object) null, new Object[]{this.f8299a.getPackageName(), str})).booleanValue();
        } catch (Throwable th) {
            Log.w(a.a("DexAnalytics"), "isPolicyReady exception", th);
            return false;
        }
    }

    public String b(String str) {
        try {
            init();
            return (String) this.f8300b.loadClass("com.miui.analytics.Analytics").getDeclaredMethod("getClientExtra", new Class[]{String.class, String.class}).invoke((Object) null, new Object[]{this.f8299a.getPackageName(), str});
        } catch (Throwable th) {
            Log.w(a.a("DexAnalytics"), "getClientExtra exception", th);
            return "";
        }
    }

    public m getVersion() {
        return new m(this.f8302d);
    }

    public void init() {
        try {
            if (!this.g) {
                this.f8300b = new DexClassLoader(this.e, this.f8299a.getDir("dex", 0).getAbsolutePath(), this.f, ClassLoader.getSystemClassLoader());
                a();
                this.g = true;
                a.a("DexAnalytics", "initialized");
            }
        } catch (Exception e2) {
            Log.e(a.a("DexAnalytics"), "init e", e2);
        }
    }

    public void setDebugOn(boolean z) {
        try {
            init();
            this.f8300b.loadClass("com.miui.analytics.Analytics").getDeclaredMethod("setDebugOn", new Class[]{Boolean.TYPE}).invoke((Object) null, new Object[]{Boolean.valueOf(z)});
        } catch (Throwable th) {
            Log.w(a.a("DexAnalytics"), "setDebugOn exception", th);
        }
    }

    public void setDefaultPolicy(String str, String str2) {
        try {
            init();
            this.f8300b.loadClass("com.miui.analytics.Analytics").getDeclaredMethod("setDefaultPolicy", new Class[]{String.class, String.class}).invoke((Object) null, new Object[]{str, str2});
        } catch (Throwable th) {
            Log.w(a.a("DexAnalytics"), "setDefaultPolicy exception", th);
        }
    }

    public void trackEvent(String str) {
        try {
            init();
            this.f8300b.loadClass("com.miui.analytics.Analytics").getDeclaredMethod("trackEvent", new Class[]{String.class}).invoke((Object) null, new Object[]{str});
        } catch (Throwable th) {
            Log.w(a.a("DexAnalytics"), "trackEvent exception", th);
        }
    }

    public void trackEvents(String[] strArr) {
        try {
            init();
            this.f8300b.loadClass("com.miui.analytics.Analytics").getDeclaredMethod("trackEvents", new Class[]{String[].class}).invoke((Object) null, new Object[]{strArr});
        } catch (Throwable th) {
            Log.w(a.a("DexAnalytics"), "trackEvents exception", th);
        }
    }
}
