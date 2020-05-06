package com.miui.gamebooster.m;

import android.text.TextUtils;
import java.util.concurrent.ConcurrentHashMap;

public class la {

    /* renamed from: a  reason: collision with root package name */
    private static volatile la f4500a;

    /* renamed from: b  reason: collision with root package name */
    private ConcurrentHashMap<String, a> f4501b = new ConcurrentHashMap<>();

    /* renamed from: c  reason: collision with root package name */
    private ConcurrentHashMap<String, String> f4502c = new ConcurrentHashMap<>();

    public static class a {

        /* renamed from: a  reason: collision with root package name */
        private String f4503a;

        /* renamed from: b  reason: collision with root package name */
        private int f4504b;

        /* renamed from: c  reason: collision with root package name */
        private boolean f4505c;

        private a(String str, int i, boolean z) {
            this.f4503a = str;
            this.f4504b = i;
            this.f4505c = z;
        }
    }

    private la() {
    }

    public static la a() {
        if (f4500a == null) {
            synchronized (la.class) {
                if (f4500a == null) {
                    f4500a = new la();
                }
            }
        }
        return f4500a;
    }

    public void a(String str, String str2, int i, boolean z) {
        if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2)) {
            if (!str.startsWith("http")) {
                str = ha.a(str);
            }
            this.f4502c.put(str2, str);
            this.f4501b.put(str, new a(str2, i, z));
        }
    }
}
