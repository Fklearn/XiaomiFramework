package com.miui.hybrid.accessory.sdk.a;

import com.miui.hybrid.accessory.a.f.a.b;
import com.miui.hybrid.accessory.a.f.a.c;
import com.miui.hybrid.accessory.a.f.b.e;
import com.miui.hybrid.accessory.a.f.b.h;
import com.miui.hybrid.accessory.a.f.b.j;
import com.miui.maml.elements.AdvancedSlider;
import java.io.Serializable;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class f implements com.miui.hybrid.accessory.a.f.a<f, a>, Serializable, Cloneable {

    /* renamed from: c  reason: collision with root package name */
    public static final Map<a, b> f5567c;

    /* renamed from: d  reason: collision with root package name */
    private static final j f5568d = new j("ResultState");
    private static final com.miui.hybrid.accessory.a.f.b.b e = new com.miui.hybrid.accessory.a.f.b.b(AdvancedSlider.STATE, (byte) 8, 1);
    private static final com.miui.hybrid.accessory.a.f.b.b f = new com.miui.hybrid.accessory.a.f.b.b("packageName", (byte) 11, 2);

    /* renamed from: a  reason: collision with root package name */
    public h f5569a = h.OK;

    /* renamed from: b  reason: collision with root package name */
    public String f5570b;

    public enum a {
        STATE(1, AdvancedSlider.STATE),
        PACKAGE_NAME(2, "packageName");
        

        /* renamed from: c  reason: collision with root package name */
        private static final Map<String, a> f5573c = null;

        /* renamed from: d  reason: collision with root package name */
        private final short f5574d;
        private final String e;

        static {
            f5573c = new HashMap();
            Iterator it = EnumSet.allOf(a.class).iterator();
            while (it.hasNext()) {
                a aVar = (a) it.next();
                f5573c.put(aVar.a(), aVar);
            }
        }

        private a(short s, String str) {
            this.f5574d = s;
            this.e = str;
        }

        public String a() {
            return this.e;
        }
    }

    static {
        EnumMap enumMap = new EnumMap(a.class);
        enumMap.put(a.STATE, new b(AdvancedSlider.STATE, (byte) 1, new com.miui.hybrid.accessory.a.f.a.a((byte) 16, h.class)));
        enumMap.put(a.PACKAGE_NAME, new b("packageName", (byte) 2, new c((byte) 11)));
        f5567c = Collections.unmodifiableMap(enumMap);
        b.a(f.class, f5567c);
    }

    public void a(e eVar) {
        eVar.a();
        while (true) {
            com.miui.hybrid.accessory.a.f.b.b c2 = eVar.c();
            byte b2 = c2.f5513b;
            if (b2 == 0) {
                eVar.b();
                c();
                return;
            }
            short s = c2.f5514c;
            if (s != 1) {
                if (s == 2 && b2 == 11) {
                    this.f5570b = eVar.q();
                    eVar.d();
                }
            } else if (b2 == 8) {
                this.f5569a = h.a(eVar.n());
                eVar.d();
            }
            h.a(eVar, b2);
            eVar.d();
        }
    }

    public boolean a() {
        return this.f5569a != null;
    }

    public boolean a(f fVar) {
        if (fVar == null) {
            return false;
        }
        boolean a2 = a();
        boolean a3 = fVar.a();
        if ((a2 || a3) && (!a2 || !a3 || !this.f5569a.equals(fVar.f5569a))) {
            return false;
        }
        boolean b2 = b();
        boolean b3 = fVar.b();
        if (b2 || b3) {
            return b2 && b3 && this.f5570b.equals(fVar.f5570b);
        }
        return true;
    }

    /* renamed from: b */
    public int compareTo(f fVar) {
        int a2;
        int a3;
        if (!f.class.equals(fVar.getClass())) {
            return f.class.getName().compareTo(fVar.getClass().getName());
        }
        int compareTo = Boolean.valueOf(a()).compareTo(Boolean.valueOf(fVar.a()));
        if (compareTo != 0) {
            return compareTo;
        }
        if (a() && (a3 = com.miui.hybrid.accessory.a.f.b.a((Comparable) this.f5569a, (Comparable) fVar.f5569a)) != 0) {
            return a3;
        }
        int compareTo2 = Boolean.valueOf(b()).compareTo(Boolean.valueOf(fVar.b()));
        if (compareTo2 != 0) {
            return compareTo2;
        }
        if (!b() || (a2 = com.miui.hybrid.accessory.a.f.b.a(this.f5570b, fVar.f5570b)) == 0) {
            return 0;
        }
        return a2;
    }

    public boolean b() {
        return this.f5570b != null;
    }

    public void c() {
        if (this.f5569a == null) {
            throw new com.miui.hybrid.accessory.a.f.b.f("Required field 'state' was not present! Struct: " + toString());
        }
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof f)) {
            return a((f) obj);
        }
        return false;
    }

    public int hashCode() {
        return 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("ResultState(");
        sb.append("state:");
        h hVar = this.f5569a;
        if (hVar == null) {
            sb.append("null");
        } else {
            sb.append(hVar);
        }
        if (b()) {
            sb.append(", ");
            sb.append("packageName:");
            String str = this.f5570b;
            if (str == null) {
                sb.append("null");
            } else {
                sb.append(str);
            }
        }
        sb.append(")");
        return sb.toString();
    }
}
