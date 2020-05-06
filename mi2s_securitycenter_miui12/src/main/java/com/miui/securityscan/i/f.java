package com.miui.securityscan.i;

public class f {

    /* renamed from: a  reason: collision with root package name */
    private static long f7725a;

    public static synchronized boolean a() {
        boolean z;
        synchronized (f.class) {
            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis - f7725a >= 500 || currentTimeMillis - f7725a <= 0) {
                f7725a = currentTimeMillis;
                z = false;
            } else {
                z = true;
            }
        }
        return z;
    }
}
