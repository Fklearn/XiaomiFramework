package com.miui.superpower;

import android.text.TextUtils;
import android.util.Log;
import com.miui.powercenter.quickoptimize.z;
import com.miui.securitycenter.Application;
import com.miui.superpower.b.k;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class b {

    /* renamed from: a  reason: collision with root package name */
    private static final List<String> f8067a = new ArrayList();

    /* renamed from: b  reason: collision with root package name */
    private static final List<String> f8068b = new ArrayList();

    static {
        f8067a.add("com.xiaomi.aiasst.service");
        f8067a.add("com.miui.securitycenter");
        f8067a.add("com.android.camera");
        f8067a.add("com.android.settings");
        f8067a.add("com.android.thememanager");
        f8067a.add("com.miui.screenrecorder");
    }

    public static List<String> a() {
        return com.miui.common.persistence.b.a("pref_key_superpower_cloud_black_processname", (ArrayList<String>) new ArrayList());
    }

    public static void a(List<String> list) {
        Application d2 = Application.d();
        List<String> a2 = k.a(d2.getPackageManager(), 0, (HashSet<String>) null);
        List<String> a3 = k.a(d2.getPackageManager(), 999, (HashSet<String>) null);
        if (list != null && !list.isEmpty()) {
            for (String next : list) {
                if (a(next, a2)) {
                    a2.remove(next);
                }
            }
        }
        a2.removeAll(b());
        a2.addAll(a());
        String b2 = k.b(d2);
        if (!TextUtils.isEmpty(b2)) {
            a2.remove(b2);
        }
        a(a2, a3);
    }

    private static void a(List<String> list, List<String> list2) {
        if (list != null && !list.isEmpty()) {
            z.b(list, 0);
        }
        if (list2 != null && !list2.isEmpty()) {
            z.b(list2, 999);
        }
    }

    private static boolean a(String str, List<String> list) {
        if (list != null && !list.isEmpty()) {
            for (String next : list) {
                if (next != null && (next.contains(str) || next.startsWith(str))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static List<String> b() {
        ArrayList<String> a2 = com.miui.common.persistence.b.a("pref_key_superpower_cloud_white_processname", (ArrayList<String>) new ArrayList());
        for (int i = 0; i < f8067a.size(); i++) {
            if (!a2.contains(f8067a.get(i))) {
                a2.add(f8067a.get(i));
            }
        }
        return a2;
    }

    public static void b(List<String> list) {
        Application d2 = Application.d();
        List<String> h = k.h(d2);
        if (list != null && !list.isEmpty()) {
            for (String next : list) {
                if (a(next, h)) {
                    h.remove(next);
                }
            }
        }
        h.removeAll(b());
        h.addAll(a());
        String b2 = k.b(d2);
        if (!TextUtils.isEmpty(b2)) {
            h.remove(b2);
        }
        ArrayList arrayList = new ArrayList();
        if (!h.isEmpty()) {
            for (int size = h.size() - 1; size >= 0; size--) {
                Log.e("MemoryCleanManager", h.get(size));
                if (h.get(size).endsWith(":999")) {
                    arrayList.add(h.get(size).replace(":999", ""));
                    h.remove(size);
                }
            }
        }
        a(h, (List<String>) arrayList);
    }
}
