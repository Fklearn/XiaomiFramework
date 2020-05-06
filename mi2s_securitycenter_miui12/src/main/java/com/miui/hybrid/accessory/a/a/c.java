package com.miui.hybrid.accessory.a.a;

import android.os.Build;
import com.miui.activityutil.h;
import com.miui.maml.folme.AnimatedProperty;

public final class c {
    public static String a() {
        try {
            Class<?> cls = Class.forName("miui.os.Build");
            return cls.getField("IS_ALPHA_BUILD").getBoolean((Object) null) ? AnimatedProperty.PROPERTY_NAME_ALPHA : cls.getField("IS_DEVELOPMENT_VERSION").getBoolean((Object) null) ? "dev" : cls.getField("IS_STABLE_VERSION").getBoolean((Object) null) ? "stable" : h.f2289a;
        } catch (Exception unused) {
            return h.f2289a;
        }
    }

    public static String b() {
        return Build.VERSION.INCREMENTAL + "(" + a() + ")";
    }

    public static boolean c() {
        try {
            return Class.forName("miui.os.Build").getField("IS_GLOBAL_BUILD").getBoolean((Object) null);
        } catch (Exception unused) {
            return false;
        }
    }
}
