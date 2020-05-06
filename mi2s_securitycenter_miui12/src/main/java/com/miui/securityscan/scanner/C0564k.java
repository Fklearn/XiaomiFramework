package com.miui.securityscan.scanner;

import android.app.ActivityManager;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import b.b.o.g.c;
import b.b.o.g.d;
import com.miui.networkassistant.config.Constants;
import com.miui.securityscan.d.c;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* renamed from: com.miui.securityscan.scanner.k  reason: case insensitive filesystem */
class C0564k {

    /* renamed from: a  reason: collision with root package name */
    private static C0564k f7903a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public Context f7904b;

    /* renamed from: c  reason: collision with root package name */
    private PackageManager f7905c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public ActivityManager f7906d;
    private c e;

    /* renamed from: com.miui.securityscan.scanner.k$a */
    public static class a implements com.miui.securitycenter.memory.a {
        public void d() {
        }

        public void f() {
        }
    }

    /* renamed from: com.miui.securityscan.scanner.k$b */
    public static class b implements com.miui.securitycenter.memory.b {
    }

    private C0564k(Context context) {
        this.f7904b = context;
        this.f7905c = context.getPackageManager();
        this.f7906d = (ActivityManager) context.getSystemService("activity");
        this.e = c.a(context);
    }

    private int a(Context context, String str) {
        c.a a2 = c.a.a("android.miui.AppOpsUtils");
        a2.b("getApplicationAutoStart", new Class[]{Context.class, String.class}, context, str);
        return a2.c();
    }

    /* access modifiers changed from: private */
    public ResolveInfo a(ActivityManager.RecentTaskInfo recentTaskInfo) {
        Intent intent = new Intent(recentTaskInfo.baseIntent);
        ComponentName componentName = recentTaskInfo.origActivity;
        if (componentName != null) {
            intent.setComponent(componentName);
        }
        intent.setFlags((intent.getFlags() & -2097153) | 268435456);
        return this.f7905c.resolveActivity(intent, 0);
    }

    /* access modifiers changed from: private */
    public SparseBooleanArray a(String str, int i) {
        SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();
        boolean z = true;
        sparseBooleanArray.put(1, new HashSet(a(this.f7904b, i)).contains(str));
        sparseBooleanArray.put(0, c(this.f7904b).contains(str));
        sparseBooleanArray.put(2, this.e.b().contains(str));
        if (a(this.f7904b.getApplicationContext(), str) != 0) {
            z = false;
        }
        sparseBooleanArray.put(3, z);
        return sparseBooleanArray;
    }

    private static List<String> a(int i) {
        try {
            List<String> list = (List) d.a("MemoryCheckManager", Class.forName("miui.process.ProcessManager"), List.class, "getLockedApplication", (Class<?>[]) new Class[]{Integer.TYPE}, Integer.valueOf(i));
            StringBuilder sb = new StringBuilder();
            sb.append("getLockedApplication = ");
            sb.append(list != null ? list.toString() : "list is null");
            Log.d("MemoryCheckManager", sb.toString());
            return list;
        } catch (Exception e2) {
            Log.e("MemoryCheckManager", "getLockedApplication", e2);
            return null;
        }
    }

    private static Set<String> a(Context context, int i) {
        List<String> a2 = a(i);
        return (a2 == null || a2.isEmpty()) ? new HashSet() : new HashSet(a2);
    }

    public static synchronized C0564k b(Context context) {
        C0564k kVar;
        synchronized (C0564k.class) {
            if (f7903a == null) {
                f7903a = new C0564k(context.getApplicationContext());
            }
            kVar = f7903a;
        }
        return kVar;
    }

    /* access modifiers changed from: private */
    public static List<String> c(Context context) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(Constants.System.ANDROID_PACKAGE_NAME);
        arrayList.add("com.android.providers.media");
        arrayList.add("com.android.deskclock");
        arrayList.add("com.google.android.marvin.talkback");
        arrayList.add("com.miui.cleanmaster");
        ActivityInfo resolveActivityInfo = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.HOME").resolveActivityInfo(context.getPackageManager(), 0);
        if (resolveActivityInfo != null) {
            arrayList.add(resolveActivityInfo.packageName);
        }
        String string = Settings.Secure.getString(context.getContentResolver(), "default_input_method");
        if (!TextUtils.isEmpty(string) && string.contains("/")) {
            arrayList.add(string.substring(0, string.indexOf(47)));
        }
        WallpaperInfo wallpaperInfo = WallpaperManager.getInstance(context).getWallpaperInfo();
        if (wallpaperInfo != null) {
            arrayList.add(wallpaperInfo.getPackageName());
        }
        return arrayList;
    }

    public void a(com.miui.securitycenter.memory.b bVar) {
        Log.d("MemoryCheckManager", "startScan");
        b.b.c.j.d.a(new C0562i(this, bVar));
    }

    public void a(List<com.miui.securitycenter.memory.d> list, com.miui.securitycenter.memory.a aVar) {
        Log.d("MemoryCheckManager", "startCleanup");
        b.b.c.j.d.a(new C0563j(this, aVar, list));
    }
}
