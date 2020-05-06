package com.miui.powercenter.quickoptimize;

import java.util.HashSet;
import java.util.Set;

public class L {

    /* renamed from: a  reason: collision with root package name */
    private static Set<Object> f7202a = new HashSet();

    public static void a(Object obj, boolean z) {
        if (z) {
            f7202a.remove(obj);
        } else {
            f7202a.add(obj);
        }
    }

    public static boolean a(Object obj) {
        return !f7202a.contains(obj);
    }
}
