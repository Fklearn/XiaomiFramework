package com.miui.permcenter;

import com.miui.common.persistence.b;

public class o {
    public static void a(boolean z) {
        b.b("key_has_shown_auto_start_declare", z);
    }

    public static boolean a() {
        return b.a("key_has_shown_auto_start_declare", false);
    }
}
