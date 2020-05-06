package com.miui.monthreport;

import com.miui.common.persistence.b;

public class g {
    public static long a() {
        return b.a("key_last_located_time", 0);
    }

    public static String a(String str) {
        return b.a("key_update_located_address", str);
    }

    public static void a(long j) {
        b.b("key_last_located_time", j);
    }

    public static void a(boolean z) {
        b.b("key_month_report_enabled", z);
    }

    public static void b(String str) {
        b.b("key_update_located_address", str);
    }

    public static boolean b() {
        return b.a("key_month_report_enabled", true);
    }
}
