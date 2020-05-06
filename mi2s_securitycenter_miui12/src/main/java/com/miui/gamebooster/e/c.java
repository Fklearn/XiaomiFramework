package com.miui.gamebooster.e;

import android.text.TextUtils;
import android.util.Log;
import b.b.o.g.e;
import com.miui.gamebooster.m.C0388t;
import java.util.ArrayList;
import java.util.List;

public class c {

    /* renamed from: a  reason: collision with root package name */
    private static final List<String> f4279a = new ArrayList();

    static {
        f4279a.add("com.tencent.tmgp.pubgmhd");
        f4279a.add("com.tencent.tmgp.pubgm");
    }

    public static List<String> a() {
        ArrayList arrayList = new ArrayList();
        if (C0388t.a("android.os.SystemProperties", "ro.vendor.audio.game.effect", false)) {
            arrayList.addAll(f4279a);
        }
        List<String> c2 = c();
        if (c2 != null) {
            for (String next : c2) {
                if (!TextUtils.isEmpty(next) && !arrayList.contains(next)) {
                    arrayList.add(next);
                }
            }
        }
        Log.i("VibrationUtils", "getSupport4DAppList: " + arrayList);
        return arrayList;
    }

    public static boolean b() {
        return d() || e();
    }

    private static List<String> c() {
        try {
            return (List) e.a(Class.forName("com.xiaomi.joyose.JoyoseManager"), List.class, "getGameMotorAppList", (Class<?>[]) null, (Object[]) null);
        } catch (Exception e) {
            Log.e("VibrationUtils", e.toString());
            return null;
        }
    }

    private static boolean d() {
        return C0388t.a("android.os.SystemProperties", "ro.vendor.audio.game.effect", false);
    }

    private static boolean e() {
        List<String> c2 = c();
        return c2 != null && !c2.isEmpty();
    }
}
