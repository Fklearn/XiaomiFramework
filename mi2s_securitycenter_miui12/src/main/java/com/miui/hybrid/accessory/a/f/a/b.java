package com.miui.hybrid.accessory.a.f.a;

import com.miui.hybrid.accessory.a.f.a;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class b implements Serializable {

    /* renamed from: d  reason: collision with root package name */
    private static Map<Class<? extends a>, Map<?, b>> f5492d = new HashMap();

    /* renamed from: a  reason: collision with root package name */
    public final String f5493a;

    /* renamed from: b  reason: collision with root package name */
    public final byte f5494b;

    /* renamed from: c  reason: collision with root package name */
    public final c f5495c;

    public b(String str, byte b2, c cVar) {
        this.f5493a = str;
        this.f5494b = b2;
        this.f5495c = cVar;
    }

    public static void a(Class<? extends a> cls, Map<?, b> map) {
        f5492d.put(cls, map);
    }
}
