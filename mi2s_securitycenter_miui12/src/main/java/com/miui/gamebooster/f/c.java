package com.miui.gamebooster.f;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.provider.Settings;
import android.support.annotation.NonNull;
import b.b.c.j.x;
import b.b.o.g.e;
import com.miui.common.persistence.b;
import com.miui.gamebooster.c;
import com.miui.gamebooster.d.d;
import com.miui.gamebooster.m.C0393y;
import com.miui.gamebooster.model.g;
import com.miui.gamebooster.model.j;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.R;
import com.miui.securitycenter.n;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import miui.os.Build;

public class c {

    /* renamed from: a  reason: collision with root package name */
    private static c f4281a;

    /* renamed from: b  reason: collision with root package name */
    private HashMap<String, j> f4282b = new HashMap<>(20, 0.75f);

    /* renamed from: c  reason: collision with root package name */
    private List<j> f4283c = new ArrayList();

    /* renamed from: d  reason: collision with root package name */
    private HashMap<String, ResolveInfo> f4284d = new HashMap<>();

    private class a implements Comparator<j> {
        private a() {
        }

        /* synthetic */ a(c cVar, b bVar) {
            this();
        }

        /* renamed from: a */
        public int compare(j jVar, j jVar2) {
            return jVar2.c() - jVar.c();
        }
    }

    private c() {
        e();
    }

    public static synchronized c a() {
        c cVar;
        synchronized (c.class) {
            if (f4281a == null) {
                f4281a = new c();
            }
            cVar = f4281a;
        }
        return cVar;
    }

