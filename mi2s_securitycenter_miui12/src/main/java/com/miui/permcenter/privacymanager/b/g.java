package com.miui.permcenter.privacymanager.b;

public class g extends b {

    /* renamed from: b  reason: collision with root package name */
    private static g f6361b;

    private g() {
    }

    public static g f() {
        synchronized (o.class) {
            if (f6361b == null) {
                f6361b = new g();
            }
        }
        return f6361b;
    }

    /* access modifiers changed from: package-private */
    public boolean a() {
        return false;
    }

    /* access modifiers changed from: package-private */
    public String d() {
        return "intro_show_";
    }
}
