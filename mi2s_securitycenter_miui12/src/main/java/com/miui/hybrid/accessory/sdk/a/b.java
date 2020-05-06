package com.miui.hybrid.accessory.sdk.a;

import com.miui.hybrid.accessory.a.f.a.c;
import com.miui.hybrid.accessory.a.f.a.g;
import com.miui.hybrid.accessory.a.f.b.f;
import com.miui.hybrid.accessory.a.f.b.j;
import com.miui.maml.elements.AdvancedSlider;
import com.miui.networkassistant.provider.ProviderConstant;
import com.miui.networkassistant.ui.activity.NetworkDiagnosticsTipActivity;
import com.miui.permission.PermissionContract;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class b implements com.miui.hybrid.accessory.a.f.a<b, a>, Serializable, Cloneable {
    private static final com.miui.hybrid.accessory.a.f.b.b A = new com.miui.hybrid.accessory.a.f.b.b(AdvancedSlider.STATE, (byte) 12, 12);
    private static final com.miui.hybrid.accessory.a.f.b.b B = new com.miui.hybrid.accessory.a.f.b.b("setting", (byte) 12, 13);
    public static final Map<a, com.miui.hybrid.accessory.a.f.a.b> n;
    private static final j o = new j("AppQueryResultItem");
    private static final com.miui.hybrid.accessory.a.f.b.b p = new com.miui.hybrid.accessory.a.f.b.b("appInfo", (byte) 12, 1);
    private static final com.miui.hybrid.accessory.a.f.b.b q = new com.miui.hybrid.accessory.a.f.b.b("minMinaVersionCode", (byte) 8, 2);
    private static final com.miui.hybrid.accessory.a.f.b.b r = new com.miui.hybrid.accessory.a.f.b.b("template", (byte) 11, 3);
    private static final com.miui.hybrid.accessory.a.f.b.b s = new com.miui.hybrid.accessory.a.f.b.b(ProviderConstant.DataUsageNotiStatusColumns.COLUMN_ICON, (byte) 11, 4);
    private static final com.miui.hybrid.accessory.a.f.b.b t = new com.miui.hybrid.accessory.a.f.b.b(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME, (byte) 11, 5);
    private static final com.miui.hybrid.accessory.a.f.b.b u = new com.miui.hybrid.accessory.a.f.b.b(PermissionContract.Method.SavePermissionDescription.EXTRA_DESCRIPTION, (byte) 11, 6);
    private static final com.miui.hybrid.accessory.a.f.b.b v = new com.miui.hybrid.accessory.a.f.b.b("category", (byte) 11, 7);
    private static final com.miui.hybrid.accessory.a.f.b.b w = new com.miui.hybrid.accessory.a.f.b.b("buttonText", (byte) 11, 8);
    private static final com.miui.hybrid.accessory.a.f.b.b x = new com.miui.hybrid.accessory.a.f.b.b("size", (byte) 10, 9);
    private static final com.miui.hybrid.accessory.a.f.b.b y = new com.miui.hybrid.accessory.a.f.b.b("specialData", (byte) 11, 10);
    private static final com.miui.hybrid.accessory.a.f.b.b z = new com.miui.hybrid.accessory.a.f.b.b("pageName", (byte) 11, 11);
    private BitSet C = new BitSet(2);

    /* renamed from: a  reason: collision with root package name */
    public a f5539a;

    /* renamed from: b  reason: collision with root package name */
    public int f5540b;

    /* renamed from: c  reason: collision with root package name */
    public String f5541c;

    /* renamed from: d  reason: collision with root package name */
    public String f5542d;
    public String e;
    public String f;
    public String g;
    public String h;
    public long i;
    public ByteBuffer j;
    public String k;
    public f l;
    public g m;

    public enum a {
        APP_INFO(1, "appInfo"),
        MIN_MINA_VERSION_CODE(2, "minMinaVersionCode"),
        TEMPLATE(3, "template"),
        ICON(4, ProviderConstant.DataUsageNotiStatusColumns.COLUMN_ICON),
        TITLE(5, NetworkDiagnosticsTipActivity.TITLE_KEY_NAME),
        DESCRIPTION(6, PermissionContract.Method.SavePermissionDescription.EXTRA_DESCRIPTION),
        CATEGORY(7, "category"),
        BUTTON_TEXT(8, "buttonText"),
        SIZE(9, "size"),
        SPECIAL_DATA(10, "specialData"),
        PAGE_NAME(11, "pageName"),
        STATE(12, AdvancedSlider.STATE),
        SETTING(13, "setting");
        
        private static final Map<String, a> n = null;
        private final short o;
        private final String p;

        static {
            n = new HashMap();
            Iterator it = EnumSet.allOf(a.class).iterator();
            while (it.hasNext()) {
                a aVar = (a) it.next();
                n.put(aVar.a(), aVar);
            }
        }

        private a(short s, String str) {
            this.o = s;
            this.p = str;
        }

        public String a() {
            return this.p;
        }
    }

    static {
        EnumMap enumMap = new EnumMap(a.class);
        enumMap.put(a.APP_INFO, new com.miui.hybrid.accessory.a.f.a.b("appInfo", (byte) 1, new g((byte) 12, a.class)));
        enumMap.put(a.MIN_MINA_VERSION_CODE, new com.miui.hybrid.accessory.a.f.a.b("minMinaVersionCode", (byte) 1, new c((byte) 8)));
        enumMap.put(a.TEMPLATE, new com.miui.hybrid.accessory.a.f.a.b("template", (byte) 2, new c((byte) 11)));
        enumMap.put(a.ICON, new com.miui.hybrid.accessory.a.f.a.b(ProviderConstant.DataUsageNotiStatusColumns.COLUMN_ICON, (byte) 2, new c((byte) 11)));
        enumMap.put(a.TITLE, new com.miui.hybrid.accessory.a.f.a.b(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME, (byte) 2, new c((byte) 11)));
        enumMap.put(a.DESCRIPTION, new com.miui.hybrid.accessory.a.f.a.b(PermissionContract.Method.SavePermissionDescription.EXTRA_DESCRIPTION, (byte) 2, new c((byte) 11)));
        enumMap.put(a.CATEGORY, new com.miui.hybrid.accessory.a.f.a.b("category", (byte) 2, new c((byte) 11)));
        enumMap.put(a.BUTTON_TEXT, new com.miui.hybrid.accessory.a.f.a.b("buttonText", (byte) 2, new c((byte) 11)));
        enumMap.put(a.SIZE, new com.miui.hybrid.accessory.a.f.a.b("size", (byte) 2, new c((byte) 10)));
        enumMap.put(a.SPECIAL_DATA, new com.miui.hybrid.accessory.a.f.a.b("specialData", (byte) 2, new c((byte) 11)));
        enumMap.put(a.PAGE_NAME, new com.miui.hybrid.accessory.a.f.a.b("pageName", (byte) 2, new c((byte) 11)));
        enumMap.put(a.STATE, new com.miui.hybrid.accessory.a.f.a.b(AdvancedSlider.STATE, (byte) 2, new g((byte) 12, f.class)));
        enumMap.put(a.SETTING, new com.miui.hybrid.accessory.a.f.a.b("setting", (byte) 2, new g((byte) 12, g.class)));
        n = Collections.unmodifiableMap(enumMap);
        com.miui.hybrid.accessory.a.f.a.b.a(b.class, n);
    }

    public a a() {
        return this.f5539a;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void a(com.miui.hybrid.accessory.a.f.b.e r6) {
        /*
            r5 = this;
            r6.a()
        L_0x0003:
            com.miui.hybrid.accessory.a.f.b.b r0 = r6.c()
            byte r1 = r0.f5513b
            if (r1 != 0) goto L_0x0033
            r6.b()
            boolean r6 = r5.c()
            if (r6 == 0) goto L_0x0018
            r5.o()
            return
        L_0x0018:
            com.miui.hybrid.accessory.a.f.b.f r6 = new com.miui.hybrid.accessory.a.f.b.f
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "Required field 'minMinaVersionCode' was not found in serialized data! Struct: "
            r0.append(r1)
            java.lang.String r1 = r5.toString()
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r6.<init>(r0)
            throw r6
        L_0x0033:
            short r0 = r0.f5514c
            r2 = 1
            r3 = 12
            r4 = 11
            switch(r0) {
                case 1: goto L_0x00c7;
                case 2: goto L_0x00b9;
                case 3: goto L_0x00b0;
                case 4: goto L_0x00a7;
                case 5: goto L_0x009e;
                case 6: goto L_0x0095;
                case 7: goto L_0x008c;
                case 8: goto L_0x0083;
                case 9: goto L_0x0075;
                case 10: goto L_0x006c;
                case 11: goto L_0x0062;
                case 12: goto L_0x0052;
                case 13: goto L_0x0042;
                default: goto L_0x003d;
            }
        L_0x003d:
            com.miui.hybrid.accessory.a.f.b.h.a(r6, r1)
            goto L_0x00d5
        L_0x0042:
            if (r1 != r3) goto L_0x003d
            com.miui.hybrid.accessory.sdk.a.g r0 = new com.miui.hybrid.accessory.sdk.a.g
            r0.<init>()
            r5.m = r0
            com.miui.hybrid.accessory.sdk.a.g r0 = r5.m
            r0.a((com.miui.hybrid.accessory.a.f.b.e) r6)
            goto L_0x00d5
        L_0x0052:
            if (r1 != r3) goto L_0x003d
            com.miui.hybrid.accessory.sdk.a.f r0 = new com.miui.hybrid.accessory.sdk.a.f
            r0.<init>()
            r5.l = r0
            com.miui.hybrid.accessory.sdk.a.f r0 = r5.l
            r0.a((com.miui.hybrid.accessory.a.f.b.e) r6)
            goto L_0x00d5
        L_0x0062:
            if (r1 != r4) goto L_0x003d
            java.lang.String r0 = r6.q()
            r5.k = r0
            goto L_0x00d5
        L_0x006c:
            if (r1 != r4) goto L_0x003d
            java.nio.ByteBuffer r0 = r6.r()
            r5.j = r0
            goto L_0x00d5
        L_0x0075:
            r0 = 10
            if (r1 != r0) goto L_0x003d
            long r0 = r6.o()
            r5.i = r0
            r5.b((boolean) r2)
            goto L_0x00d5
        L_0x0083:
            if (r1 != r4) goto L_0x003d
            java.lang.String r0 = r6.q()
            r5.h = r0
            goto L_0x00d5
        L_0x008c:
            if (r1 != r4) goto L_0x003d
            java.lang.String r0 = r6.q()
            r5.g = r0
            goto L_0x00d5
        L_0x0095:
            if (r1 != r4) goto L_0x003d
            java.lang.String r0 = r6.q()
            r5.f = r0
            goto L_0x00d5
        L_0x009e:
            if (r1 != r4) goto L_0x003d
            java.lang.String r0 = r6.q()
            r5.e = r0
            goto L_0x00d5
        L_0x00a7:
            if (r1 != r4) goto L_0x003d
            java.lang.String r0 = r6.q()
            r5.f5542d = r0
            goto L_0x00d5
        L_0x00b0:
            if (r1 != r4) goto L_0x003d
            java.lang.String r0 = r6.q()
            r5.f5541c = r0
            goto L_0x00d5
        L_0x00b9:
            r0 = 8
            if (r1 != r0) goto L_0x003d
            int r0 = r6.n()
            r5.f5540b = r0
            r5.a((boolean) r2)
            goto L_0x00d5
        L_0x00c7:
            if (r1 != r3) goto L_0x003d
            com.miui.hybrid.accessory.sdk.a.a r0 = new com.miui.hybrid.accessory.sdk.a.a
            r0.<init>()
            r5.f5539a = r0
            com.miui.hybrid.accessory.sdk.a.a r0 = r5.f5539a
            r0.a((com.miui.hybrid.accessory.a.f.b.e) r6)
        L_0x00d5:
            r6.d()
            goto L_0x0003
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.hybrid.accessory.sdk.a.b.a(com.miui.hybrid.accessory.a.f.b.e):void");
    }

    public void a(boolean z2) {
        this.C.set(0, z2);
    }

    public boolean a(b bVar) {
        if (bVar == null) {
            return false;
        }
        boolean b2 = b();
        boolean b3 = bVar.b();
        if (((b2 || b3) && (!b2 || !b3 || !this.f5539a.a(bVar.f5539a))) || this.f5540b != bVar.f5540b) {
            return false;
        }
        boolean d2 = d();
        boolean d3 = bVar.d();
        if ((d2 || d3) && (!d2 || !d3 || !this.f5541c.equals(bVar.f5541c))) {
            return false;
        }
        boolean e2 = e();
        boolean e3 = bVar.e();
        if ((e2 || e3) && (!e2 || !e3 || !this.f5542d.equals(bVar.f5542d))) {
            return false;
        }
        boolean f2 = f();
        boolean f3 = bVar.f();
        if ((f2 || f3) && (!f2 || !f3 || !this.e.equals(bVar.e))) {
            return false;
        }
        boolean g2 = g();
        boolean g3 = bVar.g();
        if ((g2 || g3) && (!g2 || !g3 || !this.f.equals(bVar.f))) {
            return false;
        }
        boolean h2 = h();
        boolean h3 = bVar.h();
        if ((h2 || h3) && (!h2 || !h3 || !this.g.equals(bVar.g))) {
            return false;
        }
        boolean i2 = i();
        boolean i3 = bVar.i();
        if ((i2 || i3) && (!i2 || !i3 || !this.h.equals(bVar.h))) {
            return false;
        }
        boolean j2 = j();
        boolean j3 = bVar.j();
        if ((j2 || j3) && (!j2 || !j3 || this.i != bVar.i)) {
            return false;
        }
        boolean k2 = k();
        boolean k3 = bVar.k();
        if ((k2 || k3) && (!k2 || !k3 || !this.j.equals(bVar.j))) {
            return false;
        }
        boolean l2 = l();
        boolean l3 = bVar.l();
        if ((l2 || l3) && (!l2 || !l3 || !this.k.equals(bVar.k))) {
            return false;
        }
        boolean m2 = m();
        boolean m3 = bVar.m();
        if ((m2 || m3) && (!m2 || !m3 || !this.l.a(bVar.l))) {
            return false;
        }
        boolean n2 = n();
        boolean n3 = bVar.n();
        if (n2 || n3) {
            return n2 && n3 && this.m.a(bVar.m);
        }
        return true;
    }

    /* renamed from: b */
    public int compareTo(b bVar) {
        int a2;
        int a3;
        int a4;
        int a5;
        int a6;
        int a7;
        int a8;
        int a9;
        int a10;
        int a11;
        int a12;
        int a13;
        int a14;
        if (!b.class.equals(bVar.getClass())) {
            return b.class.getName().compareTo(bVar.getClass().getName());
        }
        int compareTo = Boolean.valueOf(b()).compareTo(Boolean.valueOf(bVar.b()));
        if (compareTo != 0) {
            return compareTo;
        }
        if (b() && (a14 = com.miui.hybrid.accessory.a.f.b.a((Comparable) this.f5539a, (Comparable) bVar.f5539a)) != 0) {
            return a14;
        }
        int compareTo2 = Boolean.valueOf(c()).compareTo(Boolean.valueOf(bVar.c()));
        if (compareTo2 != 0) {
            return compareTo2;
        }
        if (c() && (a13 = com.miui.hybrid.accessory.a.f.b.a(this.f5540b, bVar.f5540b)) != 0) {
            return a13;
        }
        int compareTo3 = Boolean.valueOf(d()).compareTo(Boolean.valueOf(bVar.d()));
        if (compareTo3 != 0) {
            return compareTo3;
        }
        if (d() && (a12 = com.miui.hybrid.accessory.a.f.b.a(this.f5541c, bVar.f5541c)) != 0) {
            return a12;
        }
        int compareTo4 = Boolean.valueOf(e()).compareTo(Boolean.valueOf(bVar.e()));
        if (compareTo4 != 0) {
            return compareTo4;
        }
        if (e() && (a11 = com.miui.hybrid.accessory.a.f.b.a(this.f5542d, bVar.f5542d)) != 0) {
            return a11;
        }
        int compareTo5 = Boolean.valueOf(f()).compareTo(Boolean.valueOf(bVar.f()));
        if (compareTo5 != 0) {
            return compareTo5;
        }
        if (f() && (a10 = com.miui.hybrid.accessory.a.f.b.a(this.e, bVar.e)) != 0) {
            return a10;
        }
        int compareTo6 = Boolean.valueOf(g()).compareTo(Boolean.valueOf(bVar.g()));
        if (compareTo6 != 0) {
            return compareTo6;
        }
        if (g() && (a9 = com.miui.hybrid.accessory.a.f.b.a(this.f, bVar.f)) != 0) {
            return a9;
        }
        int compareTo7 = Boolean.valueOf(h()).compareTo(Boolean.valueOf(bVar.h()));
        if (compareTo7 != 0) {
            return compareTo7;
        }
        if (h() && (a8 = com.miui.hybrid.accessory.a.f.b.a(this.g, bVar.g)) != 0) {
            return a8;
        }
        int compareTo8 = Boolean.valueOf(i()).compareTo(Boolean.valueOf(bVar.i()));
        if (compareTo8 != 0) {
            return compareTo8;
        }
        if (i() && (a7 = com.miui.hybrid.accessory.a.f.b.a(this.h, bVar.h)) != 0) {
            return a7;
        }
        int compareTo9 = Boolean.valueOf(j()).compareTo(Boolean.valueOf(bVar.j()));
        if (compareTo9 != 0) {
            return compareTo9;
        }
        if (j() && (a6 = com.miui.hybrid.accessory.a.f.b.a(this.i, bVar.i)) != 0) {
            return a6;
        }
        int compareTo10 = Boolean.valueOf(k()).compareTo(Boolean.valueOf(bVar.k()));
        if (compareTo10 != 0) {
            return compareTo10;
        }
        if (k() && (a5 = com.miui.hybrid.accessory.a.f.b.a((Comparable) this.j, (Comparable) bVar.j)) != 0) {
            return a5;
        }
        int compareTo11 = Boolean.valueOf(l()).compareTo(Boolean.valueOf(bVar.l()));
        if (compareTo11 != 0) {
            return compareTo11;
        }
        if (l() && (a4 = com.miui.hybrid.accessory.a.f.b.a(this.k, bVar.k)) != 0) {
            return a4;
        }
        int compareTo12 = Boolean.valueOf(m()).compareTo(Boolean.valueOf(bVar.m()));
        if (compareTo12 != 0) {
            return compareTo12;
        }
        if (m() && (a3 = com.miui.hybrid.accessory.a.f.b.a((Comparable) this.l, (Comparable) bVar.l)) != 0) {
            return a3;
        }
        int compareTo13 = Boolean.valueOf(n()).compareTo(Boolean.valueOf(bVar.n()));
        if (compareTo13 != 0) {
            return compareTo13;
        }
        if (!n() || (a2 = com.miui.hybrid.accessory.a.f.b.a((Comparable) this.m, (Comparable) bVar.m)) == 0) {
            return 0;
        }
        return a2;
    }

    public void b(boolean z2) {
        this.C.set(1, z2);
    }

    public boolean b() {
        return this.f5539a != null;
    }

    public boolean c() {
        return this.C.get(0);
    }

    public boolean d() {
        return this.f5541c != null;
    }

    public boolean e() {
        return this.f5542d != null;
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof b)) {
            return a((b) obj);
        }
        return false;
    }

    public boolean f() {
        return this.e != null;
    }

    public boolean g() {
        return this.f != null;
    }

    public boolean h() {
        return this.g != null;
    }

    public int hashCode() {
        return 0;
    }

    public boolean i() {
        return this.h != null;
    }

    public boolean j() {
        return this.C.get(1);
    }

    public boolean k() {
        return this.j != null;
    }

    public boolean l() {
        return this.k != null;
    }

    public boolean m() {
        return this.l != null;
    }

    public boolean n() {
        return this.m != null;
    }

    public void o() {
        if (this.f5539a == null) {
            throw new f("Required field 'appInfo' was not present! Struct: " + toString());
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("AppQueryResultItem(");
        sb.append("appInfo:");
        a aVar = this.f5539a;
        if (aVar == null) {
            sb.append("null");
        } else {
            sb.append(aVar);
        }
        sb.append(", ");
        sb.append("minMinaVersionCode:");
        sb.append(this.f5540b);
        if (d()) {
            sb.append(", ");
            sb.append("template:");
            String str = this.f5541c;
            if (str == null) {
                sb.append("null");
            } else {
                sb.append(str);
            }
        }
        if (e()) {
            sb.append(", ");
            sb.append("icon:");
            String str2 = this.f5542d;
            if (str2 == null) {
                sb.append("null");
            } else {
                sb.append(str2);
            }
        }
        if (f()) {
            sb.append(", ");
            sb.append("title:");
            String str3 = this.e;
            if (str3 == null) {
                sb.append("null");
            } else {
                sb.append(str3);
            }
        }
        if (g()) {
            sb.append(", ");
            sb.append("description:");
            String str4 = this.f;
            if (str4 == null) {
                sb.append("null");
            } else {
                sb.append(str4);
            }
        }
        if (h()) {
            sb.append(", ");
            sb.append("category:");
            String str5 = this.g;
            if (str5 == null) {
                sb.append("null");
            } else {
                sb.append(str5);
            }
        }
        if (i()) {
            sb.append(", ");
            sb.append("buttonText:");
            String str6 = this.h;
            if (str6 == null) {
                sb.append("null");
            } else {
                sb.append(str6);
            }
        }
        if (j()) {
            sb.append(", ");
            sb.append("size:");
            sb.append(this.i);
        }
        if (k()) {
            sb.append(", ");
            sb.append("specialData:");
            ByteBuffer byteBuffer = this.j;
            if (byteBuffer == null) {
                sb.append("null");
            } else {
                com.miui.hybrid.accessory.a.f.b.a(byteBuffer, sb);
            }
        }
        if (l()) {
            sb.append(", ");
            sb.append("pageName:");
            String str7 = this.k;
            if (str7 == null) {
                sb.append("null");
            } else {
                sb.append(str7);
            }
        }
        if (m()) {
            sb.append(", ");
            sb.append("state:");
            f fVar = this.l;
            if (fVar == null) {
                sb.append("null");
            } else {
                sb.append(fVar);
            }
        }
        if (n()) {
            sb.append(", ");
            sb.append("setting:");
            g gVar = this.m;
            if (gVar == null) {
                sb.append("null");
            } else {
                sb.append(gVar);
            }
        }
        sb.append(")");
        return sb.toString();
    }
}
