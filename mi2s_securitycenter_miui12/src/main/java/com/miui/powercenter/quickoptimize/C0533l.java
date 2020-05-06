package com.miui.powercenter.quickoptimize;

import com.miui.common.persistence.b;

/* renamed from: com.miui.powercenter.quickoptimize.l  reason: case insensitive filesystem */
public class C0533l {
    public static long a() {
        return b.a("key_last_kill_running_app_time", 0);
    }

    public static void a(long j) {
        b.b("key_last_kill_running_app_time", j);
    }
}
