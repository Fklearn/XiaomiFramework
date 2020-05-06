package com.miui.gamebooster.f;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.j.x;
import com.miui.common.persistence.b;
import com.miui.gamebooster.m.C0393y;
import com.miui.securitycenter.Application;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

public class d {

    /* renamed from: a  reason: collision with root package name */
    public static d f4286a;

    /* renamed from: b  reason: collision with root package name */
    private PackageManager f4287b;

    /* renamed from: c  reason: collision with root package name */
    private Field f4288c;

    /* renamed from: d  reason: collision with root package name */
    private final Object f4289d = new Object();
    private Context e;
    private HashMap<String, PackageInfo> f = new HashMap<>();

    public d(Context context) {
        this.e = context.getApplicationContext();
        this.f4287b = this.e.getPackageManager();
    }

    public static synchronized d a(Context context) {
        d dVar;
        synchronized (d.class) {
            if (f4286a == null) {
                f4286a = new d(context);
            }
            dVar = f4286a;
        }
        return dVar;
    }

    private boolean a(ActivityInfo activityInfo) {
        int i;
        if (activityInfo == null) {
            return false;
        }
        try {
            if (this.f4288c == null) {
                this.f4288c = activityInfo.getClass().getDeclaredField("resizeMode");
                this.f4288c.setAccessible(true);
            }
            i = ((Integer) this.f4288c.get(activityInfo)).intValue();
        } catch (Exception e2) {
            Log.i("GameBoosterReflectUtils", e2.toString());
            i = 0;
        }
        return i != 0;
    }

    private String d() {
        return b.a("key_currentbooster_pkg_uid", (String) null).split(",")[0];
    }

    public HashMap<String, PackageInfo> a() {
        HashMap<String, PackageInfo> a2 = a((List<String>) null);
        for (String remove : a.a(this.e)) {
            a2.remove(remove);
        }
        return a2;
    }

    public HashMap<String, PackageInfo> a(List<String> list) {
        synchronized (this.f4289d) {
            HashMap<String, PackageInfo> hashMap = new HashMap<>();
            hashMap.putAll(this.f);
            if (!hashMap.isEmpty()) {
                return hashMap;
            }
            try {
                Application d2 = Application.d();
                List<PackageInfo> installedPackages = this.f4287b.getInstalledPackages(0);
                String d3 = d();
                for (PackageInfo next : installedPackages) {
                    if (C0393y.a(next.packageName, (Context) d2)) {
                        if (list == null || !list.contains(next.packageName)) {
                            if (TextUtils.isEmpty(d3) || !d3.equals(next.packageName)) {
                                ResolveInfo a2 = x.a((Context) d2, next.packageName);
                                if (a2 != null) {
                                    if (a2.activityInfo != null) {
                                        if (a(a2.activityInfo)) {
                                            this.f.put(next.packageName, next);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e2) {
                Log.i("VBRecommendAppMgr", "getAllApps Failed: " + e2.toString());
            }
            hashMap.putAll(this.f);
            return hashMap;
        }
    }

    public HashMap<String, PackageInfo> b() {
        return a((List<String>) null);
    }

    public void c() {
        this.f.clear();
    }
}
