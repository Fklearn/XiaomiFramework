package com.miui.superpower.b;

import android.util.ArrayMap;
import b.b.c.j.y;
import com.miui.activityutil.h;

public class b {

    /* renamed from: a  reason: collision with root package name */
    private static final ArrayMap<String, Float> f8070a = new ArrayMap<>();

    /* renamed from: b  reason: collision with root package name */
    private static final ArrayMap<String, Float> f8071b = new ArrayMap<>();

    /* renamed from: c  reason: collision with root package name */
    private static float f8072c;

    /* renamed from: d  reason: collision with root package name */
    private static float f8073d;
    private static final String e = y.a("ro.product.device", h.f2289a);

    static {
        f8070a.put("sagit", Float.valueOf(13.89f));
        f8070a.put("dipper", Float.valueOf(11.56f));
        f8070a.put("cepheus", Float.valueOf(11.79f));
        for (Float floatValue : f8070a.values()) {
            f8072c += floatValue.floatValue();
        }
        f8072c /= (float) f8070a.size();
        f8071b.put("sagit", Float.valueOf(16.0f));
        f8071b.put("dipper", Float.valueOf(14.0f));
        f8071b.put("cepheus", Float.valueOf(15.0f));
        for (Float floatValue2 : f8071b.values()) {
            f8073d += floatValue2.floatValue();
        }
        f8073d /= (float) f8071b.size();
    }

    public static Float a() {
        return f8070a.containsKey(e) ? f8070a.get(e) : Float.valueOf(f8072c);
    }

    public static Float b() {
        return f8071b.containsKey(e) ? f8071b.get(e) : Float.valueOf(f8073d);
    }
}