    private HashMap<String, j> a(Context context, List<String> list) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
        ArrayList arrayList = new ArrayList();
        arrayList.addAll(list);
        HashMap<String, j> hashMap = new HashMap<>();
        String d2 = d();
        ResolveInfo a2 = x.a(context, C0393y.a(Settings.Secure.getString(context.getContentResolver(), "gamebox_stick")));
        if (a2 != null) {
            arrayList.add(a2.activityInfo.packageName);
        }
        if (d2 != null) {
            arrayList.add(d2);
        }
        PackageManager packageManager = context.getPackageManager();
        try {
            for (ActivityManager.RecentTaskInfo recentTaskInfo : activityManager.getRecentTasks(64, 0)) {
                ResolveInfo resolveActivity = packageManager.resolveActivity(recentTaskInfo.baseIntent, 0);
                if (resolveActivity != null) {
                    String str = resolveActivity.activityInfo.packageName;
                    int intValue = ((Integer) e.a((Object) resolveActivity.activityInfo, "resizeMode", Integer.TYPE)).intValue();
                    if (!arrayList.contains(str) && C0393y.a(str, context) && intValue != 0) {
                        arrayList.add(str);
                        hashMap.put(str, new j(com.miui.gamebooster.d.c.RENCENT, resolveActivity, (g) null, R.layout.gamebox_list_item));
                    }
                }
            }
            if (a2 != null && !list.contains(a2)) {
                j jVar = new j(com.miui.gamebooster.d.c.RENCENT, a2, (g) null, R.layout.gamebox_list_item);
                jVar.a(true);
                hashMap.put(a2.activityInfo.applicationInfo.packageName, jVar);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hashMap;
    }

    @NonNull
    private HashMap<String, ResolveInfo> b(Context context) {
        for (String next : d.a(context).a().keySet()) {
            ResolveInfo a2 = x.a(context, next);
            if (a2 != null) {
                this.f4284d.put(next, a2);
            }
        }
        return this.f4284d;
    }

    private void c(Context context) {
        Set<String> keySet = d.a(context).a().keySet();
        ArrayList<String> arrayList = new ArrayList<>();
        HashMap<String, j> a2 = a(Application.d(), a.a(context));
        Set<String> keySet2 = a2.keySet();
        for (Map.Entry next : this.f4282b.entrySet()) {
            j jVar = (j) next.getValue();
            String str = (String) next.getKey();
            if (jVar != null) {
                if (keySet.contains(str)) {
                    keySet.remove(str);
                } else {
                    arrayList.add(str);
                }
            }
        }
        for (String str2 : arrayList) {
            this.f4283c.remove(this.f4282b.get(str2));
            this.f4282b.remove(str2);
        }
        for (String next2 : keySet) {
            ResolveInfo a3 = x.a(context, next2);
            if (a3 != null) {
                this.f4282b.put(next2, new j(com.miui.gamebooster.d.c.RENCENT, a3, (g) null, R.layout.gb_h_recommend_app_item, 0));
                this.f4283c.add(this.f4282b.get(next2));
            }
        }
        Set<String> keySet3 = this.f4282b.keySet();
        for (String next3 : keySet2) {
            if (!keySet3.contains(next3)) {
                this.f4282b.put(next3, a2.get(next3));
                this.f4283c.add(this.f4282b.get(next3));
            }
        }
        for (Map.Entry next4 : this.f4282b.entrySet()) {
            j jVar2 = (j) next4.getValue();
            String str3 = (String) next4.getKey();
            if (!(jVar2 == null || jVar2.c() == 2)) {
                jVar2.a(keySet2.contains(str3) ? 1 : 0);
            }
        }
        Collections.sort(this.f4283c, new a(this, (b) null));
    }

    private String d() {
        return b.a("key_currentbooster_pkg_uid", (String) null).split(",")[0];
    }

    private void e() {
        Application d2 = Application.d();
        List<String> b2 = com.miui.gamebooster.c.a((Context) d2).b();
        HashMap<String, ResolveInfo> b3 = b(d2);
        Set<String> keySet = b3.keySet();
        HashMap<String, j> a2 = a(d2, a.a(d2));
        for (String next : b2) {
            if (keySet.contains(next)) {
                this.f4282b.put(next, new j(com.miui.gamebooster.d.c.RENCENT, b3.get(next), (g) null, R.layout.gamebox_list_item, 2));
                a2.remove(next);
                keySet.remove(next);
            }
        }
        for (String next2 : a2.keySet()) {
            j jVar = a2.get(next2);
            if (jVar != null) {
                jVar.a(1);
                this.f4282b.put(next2, jVar);
                keySet.remove(next2);
            }
        }
        for (String next3 : keySet) {
            this.f4282b.put(next3, new j(com.miui.gamebooster.d.c.RENCENT, b3.get(next3), (g) null, R.layout.gb_h_recommend_app_item, 0));
        }
        if (this.f4282b.containsKey("com.android.browser") && !x.g(d2, "com.android.browser")) {
            this.f4282b.remove("com.android.browser");
        }
        this.f4283c.addAll(this.f4282b.values());
        Collections.sort(this.f4283c, new a(this, (b) null));
    }

    private int f() {
        g b2;
        List<j> list = this.f4283c;
        if (list == null || list.size() <= 0) {
            return -1;
        }
        for (int size = this.f4283c.size() - 1; size >= 0; size--) {
            j jVar = this.f4283c.get(size);
            if (jVar != null && (b2 = jVar.b()) != null && d.QUICKBROWSER == b2.c()) {
                return size;
            }
        }
        return -1;
    }

    public List<j> a(Context context) {
        j jVar;
        j jVar2;
        c(context);
        if (Build.IS_INTERNATIONAL_BUILD && !x.g(context, "com.android.browser") && !x.g(context, "com.mi.globalbrowser") && (jVar2 = this.f4282b.get("com.android.chrome")) != null) {
            this.f4283c.remove(jVar2);
        }
        int f = f();
        if (f < 0) {
            if (b.b.l.b.b().a(d())) {
                j jVar3 = new j(com.miui.gamebooster.d.c.RENCENT, x.a(context, "com.android.browser"), new g(d.QUICKBROWSER, R.drawable.gamebox_game_button), R.layout.gamebox_function_item);
                jVar3.a(2);
                this.f4283c.add(jVar3);
            }
        } else if (!b.b.l.b.b().a(d())) {
            this.f4283c.remove(f);
        }
        if (this.f4282b.containsKey("com.android.browser") && !x.g(context, "com.android.browser") && (jVar = this.f4282b.get("com.android.browser")) != null) {
            this.f4283c.remove(jVar);
        }
        return this.f4283c;
    }

    public void a(String str) {
        j jVar = this.f4282b.get(str);
        if (jVar != null) {
            jVar.a(2);
            this.f4283c.remove(jVar);
            this.f4283c.add(0, jVar);
        }
        n.a().b(new b(this));
    }

    public boolean b() {
        return this.f4282b.isEmpty();
    }

    public void c() {
        ArrayList arrayList = new ArrayList();
        for (j next : this.f4283c) {
            if (next.c() == 2 && next.d() != null) {
                arrayList.add(next.d().activityInfo.applicationInfo.processName);
            }
        }
        c.a a2 = com.miui.gamebooster.c.a((Context) Application.d()).a();
        a2.a(arrayList);
        a2.a();
        Collections.sort(this.f4283c, new a(this, (b) null));
    }
}
