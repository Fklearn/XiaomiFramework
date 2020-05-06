package com.miui.permcenter.settings;

import com.miui.common.persistence.b;

public class x {
    public static void a(boolean z) {
        b.b("pref_ignore_permission_dialog", z);
    }

    public static boolean a() {
        return b.a("pref_ignore_permission_dialog", false);
    }

    public static void b(boolean z) {
        b.b("pref_first_open_privacy_settings", z);
    }

    public static boolean b() {
        return b.a("pref_first_open_privacy_settings", true);
    }

    public static void c(boolean z) {
        b.b("pref_first_use_permission_dialog", z);
    }

    public static boolean c() {
        return b.a("pref_first_use_permission_dialog", true);
    }
}
