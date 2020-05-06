package com.miui.hybrid.accessory.sdk.a;

import com.miui.hybrid.accessory.a.f.a.b;
import com.miui.hybrid.accessory.a.f.a.c;
import com.miui.hybrid.accessory.a.f.a.e;
import com.miui.hybrid.accessory.a.f.a.g;
import com.miui.hybrid.accessory.a.f.b.f;
import com.miui.hybrid.accessory.a.f.b.h;
import com.miui.hybrid.accessory.a.f.b.j;
import com.miui.permission.PermissionContract;
import java.io.Serializable;
import java.util.BitSet;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class d implements com.miui.hybrid.accessory.a.f.a<d, a>, Serializable, Cloneable {

    /* renamed from: d  reason: collision with root package name */
    public static final Map<a, b> f5551d;
    private static final j e = new j("NativeAppQueryResult");
    private static final com.miui.hybrid.accessory.a.f.b.b f = new com.miui.hybrid.accessory.a.f.b.b("errorCode", (byte) 8, 1);
    private static final com.miui.hybrid.accessory.a.f.b.b g = new com.miui.hybrid.accessory.a.f.b.b(PermissionContract.Method.SavePermissionDescription.EXTRA_DESCRIPTION, (byte) 11, 2);
    private static final com.miui.hybrid.accessory.a.f.b.b h = new com.miui.hybrid.accessory.a.f.b.b("appQueryResultMap", (byte) 13, 3);

    /* renamed from: a  reason: collision with root package name */
    public int f5552a;

    /* renamed from: b  reason: collision with root package name */
    public String f5553b;

    /* renamed from: c  reason: collision with root package name */
    public Map<String, b> f5554c;
    private BitSet i = new BitSet(1);

    public enum a {
        ERROR_CODE(1, "errorCode"),
        DESCRIPTION(2, PermissionContract.Method.SavePermissionDescription.EXTRA_DESCRIPTION),
        APP_QUERY_RESULT_MAP(3, "appQueryResultMap");
        

        /* renamed from: d  reason: collision with root package name */
        private static final Map<String, a> f5558d = null;
        private final short e;
        private final String f;

        static {
            f5558d = new HashMap();
            Iterator it = EnumSet.allOf(a.class).iterator();
            while (it.hasNext()) {
                a aVar = (a) it.next();
                f5558d.put(aVar.a(), aVar);
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
        enumMap.put(a.ERROR_CODE, new b("errorCode", (byte) 1, new c((byte) 8)));
        enumMap.put(a.DESCRIPTION, new b(PermissionContract.Method.SavePermissionDescription.EXTRA_DESCRIPTION, (byte) 1, new c((byte) 11)));
        enumMap.put(a.APP_QUERY_RESULT_MAP, new b("appQueryResultMap", (byte) 1, new e((byte) 13, new c((byte) 11), new g((byte) 12, b.class))));
        f5551d = Collections.unmodifiableMap(enumMap);
        b.a(d.class, f5551d);
    }

    public void a(com.miui.hybrid.accessory.a.f.b.e eVar) {
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
                    if (s == 3 && b2 == 13) {
                        com.miui.hybrid.accessory.a.f.b.d e2 = eVar.e();
                        this.f5554c = new HashMap(e2.f5519c * 2);
                        for (int i2 = 0; i2 < e2.f5519c; i2++) {
                            String q = eVar.q();
                            b bVar = new b();
                            bVar.a(eVar);
                            this.f5554c.put(q, bVar);
                        }
                        eVar.f();
                        eVar.d();
                    }
                } else if (b2 == 11) {
                    this.f5553b = eVar.q();
                    eVar.d();
                }
            } else if (b2 == 8) {
                this.f5552a = eVar.n();
                a(true);
                eVar.d();
            }
            h.a(eVar, b2);
            eVar.d();
        }
        eVar.b();
        if (a()) {
            e();
            return;
        }
        throw new f("Required field 'errorCode' was not found in serialized data! Struct: " + toString());
    }

    public void a(boolean z) {
        this.i.set(0, z);
    }

    public boolean a() {
        return this.i.get(0);
    }

    public boolean a(d dVar) {
        if (dVar == null || this.f5552a != dVar.f5552a) {
            return false;
        }
        boolean b2 = b();
        boolean b3 = dVar.b();
        if ((b2 || b3) && (!b2 || !b3 || !this.f5553b.equals(dVar.f5553b))) {
            return false;
        }
        boolean d2 = d();
        boolean d3 = dVar.d();
        if (d2 || d3) {
            return d2 && d3 && this.f5554c.equals(dVar.f5554c);
        }
        return true;
    }

    /* renamed from: b */
    public int compareTo(d dVar) {
        int a2;
        int a3;
        int a4;
        if (!d.class.equals(dVar.getClass())) {
            return d.class.getName().compareTo(dVar.getClass().getName());
        }
        int compareTo = Boolean.valueOf(a()).compareTo(Boolean.valueOf(dVar.a()));
        if (compareTo != 0) {
            return compareTo;
        }
        if (a() && (a4 = com.miui.hybrid.accessory.a.f.b.a(this.f5552a, dVar.f5552a)) != 0) {
            return a4;
        }
        int compareTo2 = Boolean.valueOf(b()).compareTo(Boolean.valueOf(dVar.b()));
        if (compareTo2 != 0) {
            return compareTo2;
        }
        if (b() && (a3 = com.miui.hybrid.accessory.a.f.b.a(this.f5553b, dVar.f5553b)) != 0) {
            return a3;
        }
        int compareTo3 = Boolean.valueOf(d()).compareTo(Boolean.valueOf(dVar.d()));
        if (compareTo3 != 0) {
            return compareTo3;
        }
        if (!d() || (a2 = com.miui.hybrid.accessory.a.f.b.a((Map) this.f5554c, (Map) dVar.f5554c)) == 0) {
            return 0;
        }
        return a2;
    }

    public boolean b() {
        return this.f5553b != null;
    }

    public Map<String, b> c() {
        return this.f5554c;
    }

    public boolean d() {
        return this.f5554c != null;
    }

    public void e() {
        if (this.f5553b == null) {
            throw new f("Required field 'description' was not present! Struct: " + toString());
        } else if (this.f5554c == null) {
            throw new f("Required field 'appQueryResultMap' was not present! Struct: " + toString());
        }
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof d)) {
            return a((d) obj);
        }
        return false;
    }

    public int hashCode() {
        return 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("NativeAppQueryResult(");
        sb.append("errorCode:");
        sb.append(this.f5552a);
        sb.append(", ");
        sb.append("description:");
        String str = this.f5553b;
        if (str == null) {
            sb.append("null");
        } else {
            sb.append(str);
        }
        sb.append(", ");
        sb.append("appQueryResultMap:");
        Map<String, b> map = this.f5554c;
        if (map == null) {
            sb.append("null");
        } else {
            sb.append(map);
        }
        sb.append(")");
        return sb.toString();
    }
}
