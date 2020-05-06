package com.miui.hybrid.accessory.sdk.a;

import com.miui.hybrid.accessory.a.f.a.b;
import com.miui.hybrid.accessory.a.f.a.c;
import com.miui.hybrid.accessory.a.f.b.e;
import com.miui.hybrid.accessory.a.f.b.f;
import com.miui.hybrid.accessory.a.f.b.h;
import com.miui.hybrid.accessory.a.f.b.j;
import java.io.Serializable;
import java.util.BitSet;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class g implements com.miui.hybrid.accessory.a.f.a<g, a>, Serializable, Cloneable {

    /* renamed from: d  reason: collision with root package name */
    public static final Map<a, b> f5575d;
    private static final j e = new j("ServerSetting");
    private static final com.miui.hybrid.accessory.a.f.b.b f = new com.miui.hybrid.accessory.a.f.b.b("lastModifyTime", (byte) 10, 1);
    private static final com.miui.hybrid.accessory.a.f.b.b g = new com.miui.hybrid.accessory.a.f.b.b("packageName", (byte) 11, 2);
    private static final com.miui.hybrid.accessory.a.f.b.b h = new com.miui.hybrid.accessory.a.f.b.b("setting", (byte) 11, 3);

    /* renamed from: a  reason: collision with root package name */
    public long f5576a;

    /* renamed from: b  reason: collision with root package name */
    public String f5577b;

    /* renamed from: c  reason: collision with root package name */
    public String f5578c;
    private BitSet i = new BitSet(1);

    public enum a {
        LAST_MODIFY_TIME(1, "lastModifyTime"),
        PACKAGE_NAME(2, "packageName"),
        SETTING(3, "setting");
        

        /* renamed from: d  reason: collision with root package name */
        private static final Map<String, a> f5582d = null;
        private final short e;
        private final String f;

        static {
            f5582d = new HashMap();
            Iterator it = EnumSet.allOf(a.class).iterator();
            while (it.hasNext()) {
                a aVar = (a) it.next();
                f5582d.put(aVar.a(), aVar);
            }
        }

        private a(short s, String str) {
            this.e = s;
            this.f = str;
        }

        public String a() {
            return this.f;
        }
    }

    static {
        EnumMap enumMap = new EnumMap(a.class);
        enumMap.put(a.LAST_MODIFY_TIME, new b("lastModifyTime", (byte) 1, new c((byte) 10)));
        enumMap.put(a.PACKAGE_NAME, new b("packageName", (byte) 1, new c((byte) 11)));
        enumMap.put(a.SETTING, new b("setting", (byte) 1, new c((byte) 11)));
        f5575d = Collections.unmodifiableMap(enumMap);
        b.a(g.class, f5575d);
    }

    public void a(e eVar) {
        eVar.a();
        while (true) {
            com.miui.hybrid.accessory.a.f.b.b c2 = eVar.c();
            byte b2 = c2.f5513b;
            if (b2 == 0) {
                break;
            }
            short s = c2.f5514c;
            if (s != 1) {
                if (s != 2) {
                    if (s == 3 && b2 == 11) {
                        this.f5578c = eVar.q();
                        eVar.d();
                    }
                } else if (b2 == 11) {
                    this.f5577b = eVar.q();
                    eVar.d();
                }
            } else if (b2 == 10) {
                this.f5576a = eVar.o();
                a(true);
                eVar.d();
            }
            h.a(eVar, b2);
            eVar.d();
        }
        eVar.b();
        if (a()) {
            d();
            return;
        }
        throw new f("Required field 'lastModifyTime' was not found in serialized data! Struct: " + toString());
    }

    public void a(boolean z) {
        this.i.set(0, z);
    }

    public boolean a() {
        return this.i.get(0);
    }

    public boolean a(g gVar) {
        if (gVar == null || this.f5576a != gVar.f5576a) {
            return false;
        }
        boolean b2 = b();
        boolean b3 = gVar.b();
        if ((b2 || b3) && (!b2 || !b3 || !this.f5577b.equals(gVar.f5577b))) {
            return false;
        }
        boolean c2 = c();
        boolean c3 = gVar.c();
        if (c2 || c3) {
            return c2 && c3 && this.f5578c.equals(gVar.f5578c);
        }
        return true;
    }

    /* renamed from: b */
    public int compareTo(g gVar) {
        int a2;
        int a3;
        int a4;
        if (!g.class.equals(gVar.getClass())) {
            return g.class.getName().compareTo(gVar.getClass().getName());
        }
        int compareTo = Boolean.valueOf(a()).compareTo(Boolean.valueOf(gVar.a()));
        if (compareTo != 0) {
            return compareTo;
        }
        if (a() && (a4 = com.miui.hybrid.accessory.a.f.b.a(this.f5576a, gVar.f5576a)) != 0) {
            return a4;
        }
        int compareTo2 = Boolean.valueOf(b()).compareTo(Boolean.valueOf(gVar.b()));
        if (compareTo2 != 0) {
            return compareTo2;
        }
        if (b() && (a3 = com.miui.hybrid.accessory.a.f.b.a(this.f5577b, gVar.f5577b)) != 0) {
            return a3;
        }
        int compareTo3 = Boolean.valueOf(c()).compareTo(Boolean.valueOf(gVar.c()));
        if (compareTo3 != 0) {
            return compareTo3;
        }
        if (!c() || (a2 = com.miui.hybrid.accessory.a.f.b.a(this.f5578c, gVar.f5578c)) == 0) {
            return 0;
        }
        return a2;
    }

    public boolean b() {
        return this.f5577b != null;
    }

    public boolean c() {
        return this.f5578c != null;
    }

    public void d() {
        if (this.f5577b == null) {
            throw new f("Required field 'packageName' was not present! Struct: " + toString());
        } else if (this.f5578c == null) {
            throw new f("Required field 'setting' was not present! Struct: " + toString());
        }
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof g)) {
            return a((g) obj);
        }
        return false;
    }

    public int hashCode() {
        return 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("ServerSetting(");
        sb.append("lastModifyTime:");
        sb.append(this.f5576a);
        sb.append(", ");
        sb.append("packageName:");
        String str = this.f5577b;
        if (str == null) {
            sb.append("null");
        } else {
            sb.append(str);
        }
        sb.append(", ");
        sb.append("setting:");
        String str2 = this.f5578c;
        if (str2 == null) {
            sb.append("null");
        } else {
            sb.append(str2);
        }
        sb.append(")");
        return sb.toString();
    }
}
