package com.miui.superpower.b;

import com.miui.common.persistence.b;

public class g {
    public static void a(boolean z) {
        b.b("key_superpower_autoleave", z);
    }

    public static boolean a() {
        return b.a("key_superpower_autoenter", false);
    }

    public static void b(boolean z) {
        b.b("key_superpower_dialog_enable", z);
    }

    public static boolean b() {
        return b.a("key_superpower_autoleave", false);
    }

    public static boolean c() {
        return b.a("key_superpower_dialog_enable", true);
    }
}
