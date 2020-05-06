package com.miui.hybrid.accessory.sdk.a;

import com.miui.hybrid.accessory.a.f.a.b;
import com.miui.hybrid.accessory.a.f.a.c;
import com.miui.hybrid.accessory.a.f.a.f;
import com.miui.hybrid.accessory.a.f.b.h;
import com.miui.hybrid.accessory.a.f.b.i;
import com.miui.hybrid.accessory.a.f.b.j;
import java.io.Serializable;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import miui.cloud.CloudPushConstants;

public class e implements com.miui.hybrid.accessory.a.f.a<e, a>, Serializable, Cloneable {

    /* renamed from: d  reason: collision with root package name */
    public static final Map<a, b> f5559d;
    private static final j e = new j("Page");
    private static final com.miui.hybrid.accessory.a.f.b.b f = new com.miui.hybrid.accessory.a.f.b.b("downloadUrl", (byte) 11, 1);
    private static final com.miui.hybrid.accessory.a.f.b.b g = new com.miui.hybrid.accessory.a.f.b.b(CloudPushConstants.XML_NAME, (byte) 11, 2);
    private static final com.miui.hybrid.accessory.a.f.b.b h = new com.miui.hybrid.accessory.a.f.b.b("keywords", (byte) 14, 3);

    /* renamed from: a  reason: collision with root package name */
    public String f5560a;

    /* renamed from: b  reason: collision with root package name */
    public String f5561b;

    /* renamed from: c  reason: collision with root package name */
    public Set<String> f5562c;

    public enum a {
        DOWNLOAD_URL(1, "downloadUrl"),
        NAME(2, CloudPushConstants.XML_NAME),
        KEYWORDS(3, "keywords");
        

        /* renamed from: d  reason: collision with root package name */
        private static final Map<String, a> f5566d = null;
        private final short e;
        private final String f;

        static {
            f5566d = new HashMap();
            Iterator it = EnumSet.allOf(a.class).iterator();
            while (it.hasNext()) {
                a aVar = (a) it.next();
                f5566d.put(aVar.a(), aVar);
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
        enumMap.put(a.DOWNLOAD_URL, new b("downloadUrl", (byte) 1, new c((byte) 11)));
        enumMap.put(a.NAME, new b(CloudPushConstants.XML_NAME, (byte) 1, new c((byte) 11)));
        enumMap.put(a.KEYWORDS, new b("keywords", (byte) 1, new f((byte) 14, new c((byte) 11))));
        f5559d = Collections.unmodifiableMap(enumMap);
        b.a(e.class, f5559d);
    }

    public void a(com.miui.hybrid.accessory.a.f.b.e eVar) {
        eVar.a();
        while (true) {
            com.miui.hybrid.accessory.a.f.b.b c2 = eVar.c();
            byte b2 = c2.f5513b;
            if (b2 == 0) {
                eVar.b();
                d();
                return;
            }
            short s = c2.f5514c;
            if (s != 1) {
                if (s != 2) {
                    if (s == 3 && b2 == 14) {
                        i i = eVar.i();
                        this.f5562c = new HashSet(i.f5523b * 2);
                        for (int i2 = 0; i2 < i.f5523b; i2++) {
                            this.f5562c.add(eVar.q());
                        }
                        eVar.j();
                        eVar.d();
                    }
                } else if (b2 == 11) {
                    this.f5561b = eVar.q();
                    eVar.d();
                }
            } else if (b2 == 11) {
                this.f5560a = eVar.q();
                eVar.d();
            }
            h.a(eVar, b2);
            eVar.d();
        }
    }

    public boolean a() {
        return this.f5560a != null;
    }

    public boolean a(e eVar) {
        if (eVar == null) {
            return false;
        }
        boolean a2 = a();
        boolean a3 = eVar.a();
        if ((a2 || a3) && (!a2 || !a3 || !this.f5560a.equals(eVar.f5560a))) {
            return false;
        }
        boolean b2 = b();
        boolean b3 = eVar.b();
        if ((b2 || b3) && (!b2 || !b3 || !this.f5561b.equals(eVar.f5561b))) {
            return false;
        }
        boolean c2 = c();
        boolean c3 = eVar.c();
        if (c2 || c3) {
            return c2 && c3 && this.f5562c.equals(eVar.f5562c);
        }
        return true;
    }

    /* renamed from: b */
    public int compareTo(e eVar) {
        int a2;
        int a3;
        int a4;
        if (!e.class.equals(eVar.getClass())) {
            return e.class.getName().compareTo(eVar.getClass().getName());
        }
        int compareTo = Boolean.valueOf(a()).compareTo(Boolean.valueOf(eVar.a()));
        if (compareTo != 0) {
            return compareTo;
        }
        if (a() && (a4 = com.miui.hybrid.accessory.a.f.b.a(this.f5560a, eVar.f5560a)) != 0) {
            return a4;
        }
        int compareTo2 = Boolean.valueOf(b()).compareTo(Boolean.valueOf(eVar.b()));
        if (compareTo2 != 0) {
            return compareTo2;
        }
        if (b() && (a3 = com.miui.hybrid.accessory.a.f.b.a(this.f5561b, eVar.f5561b)) != 0) {
            return a3;
        }
        int compareTo3 = Boolean.valueOf(c()).compareTo(Boolean.valueOf(eVar.c()));
        if (compareTo3 != 0) {
            return compareTo3;
        }
        if (!c() || (a2 = com.miui.hybrid.accessory.a.f.b.a((Set) this.f5562c, (Set) eVar.f5562c)) == 0) {
            return 0;
        }
        return a2;
    }

    public boolean b() {
        return this.f5561b != null;
    }

    public boolean c() {
        return this.f5562c != null;
    }

    public void d() {
        if (this.f5560a == null) {
            throw new com.miui.hybrid.accessory.a.f.b.f("Required field 'downloadUrl' was not present! Struct: " + toString());
        } else if (this.f5561b == null) {
            throw new com.miui.hybrid.accessory.a.f.b.f("Required field 'name' was not present! Struct: " + toString());
        } else if (this.f5562c == null) {
            throw new com.miui.hybrid.accessory.a.f.b.f("Required field 'keywords' was not present! Struct: " + toString());
        }
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof e)) {
            return a((e) obj);
        }
        return false;
    }

    public int hashCode() {
        return 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("Page(");
        sb.append("downloadUrl:");
        String str = this.f5560a;
        if (str == null) {
            sb.append("null");
        } else {
            sb.append(str);
        }
        sb.append(", ");
        sb.append("name:");
        String str2 = this.f5561b;
        if (str2 == null) {
            sb.append("null");
        } else {
            sb.append(str2);
        }
        sb.append(", ");
        sb.append("keywords:");
        Set<String> set = this.f5562c;
        if (set == null) {
            sb.append("null");
        } else {
            sb.append(set);
        }
        sb.append(")");
        return sb.toString();
    }
}
