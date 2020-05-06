package com.miui.gamebooster.e.a;

import android.content.Context;
import android.os.Build;
import b.b.o.g.e;
import com.miui.gamebooster.m.C0388t;
import java.util.ArrayList;
import java.util.List;
import miui.util.Log;

public class a {

    /* renamed from: a  reason: collision with root package name */
    private static final List<String> f4278a = new ArrayList();

    static {
        f4278a.add("lmi");
        f4278a.add("lmipro");
        f4278a.add("lmiin");
        f4278a.add("lmiinpro");
    }

    public static void a(Context context, boolean z) {
        if (C0388t.j() && !f4278a.contains(Build.DEVICE)) {
            try {
                int i = 4;
                if (Build.VERSION.SDK_INT > 28) {
                    Object a2 = e.a(Class.forName("android.net.wifi.MiuiWifiManager"), "getInstance", (Class<?>[]) new Class[]{Context.class}, context);
                    Class[] clsArr = {Integer.TYPE};
                    Object[] objArr = new Object[1];
                    if (!z) {
                        i = 1;
                    }
                    objArr[0] = Integer.valueOf(i);
                    e.a(a2, (Class) null, "setLatencyLevel", (Class<?>[]) clsArr, objArr);
                } else {
                    Class<?> cls = Class.forName("android.net.wifi.MiuiWifiManager");
                    Class[] clsArr2 = {Integer.TYPE};
                    Object[] objArr2 = new Object[1];
                    if (!z) {
                        i = 1;
                    }
                    objArr2[0] = Integer.valueOf(i);
                    e.a(cls, "setLatencyLevel", (Class<?>[]) clsArr2, objArr2);
                }
                Log.i("MiuiWifiManagerCompat", "mWifiOptimaze..." + z);
            } catch (Exception e) {
                Log.i("MiuiWifiManagerCompat", e.toString());
            }
        }
    }
}
