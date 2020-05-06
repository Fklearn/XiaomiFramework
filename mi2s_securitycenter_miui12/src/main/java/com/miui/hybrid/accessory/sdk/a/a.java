package com.miui.hybrid.accessory.sdk.a;

import com.miui.hybrid.accessory.a.f.a.b;
import com.miui.hybrid.accessory.a.f.a.c;
import com.miui.hybrid.accessory.a.f.a.d;
import com.miui.hybrid.accessory.a.f.a.f;
import com.miui.hybrid.accessory.a.f.a.g;
import com.miui.hybrid.accessory.a.f.b.j;
import com.miui.networkassistant.provider.ProviderConstant;
import com.miui.permission.PermissionContract;
import java.io.Serializable;
import java.util.BitSet;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class a implements com.miui.hybrid.accessory.a.f.a<a, C0053a>, Serializable, Cloneable {
    public static final Map<C0053a, b> E;
    private static final j F = new j("AppInfo");
    private static final com.miui.hybrid.accessory.a.f.b.b G = new com.miui.hybrid.accessory.a.f.b.b("appId", (byte) 10, 1);
    private static final com.miui.hybrid.accessory.a.f.b.b H = new com.miui.hybrid.accessory.a.f.b.b("appName", (byte) 11, 2);
    private static final com.miui.hybrid.accessory.a.f.b.b I = new com.miui.hybrid.accessory.a.f.b.b("appSecret", (byte) 11, 3);
    private static final com.miui.hybrid.accessory.a.f.b.b J = new com.miui.hybrid.accessory.a.f.b.b("appKey", (byte) 10, 4);
    private static final com.miui.hybrid.accessory.a.f.b.b K = new com.miui.hybrid.accessory.a.f.b.b("appVersionCode", (byte) 8, 5);
    private static final com.miui.hybrid.accessory.a.f.b.b L = new com.miui.hybrid.accessory.a.f.b.b("sdkVersionCode", (byte) 8, 6);
    private static final com.miui.hybrid.accessory.a.f.b.b M = new com.miui.hybrid.accessory.a.f.b.b("packageName", (byte) 11, 7);
    private static final com.miui.hybrid.accessory.a.f.b.b N = new com.miui.hybrid.accessory.a.f.b.b(ProviderConstant.DataUsageNotiStatusColumns.COLUMN_ICON, (byte) 11, 8);
    private static final com.miui.hybrid.accessory.a.f.b.b O = new com.miui.hybrid.accessory.a.f.b.b(PermissionContract.Method.SavePermissionDescription.EXTRA_DESCRIPTION, (byte) 11, 9);
    private static final com.miui.hybrid.accessory.a.f.b.b P = new com.miui.hybrid.accessory.a.f.b.b("downloadUrl", (byte) 11, 10);
    private static final com.miui.hybrid.accessory.a.f.b.b Q = new com.miui.hybrid.accessory.a.f.b.b("status", (byte) 8, 11);
    private static final com.miui.hybrid.accessory.a.f.b.b R = new com.miui.hybrid.accessory.a.f.b.b("categories", (byte) 14, 12);
    private static final com.miui.hybrid.accessory.a.f.b.b S = new com.miui.hybrid.accessory.a.f.b.b("keywords", (byte) 14, 13);
    private static final com.miui.hybrid.accessory.a.f.b.b T = new com.miui.hybrid.accessory.a.f.b.b("createTime", (byte) 10, 14);
    private static final com.miui.hybrid.accessory.a.f.b.b U = new com.miui.hybrid.accessory.a.f.b.b("lastUpdateTime", (byte) 10, 15);
    private static final com.miui.hybrid.accessory.a.f.b.b V = new com.miui.hybrid.accessory.a.f.b.b("popularity", (byte) 4, 16);
    private static final com.miui.hybrid.accessory.a.f.b.b W = new com.miui.hybrid.accessory.a.f.b.b("developerId", (byte) 10, 17);
    private static final com.miui.hybrid.accessory.a.f.b.b X = new com.miui.hybrid.accessory.a.f.b.b("company", (byte) 11, 18);
    private static final com.miui.hybrid.accessory.a.f.b.b Y = new com.miui.hybrid.accessory.a.f.b.b("models", (byte) 14, 19);
    private static final com.miui.hybrid.accessory.a.f.b.b Z = new com.miui.hybrid.accessory.a.f.b.b("locales", (byte) 14, 20);
    private static final com.miui.hybrid.accessory.a.f.b.b aa = new com.miui.hybrid.accessory.a.f.b.b("regions", (byte) 14, 21);
    private static final com.miui.hybrid.accessory.a.f.b.b ab = new com.miui.hybrid.accessory.a.f.b.b("defaultPageName", (byte) 11, 22);
    private static final com.miui.hybrid.accessory.a.f.b.b ac = new com.miui.hybrid.accessory.a.f.b.b("pages", (byte) 15, 23);
    private static final com.miui.hybrid.accessory.a.f.b.b ad = new com.miui.hybrid.accessory.a.f.b.b("nativePackageNames", (byte) 15, 24);
    private static final com.miui.hybrid.accessory.a.f.b.b ae = new com.miui.hybrid.accessory.a.f.b.b("size", (byte) 10, 25);
    private static final com.miui.hybrid.accessory.a.f.b.b af = new com.miui.hybrid.accessory.a.f.b.b("domains", (byte) 14, 26);
    private static final com.miui.hybrid.accessory.a.f.b.b ag = new com.miui.hybrid.accessory.a.f.b.b("changeLog", (byte) 11, 28);
    private static final com.miui.hybrid.accessory.a.f.b.b ah = new com.miui.hybrid.accessory.a.f.b.b("introduction", (byte) 11, 29);
    private static final com.miui.hybrid.accessory.a.f.b.b ai = new com.miui.hybrid.accessory.a.f.b.b("minMinaVersionCode", (byte) 8, 30);
    private static final com.miui.hybrid.accessory.a.f.b.b aj = new com.miui.hybrid.accessory.a.f.b.b("appVersionName", (byte) 11, 33);
    public String A;
    public String B;
    public int C;
    public String D;

    /* renamed from: a  reason: collision with root package name */
    public long f5531a;
    private BitSet ak = new BitSet(10);

    /* renamed from: b  reason: collision with root package name */
    public String f5532b;

    /* renamed from: c  reason: collision with root package name */
    public String f5533c;

    /* renamed from: d  reason: collision with root package name */
    public long f5534d;
    public int e;
    public int f;
    public String g;
    public String h;
    public String i;
    public String j;
    public c k = c.Beta;
    public Set<String> l;
    public Set<String> m;
    public long n;
    public long o;
    public double p = 0.0d;
    public long q;
    public String r;
    public Set<String> s;
    public Set<String> t;
    public Set<String> u;
    public String v;
    public List<e> w;
    public List<String> x;
    public long y;
    public Set<String> z;

    /* renamed from: com.miui.hybrid.accessory.sdk.a.a$a  reason: collision with other inner class name */
    public enum C0053a {
        APP_ID(1, "appId"),
        APP_NAME(2, "appName"),
        APP_SECRET(3, "appSecret"),
        APP_KEY(4, "appKey"),
        APP_VERSION_CODE(5, "appVersionCode"),
        SDK_VERSION_CODE(6, "sdkVersionCode"),
        PACKAGE_NAME(7, "packageName"),
        ICON(8, ProviderConstant.DataUsageNotiStatusColumns.COLUMN_ICON),
        DESCRIPTION(9, PermissionContract.Method.SavePermissionDescription.EXTRA_DESCRIPTION),
        DOWNLOAD_URL(10, "downloadUrl"),
        STATUS(11, "status"),
        CATEGORIES(12, "categories"),
        KEYWORDS(13, "keywords"),
        CREATE_TIME(14, "createTime"),
        LAST_UPDATE_TIME(15, "lastUpdateTime"),
        POPULARITY(16, "popularity"),
        DEVELOPER_ID(17, "developerId"),
        COMPANY(18, "company"),
        MODELS(19, "models"),
        LOCALES(20, "locales"),
        REGIONS(21, "regions"),
        DEFAULT_PAGE_NAME(22, "defaultPageName"),
        PAGES(23, "pages"),
        NATIVE_PACKAGE_NAMES(24, "nativePackageNames"),
        SIZE(25, "size"),
        DOMAINS(26, "domains"),
        CHANGE_LOG(28, "changeLog"),
        INTRODUCTION(29, "introduction"),
        MIN_MINA_VERSION_CODE(30, "minMinaVersionCode"),
        APP_VERSION_NAME(33, "appVersionName");
        
        private static final Map<String, C0053a> E = null;
        private final short F;
        private final String G;

        static {
            E = new HashMap();
            Iterator it = EnumSet.allOf(C0053a.class).iterator();
            while (it.hasNext()) {
                C0053a aVar = (C0053a) it.next();
                E.put(aVar.a(), aVar);
            }
        }

        private C0053a(short s, String str) {
            this.F = s;
            this.G = str;
        }

        public String a() {
            return this.G;
        }
    }

    static {
        EnumMap enumMap = new EnumMap(C0053a.class);
        enumMap.put(C0053a.APP_ID, new b("appId", (byte) 1, new c((byte) 10)));
        enumMap.put(C0053a.APP_NAME, new b("appName", (byte) 1, new c((byte) 11)));
        enumMap.put(C0053a.APP_SECRET, new b("appSecret", (byte) 1, new c((byte) 11)));
        enumMap.put(C0053a.APP_KEY, new b("appKey", (byte) 1, new c((byte) 10)));
        enumMap.put(C0053a.APP_VERSION_CODE, new b("appVersionCode", (byte) 1, new c((byte) 8)));
        enumMap.put(C0053a.SDK_VERSION_CODE, new b("sdkVersionCode", (byte) 1, new c((byte) 8)));
        enumMap.put(C0053a.PACKAGE_NAME, new b("packageName", (byte) 1, new c((byte) 11)));
        enumMap.put(C0053a.ICON, new b(ProviderConstant.DataUsageNotiStatusColumns.COLUMN_ICON, (byte) 1, new c((byte) 11)));
        enumMap.put(C0053a.DESCRIPTION, new b(PermissionContract.Method.SavePermissionDescription.EXTRA_DESCRIPTION, (byte) 1, new c((byte) 11)));
        enumMap.put(C0053a.DOWNLOAD_URL, new b("downloadUrl", (byte) 1, new c((byte) 11)));
        enumMap.put(C0053a.STATUS, new b("status", (byte) 1, new com.miui.hybrid.accessory.a.f.a.a((byte) 16, c.class)));
        enumMap.put(C0053a.CATEGORIES, new b("categories", (byte) 1, new f((byte) 14, new c((byte) 11))));
        enumMap.put(C0053a.KEYWORDS, new b("keywords", (byte) 1, new f((byte) 14, new c((byte) 11))));
        enumMap.put(C0053a.CREATE_TIME, new b("createTime", (byte) 1, new c((byte) 10)));
        enumMap.put(C0053a.LAST_UPDATE_TIME, new b("lastUpdateTime", (byte) 1, new c((byte) 10)));
        enumMap.put(C0053a.POPULARITY, new b("popularity", (byte) 1, new c((byte) 4)));
        enumMap.put(C0053a.DEVELOPER_ID, new b("developerId", (byte) 2, new c((byte) 10)));
        enumMap.put(C0053a.COMPANY, new b("company", (byte) 2, new c((byte) 11)));
        enumMap.put(C0053a.MODELS, new b("models", (byte) 2, new f((byte) 14, new c((byte) 11))));
        enumMap.put(C0053a.LOCALES, new b("locales", (byte) 2, new f((byte) 14, new c((byte) 11))));
        enumMap.put(C0053a.REGIONS, new b("regions", (byte) 2, new f((byte) 14, new c((byte) 11))));
        enumMap.put(C0053a.DEFAULT_PAGE_NAME, new b("defaultPageName", (byte) 2, new c((byte) 11)));
        enumMap.put(C0053a.PAGES, new b("pages", (byte) 2, new d((byte) 15, new g((byte) 12, e.class))));
        enumMap.put(C0053a.NATIVE_PACKAGE_NAMES, new b("nativePackageNames", (byte) 2, new d((byte) 15, new c((byte) 11))));
        enumMap.put(C0053a.SIZE, new b("size", (byte) 2, new c((byte) 10)));
        enumMap.put(C0053a.DOMAINS, new b("domains", (byte) 2, new f((byte) 14, new c((byte) 11))));
        enumMap.put(C0053a.CHANGE_LOG, new b("changeLog", (byte) 2, new c((byte) 11)));
        enumMap.put(C0053a.INTRODUCTION, new b("introduction", (byte) 2, new c((byte) 11)));
        enumMap.put(C0053a.MIN_MINA_VERSION_CODE, new b("minMinaVersionCode", (byte) 2, new c((byte) 8)));
        enumMap.put(C0053a.APP_VERSION_NAME, new b("appVersionName", (byte) 2, new c((byte) 11)));
        E = Collections.unmodifiableMap(enumMap);
        b.a(a.class, E);
    }

    public boolean A() {
        return this.x != null;
    }

    public long B() {
        return this.y;
    }

    public boolean C() {
        return this.ak.get(8);
    }

    public boolean D() {
        return this.z != null;
    }

    public boolean E() {
        return this.A != null;
    }

    public boolean F() {
        return this.B != null;
    }

    public boolean G() {
        return this.ak.get(9);
    }

    public boolean H() {
        return this.D != null;
    }

    public void I() {
        if (this.f5532b == null) {
            throw new com.miui.hybrid.accessory.a.f.b.f("Required field 'appName' was not present! Struct: " + toString());
        } else if (this.f5533c == null) {
            throw new com.miui.hybrid.accessory.a.f.b.f("Required field 'appSecret' was not present! Struct: " + toString());
        } else if (this.g == null) {
            throw new com.miui.hybrid.accessory.a.f.b.f("Required field 'packageName' was not present! Struct: " + toString());
        } else if (this.h == null) {
            throw new com.miui.hybrid.accessory.a.f.b.f("Required field 'icon' was not present! Struct: " + toString());
        } else if (this.i == null) {
            throw new com.miui.hybrid.accessory.a.f.b.f("Required field 'description' was not present! Struct: " + toString());
        } else if (this.j == null) {
            throw new com.miui.hybrid.accessory.a.f.b.f("Required field 'downloadUrl' was not present! Struct: " + toString());
        } else if (this.k == null) {
            throw new com.miui.hybrid.accessory.a.f.b.f("Required field 'status' was not present! Struct: " + toString());
        } else if (this.l == null) {
            throw new com.miui.hybrid.accessory.a.f.b.f("Required field 'categories' was not present! Struct: " + toString());
        } else if (this.m == null) {
            throw new com.miui.hybrid.accessory.a.f.b.f("Required field 'keywords' was not present! Struct: " + toString());
        }
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:100:0x029d, code lost:
        r10.j();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x01aa, code lost:
        r10.h();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void a(com.miui.hybrid.accessory.a.f.b.e r10) {
        /*
            r9 = this;
            r10.a()
        L_0x0003:
            com.miui.hybrid.accessory.a.f.b.b r0 = r10.c()
            byte r1 = r0.f5513b
            if (r1 != 0) goto L_0x00f9
            r10.b()
            boolean r10 = r9.a()
            if (r10 == 0) goto L_0x00de
            boolean r10 = r9.e()
            if (r10 == 0) goto L_0x00c3
            boolean r10 = r9.f()
            if (r10 == 0) goto L_0x00a8
            boolean r10 = r9.g()
            if (r10 == 0) goto L_0x008d
            boolean r10 = r9.q()
            if (r10 == 0) goto L_0x0072
            boolean r10 = r9.r()
            if (r10 == 0) goto L_0x0057
            boolean r10 = r9.s()
            if (r10 == 0) goto L_0x003c
            r9.I()
            return
        L_0x003c:
            com.miui.hybrid.accessory.a.f.b.f r10 = new com.miui.hybrid.accessory.a.f.b.f
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "Required field 'popularity' was not found in serialized data! Struct: "
            r0.append(r1)
            java.lang.String r1 = r9.toString()
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r10.<init>(r0)
            throw r10
        L_0x0057:
            com.miui.hybrid.accessory.a.f.b.f r10 = new com.miui.hybrid.accessory.a.f.b.f
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "Required field 'lastUpdateTime' was not found in serialized data! Struct: "
            r0.append(r1)
            java.lang.String r1 = r9.toString()
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r10.<init>(r0)
            throw r10
        L_0x0072:
            com.miui.hybrid.accessory.a.f.b.f r10 = new com.miui.hybrid.accessory.a.f.b.f
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "Required field 'createTime' was not found in serialized data! Struct: "
            r0.append(r1)
            java.lang.String r1 = r9.toString()
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r10.<init>(r0)
            throw r10
        L_0x008d:
            com.miui.hybrid.accessory.a.f.b.f r10 = new com.miui.hybrid.accessory.a.f.b.f
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "Required field 'sdkVersionCode' was not found in serialized data! Struct: "
            r0.append(r1)
            java.lang.String r1 = r9.toString()
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r10.<init>(r0)
            throw r10
        L_0x00a8:
            com.miui.hybrid.accessory.a.f.b.f r10 = new com.miui.hybrid.accessory.a.f.b.f
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "Required field 'appVersionCode' was not found in serialized data! Struct: "
            r0.append(r1)
            java.lang.String r1 = r9.toString()
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r10.<init>(r0)
            throw r10
        L_0x00c3:
            com.miui.hybrid.accessory.a.f.b.f r10 = new com.miui.hybrid.accessory.a.f.b.f
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "Required field 'appKey' was not found in serialized data! Struct: "
            r0.append(r1)
            java.lang.String r1 = r9.toString()
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r10.<init>(r0)
            throw r10
        L_0x00de:
            com.miui.hybrid.accessory.a.f.b.f r10 = new com.miui.hybrid.accessory.a.f.b.f
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "Required field 'appId' was not found in serialized data! Struct: "
            r0.append(r1)
            java.lang.String r1 = r9.toString()
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r10.<init>(r0)
            throw r10
        L_0x00f9:
            short r0 = r0.f5514c
            r2 = 15
            r3 = 8
            r4 = 14
            r5 = 10
            r6 = 0
            r7 = 11
            r8 = 1
            switch(r0) {
                case 1: goto L_0x030a;
                case 2: goto L_0x0301;
                case 3: goto L_0x02f8;
                case 4: goto L_0x02ec;
                case 5: goto L_0x02e0;
                case 6: goto L_0x02d4;
                case 7: goto L_0x02cb;
                case 8: goto L_0x02c2;
                case 9: goto L_0x02b9;
                case 10: goto L_0x02b0;
                case 11: goto L_0x02a2;
                case 12: goto L_0x027c;
                case 13: goto L_0x025b;
                case 14: goto L_0x024e;
                case 15: goto L_0x0241;
                case 16: goto L_0x0233;
                case 17: goto L_0x0226;
                case 18: goto L_0x021c;
                case 19: goto L_0x01fb;
                case 20: goto L_0x01da;
                case 21: goto L_0x01b9;
                case 22: goto L_0x01af;
                case 23: goto L_0x0187;
                case 24: goto L_0x0168;
                case 25: goto L_0x015b;
                case 26: goto L_0x013a;
                case 27: goto L_0x010a;
                case 28: goto L_0x0130;
                case 29: goto L_0x0126;
                case 30: goto L_0x0119;
                case 31: goto L_0x010a;
                case 32: goto L_0x010a;
                case 33: goto L_0x010f;
                default: goto L_0x010a;
            }
        L_0x010a:
            com.miui.hybrid.accessory.a.f.b.h.a(r10, r1)
            goto L_0x0315
        L_0x010f:
            if (r1 != r7) goto L_0x010a
            java.lang.String r0 = r10.q()
            r9.D = r0
            goto L_0x0315
        L_0x0119:
            if (r1 != r3) goto L_0x010a
            int r0 = r10.n()
            r9.C = r0
            r9.j(r8)
            goto L_0x0315
        L_0x0126:
            if (r1 != r7) goto L_0x010a
            java.lang.String r0 = r10.q()
            r9.B = r0
            goto L_0x0315
        L_0x0130:
            if (r1 != r7) goto L_0x010a
            java.lang.String r0 = r10.q()
            r9.A = r0
            goto L_0x0315
        L_0x013a:
            if (r1 != r4) goto L_0x010a
            com.miui.hybrid.accessory.a.f.b.i r0 = r10.i()
            java.util.HashSet r1 = new java.util.HashSet
            int r2 = r0.f5523b
            int r2 = r2 * 2
            r1.<init>(r2)
            r9.z = r1
        L_0x014b:
            int r1 = r0.f5523b
            if (r6 >= r1) goto L_0x029d
            java.lang.String r1 = r10.q()
            java.util.Set<java.lang.String> r2 = r9.z
            r2.add(r1)
            int r6 = r6 + 1
            goto L_0x014b
        L_0x015b:
            if (r1 != r5) goto L_0x010a
            long r0 = r10.o()
            r9.y = r0
            r9.i(r8)
            goto L_0x0315
        L_0x0168:
            if (r1 != r2) goto L_0x010a
            com.miui.hybrid.accessory.a.f.b.c r0 = r10.g()
            java.util.ArrayList r1 = new java.util.ArrayList
            int r2 = r0.f5516b
            r1.<init>(r2)
            r9.x = r1
        L_0x0177:
            int r1 = r0.f5516b
            if (r6 >= r1) goto L_0x01aa
            java.lang.String r1 = r10.q()
            java.util.List<java.lang.String> r2 = r9.x
            r2.add(r1)
            int r6 = r6 + 1
            goto L_0x0177
        L_0x0187:
            if (r1 != r2) goto L_0x010a
            com.miui.hybrid.accessory.a.f.b.c r0 = r10.g()
            java.util.ArrayList r1 = new java.util.ArrayList
            int r2 = r0.f5516b
            r1.<init>(r2)
            r9.w = r1
        L_0x0196:
            int r1 = r0.f5516b
            if (r6 >= r1) goto L_0x01aa
            com.miui.hybrid.accessory.sdk.a.e r1 = new com.miui.hybrid.accessory.sdk.a.e
            r1.<init>()
            r1.a((com.miui.hybrid.accessory.a.f.b.e) r10)
            java.util.List<com.miui.hybrid.accessory.sdk.a.e> r2 = r9.w
            r2.add(r1)
            int r6 = r6 + 1
            goto L_0x0196
        L_0x01aa:
            r10.h()
            goto L_0x0315
        L_0x01af:
            if (r1 != r7) goto L_0x010a
            java.lang.String r0 = r10.q()
            r9.v = r0
            goto L_0x0315
        L_0x01b9:
            if (r1 != r4) goto L_0x010a
            com.miui.hybrid.accessory.a.f.b.i r0 = r10.i()
            java.util.HashSet r1 = new java.util.HashSet
            int r2 = r0.f5523b
            int r2 = r2 * 2
            r1.<init>(r2)
            r9.u = r1
        L_0x01ca:
            int r1 = r0.f5523b
            if (r6 >= r1) goto L_0x029d
            java.lang.String r1 = r10.q()
            java.util.Set<java.lang.String> r2 = r9.u
            r2.add(r1)
            int r6 = r6 + 1
            goto L_0x01ca
        L_0x01da:
            if (r1 != r4) goto L_0x010a
            com.miui.hybrid.accessory.a.f.b.i r0 = r10.i()
            java.util.HashSet r1 = new java.util.HashSet
            int r2 = r0.f5523b
            int r2 = r2 * 2
            r1.<init>(r2)
            r9.t = r1
        L_0x01eb:
            int r1 = r0.f5523b
            if (r6 >= r1) goto L_0x029d
            java.lang.String r1 = r10.q()
            java.util.Set<java.lang.String> r2 = r9.t
            r2.add(r1)
            int r6 = r6 + 1
            goto L_0x01eb
        L_0x01fb:
            if (r1 != r4) goto L_0x010a
            com.miui.hybrid.accessory.a.f.b.i r0 = r10.i()
            java.util.HashSet r1 = new java.util.HashSet
            int r2 = r0.f5523b
            int r2 = r2 * 2
            r1.<init>(r2)
            r9.s = r1
        L_0x020c:
            int r1 = r0.f5523b
            if (r6 >= r1) goto L_0x029d
            java.lang.String r1 = r10.q()
            java.util.Set<java.lang.String> r2 = r9.s
            r2.add(r1)
            int r6 = r6 + 1
            goto L_0x020c
        L_0x021c:
            if (r1 != r7) goto L_0x010a
            java.lang.String r0 = r10.q()
            r9.r = r0
            goto L_0x0315
        L_0x0226:
            if (r1 != r5) goto L_0x010a
            long r0 = r10.o()
            r9.q = r0
            r9.h(r8)
            goto L_0x0315
        L_0x0233:
            r0 = 4
            if (r1 != r0) goto L_0x010a
            double r0 = r10.p()
            r9.p = r0
            r9.g(r8)
            goto L_0x0315
        L_0x0241:
            if (r1 != r5) goto L_0x010a
            long r0 = r10.o()
            r9.o = r0
            r9.f(r8)
            goto L_0x0315
        L_0x024e:
            if (r1 != r5) goto L_0x010a
            long r0 = r10.o()
            r9.n = r0
            r9.e(r8)
            goto L_0x0315
        L_0x025b:
            if (r1 != r4) goto L_0x010a
            com.miui.hybrid.accessory.a.f.b.i r0 = r10.i()
            java.util.HashSet r1 = new java.util.HashSet
            int r2 = r0.f5523b
            int r2 = r2 * 2
            r1.<init>(r2)
            r9.m = r1
        L_0x026c:
            int r1 = r0.f5523b
            if (r6 >= r1) goto L_0x029d
            java.lang.String r1 = r10.q()
            java.util.Set<java.lang.String> r2 = r9.m
            r2.add(r1)
            int r6 = r6 + 1
            goto L_0x026c
        L_0x027c:
            if (r1 != r4) goto L_0x010a
            com.miui.hybrid.accessory.a.f.b.i r0 = r10.i()
            java.util.HashSet r1 = new java.util.HashSet
            int r2 = r0.f5523b
            int r2 = r2 * 2
            r1.<init>(r2)
            r9.l = r1
        L_0x028d:
            int r1 = r0.f5523b
            if (r6 >= r1) goto L_0x029d
            java.lang.String r1 = r10.q()
            java.util.Set<java.lang.String> r2 = r9.l
            r2.add(r1)
            int r6 = r6 + 1
            goto L_0x028d
        L_0x029d:
            r10.j()
            goto L_0x0315
        L_0x02a2:
            if (r1 != r3) goto L_0x010a
            int r0 = r10.n()
            com.miui.hybrid.accessory.sdk.a.c r0 = com.miui.hybrid.accessory.sdk.a.c.a(r0)
            r9.k = r0
            goto L_0x0315
        L_0x02b0:
            if (r1 != r7) goto L_0x010a
            java.lang.String r0 = r10.q()
            r9.j = r0
            goto L_0x0315
        L_0x02b9:
            if (r1 != r7) goto L_0x010a
            java.lang.String r0 = r10.q()
            r9.i = r0
            goto L_0x0315
        L_0x02c2:
            if (r1 != r7) goto L_0x010a
            java.lang.String r0 = r10.q()
            r9.h = r0
            goto L_0x0315
        L_0x02cb:
            if (r1 != r7) goto L_0x010a
            java.lang.String r0 = r10.q()
            r9.g = r0
            goto L_0x0315
        L_0x02d4:
            if (r1 != r3) goto L_0x010a
            int r0 = r10.n()
            r9.f = r0
            r9.d(r8)
            goto L_0x0315
        L_0x02e0:
            if (r1 != r3) goto L_0x010a
            int r0 = r10.n()
            r9.e = r0
            r9.c(r8)
            goto L_0x0315
        L_0x02ec:
            if (r1 != r5) goto L_0x010a
            long r0 = r10.o()
            r9.f5534d = r0
            r9.b((boolean) r8)
            goto L_0x0315
        L_0x02f8:
            if (r1 != r7) goto L_0x010a
            java.lang.String r0 = r10.q()
            r9.f5533c = r0
            goto L_0x0315
        L_0x0301:
            if (r1 != r7) goto L_0x010a
            java.lang.String r0 = r10.q()
            r9.f5532b = r0
            goto L_0x0315
        L_0x030a:
            if (r1 != r5) goto L_0x010a
            long r0 = r10.o()
            r9.f5531a = r0
            r9.a((boolean) r8)
        L_0x0315:
            r10.d()
            goto L_0x0003
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.hybrid.accessory.sdk.a.a.a(com.miui.hybrid.accessory.a.f.b.e):void");
    }

    public void a(boolean z2) {
        this.ak.set(0, z2);
    }

    public boolean a() {
        return this.ak.get(0);
    }

    public boolean a(a aVar) {
        if (aVar == null || this.f5531a != aVar.f5531a) {
            return false;
        }
        boolean c2 = c();
        boolean c3 = aVar.c();
        if ((c2 || c3) && (!c2 || !c3 || !this.f5532b.equals(aVar.f5532b))) {
            return false;
        }
        boolean d2 = d();
        boolean d3 = aVar.d();
        if (((d2 || d3) && (!d2 || !d3 || !this.f5533c.equals(aVar.f5533c))) || this.f5534d != aVar.f5534d || this.e != aVar.e || this.f != aVar.f) {
            return false;
        }
        boolean i2 = i();
        boolean i3 = aVar.i();
        if ((i2 || i3) && (!i2 || !i3 || !this.g.equals(aVar.g))) {
            return false;
        }
        boolean k2 = k();
        boolean k3 = aVar.k();
        if ((k2 || k3) && (!k2 || !k3 || !this.h.equals(aVar.h))) {
            return false;
        }
        boolean l2 = l();
        boolean l3 = aVar.l();
        if ((l2 || l3) && (!l2 || !l3 || !this.i.equals(aVar.i))) {
            return false;
        }
        boolean m2 = m();
        boolean m3 = aVar.m();
        if ((m2 || m3) && (!m2 || !m3 || !this.j.equals(aVar.j))) {
            return false;
        }
        boolean n2 = n();
        boolean n3 = aVar.n();
        if ((n2 || n3) && (!n2 || !n3 || !this.k.equals(aVar.k))) {
            return false;
        }
        boolean o2 = o();
        boolean o3 = aVar.o();
        if ((o2 || o3) && (!o2 || !o3 || !this.l.equals(aVar.l))) {
            return false;
        }
        boolean p2 = p();
        boolean p3 = aVar.p();
        if (((p2 || p3) && (!p2 || !p3 || !this.m.equals(aVar.m))) || this.n != aVar.n || this.o != aVar.o || this.p != aVar.p) {
            return false;
        }
        boolean t2 = t();
        boolean t3 = aVar.t();
        if ((t2 || t3) && (!t2 || !t3 || this.q != aVar.q)) {
            return false;
        }
        boolean u2 = u();
        boolean u3 = aVar.u();
        if ((u2 || u3) && (!u2 || !u3 || !this.r.equals(aVar.r))) {
            return false;
        }
        boolean v2 = v();
        boolean v3 = aVar.v();
        if ((v2 || v3) && (!v2 || !v3 || !this.s.equals(aVar.s))) {
            return false;
        }
        boolean w2 = w();
        boolean w3 = aVar.w();
        if ((w2 || w3) && (!w2 || !w3 || !this.t.equals(aVar.t))) {
            return false;
        }
        boolean x2 = x();
        boolean x3 = aVar.x();
        if ((x2 || x3) && (!x2 || !x3 || !this.u.equals(aVar.u))) {
            return false;
        }
        boolean y2 = y();
        boolean y3 = aVar.y();
        if ((y2 || y3) && (!y2 || !y3 || !this.v.equals(aVar.v))) {
            return false;
        }
        boolean z2 = z();
        boolean z3 = aVar.z();
        if ((z2 || z3) && (!z2 || !z3 || !this.w.equals(aVar.w))) {
            return false;
        }
        boolean A2 = A();
        boolean A3 = aVar.A();
        if ((A2 || A3) && (!A2 || !A3 || !this.x.equals(aVar.x))) {
            return false;
        }
        boolean C2 = C();
        boolean C3 = aVar.C();
        if ((C2 || C3) && (!C2 || !C3 || this.y != aVar.y)) {
            return false;
        }
        boolean D2 = D();
        boolean D3 = aVar.D();
        if ((D2 || D3) && (!D2 || !D3 || !this.z.equals(aVar.z))) {
            return false;
        }
        boolean E2 = E();
        boolean E3 = aVar.E();
        if ((E2 || E3) && (!E2 || !E3 || !this.A.equals(aVar.A))) {
            return false;
        }
        boolean F2 = F();
        boolean F3 = aVar.F();
        if ((F2 || F3) && (!F2 || !F3 || !this.B.equals(aVar.B))) {
            return false;
        }
        boolean G2 = G();
        boolean G3 = aVar.G();
        if ((G2 || G3) && (!G2 || !G3 || this.C != aVar.C)) {
            return false;
        }
        boolean H2 = H();
        boolean H3 = aVar.H();
        if (H2 || H3) {
            return H2 && H3 && this.D.equals(aVar.D);
        }
        return true;
    }

    /* renamed from: b */
    public int compareTo(a aVar) {
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
        int a15;
        int a16;
        int a17;
        int a18;
        int a19;
        int a20;
        int a21;
        int a22;
        int a23;
        int a24;
        int a25;
        int a26;
        int a27;
        int a28;
        int a29;
        int a30;
        int a31;
        if (!a.class.equals(aVar.getClass())) {
            return a.class.getName().compareTo(aVar.getClass().getName());
        }
        int compareTo = Boolean.valueOf(a()).compareTo(Boolean.valueOf(aVar.a()));
        if (compareTo != 0) {
            return compareTo;
        }
        if (a() && (a31 = com.miui.hybrid.accessory.a.f.b.a(this.f5531a, aVar.f5531a)) != 0) {
            return a31;
        }
        int compareTo2 = Boolean.valueOf(c()).compareTo(Boolean.valueOf(aVar.c()));
        if (compareTo2 != 0) {
            return compareTo2;
        }
        if (c() && (a30 = com.miui.hybrid.accessory.a.f.b.a(this.f5532b, aVar.f5532b)) != 0) {
            return a30;
        }
        int compareTo3 = Boolean.valueOf(d()).compareTo(Boolean.valueOf(aVar.d()));
        if (compareTo3 != 0) {
            return compareTo3;
        }
        if (d() && (a29 = com.miui.hybrid.accessory.a.f.b.a(this.f5533c, aVar.f5533c)) != 0) {
            return a29;
        }
        int compareTo4 = Boolean.valueOf(e()).compareTo(Boolean.valueOf(aVar.e()));
        if (compareTo4 != 0) {
            return compareTo4;
        }
        if (e() && (a28 = com.miui.hybrid.accessory.a.f.b.a(this.f5534d, aVar.f5534d)) != 0) {
            return a28;
        }
        int compareTo5 = Boolean.valueOf(f()).compareTo(Boolean.valueOf(aVar.f()));
        if (compareTo5 != 0) {
            return compareTo5;
        }
        if (f() && (a27 = com.miui.hybrid.accessory.a.f.b.a(this.e, aVar.e)) != 0) {
            return a27;
        }
        int compareTo6 = Boolean.valueOf(g()).compareTo(Boolean.valueOf(aVar.g()));
        if (compareTo6 != 0) {
            return compareTo6;
        }
        if (g() && (a26 = com.miui.hybrid.accessory.a.f.b.a(this.f, aVar.f)) != 0) {
            return a26;
        }
        int compareTo7 = Boolean.valueOf(i()).compareTo(Boolean.valueOf(aVar.i()));
        if (compareTo7 != 0) {
            return compareTo7;
        }
        if (i() && (a25 = com.miui.hybrid.accessory.a.f.b.a(this.g, aVar.g)) != 0) {
            return a25;
        }
        int compareTo8 = Boolean.valueOf(k()).compareTo(Boolean.valueOf(aVar.k()));
        if (compareTo8 != 0) {
            return compareTo8;
        }
        if (k() && (a24 = com.miui.hybrid.accessory.a.f.b.a(this.h, aVar.h)) != 0) {
            return a24;
        }
        int compareTo9 = Boolean.valueOf(l()).compareTo(Boolean.valueOf(aVar.l()));
        if (compareTo9 != 0) {
            return compareTo9;
        }
        if (l() && (a23 = com.miui.hybrid.accessory.a.f.b.a(this.i, aVar.i)) != 0) {
            return a23;
        }
        int compareTo10 = Boolean.valueOf(m()).compareTo(Boolean.valueOf(aVar.m()));
        if (compareTo10 != 0) {
            return compareTo10;
        }
        if (m() && (a22 = com.miui.hybrid.accessory.a.f.b.a(this.j, aVar.j)) != 0) {
            return a22;
        }
        int compareTo11 = Boolean.valueOf(n()).compareTo(Boolean.valueOf(aVar.n()));
        if (compareTo11 != 0) {
            return compareTo11;
        }
        if (n() && (a21 = com.miui.hybrid.accessory.a.f.b.a((Comparable) this.k, (Comparable) aVar.k)) != 0) {
            return a21;
        }
        int compareTo12 = Boolean.valueOf(o()).compareTo(Boolean.valueOf(aVar.o()));
        if (compareTo12 != 0) {
            return compareTo12;
        }
        if (o() && (a20 = com.miui.hybrid.accessory.a.f.b.a((Set) this.l, (Set) aVar.l)) != 0) {
            return a20;
        }
        int compareTo13 = Boolean.valueOf(p()).compareTo(Boolean.valueOf(aVar.p()));
        if (compareTo13 != 0) {
            return compareTo13;
        }
        if (p() && (a19 = com.miui.hybrid.accessory.a.f.b.a((Set) this.m, (Set) aVar.m)) != 0) {
            return a19;
        }
        int compareTo14 = Boolean.valueOf(q()).compareTo(Boolean.valueOf(aVar.q()));
        if (compareTo14 != 0) {
            return compareTo14;
        }
        if (q() && (a18 = com.miui.hybrid.accessory.a.f.b.a(this.n, aVar.n)) != 0) {
            return a18;
        }
        int compareTo15 = Boolean.valueOf(r()).compareTo(Boolean.valueOf(aVar.r()));
        if (compareTo15 != 0) {
            return compareTo15;
        }
        if (r() && (a17 = com.miui.hybrid.accessory.a.f.b.a(this.o, aVar.o)) != 0) {
            return a17;
        }
        int compareTo16 = Boolean.valueOf(s()).compareTo(Boolean.valueOf(aVar.s()));
        if (compareTo16 != 0) {
            return compareTo16;
        }
        if (s() && (a16 = com.miui.hybrid.accessory.a.f.b.a(this.p, aVar.p)) != 0) {
            return a16;
        }
        int compareTo17 = Boolean.valueOf(t()).compareTo(Boolean.valueOf(aVar.t()));
        if (compareTo17 != 0) {
            return compareTo17;
        }
        if (t() && (a15 = com.miui.hybrid.accessory.a.f.b.a(this.q, aVar.q)) != 0) {
            return a15;
        }
        int compareTo18 = Boolean.valueOf(u()).compareTo(Boolean.valueOf(aVar.u()));
        if (compareTo18 != 0) {
            return compareTo18;
        }
        if (u() && (a14 = com.miui.hybrid.accessory.a.f.b.a(this.r, aVar.r)) != 0) {
            return a14;
        }
        int compareTo19 = Boolean.valueOf(v()).compareTo(Boolean.valueOf(aVar.v()));
        if (compareTo19 != 0) {
            return compareTo19;
        }
        if (v() && (a13 = com.miui.hybrid.accessory.a.f.b.a((Set) this.s, (Set) aVar.s)) != 0) {
            return a13;
        }
        int compareTo20 = Boolean.valueOf(w()).compareTo(Boolean.valueOf(aVar.w()));
        if (compareTo20 != 0) {
            return compareTo20;
        }
        if (w() && (a12 = com.miui.hybrid.accessory.a.f.b.a((Set) this.t, (Set) aVar.t)) != 0) {
            return a12;
        }
        int compareTo21 = Boolean.valueOf(x()).compareTo(Boolean.valueOf(aVar.x()));
        if (compareTo21 != 0) {
            return compareTo21;
        }
        if (x() && (a11 = com.miui.hybrid.accessory.a.f.b.a((Set) this.u, (Set) aVar.u)) != 0) {
            return a11;
        }
        int compareTo22 = Boolean.valueOf(y()).compareTo(Boolean.valueOf(aVar.y()));
        if (compareTo22 != 0) {
            return compareTo22;
        }
        if (y() && (a10 = com.miui.hybrid.accessory.a.f.b.a(this.v, aVar.v)) != 0) {
            return a10;
        }
        int compareTo23 = Boolean.valueOf(z()).compareTo(Boolean.valueOf(aVar.z()));
        if (compareTo23 != 0) {
            return compareTo23;
        }
        if (z() && (a9 = com.miui.hybrid.accessory.a.f.b.a((List) this.w, (List) aVar.w)) != 0) {
            return a9;
        }
        int compareTo24 = Boolean.valueOf(A()).compareTo(Boolean.valueOf(aVar.A()));
        if (compareTo24 != 0) {
            return compareTo24;
        }
        if (A() && (a8 = com.miui.hybrid.accessory.a.f.b.a((List) this.x, (List) aVar.x)) != 0) {
            return a8;
        }
        int compareTo25 = Boolean.valueOf(C()).compareTo(Boolean.valueOf(aVar.C()));
        if (compareTo25 != 0) {
            return compareTo25;
        }
        if (C() && (a7 = com.miui.hybrid.accessory.a.f.b.a(this.y, aVar.y)) != 0) {
            return a7;
        }
        int compareTo26 = Boolean.valueOf(D()).compareTo(Boolean.valueOf(aVar.D()));
        if (compareTo26 != 0) {
            return compareTo26;
        }
        if (D() && (a6 = com.miui.hybrid.accessory.a.f.b.a((Set) this.z, (Set) aVar.z)) != 0) {
            return a6;
        }
        int compareTo27 = Boolean.valueOf(E()).compareTo(Boolean.valueOf(aVar.E()));
        if (compareTo27 != 0) {
            return compareTo27;
        }
        if (E() && (a5 = com.miui.hybrid.accessory.a.f.b.a(this.A, aVar.A)) != 0) {
            return a5;
        }
        int compareTo28 = Boolean.valueOf(F()).compareTo(Boolean.valueOf(aVar.F()));
        if (compareTo28 != 0) {
            return compareTo28;
        }
        if (F() && (a4 = com.miui.hybrid.accessory.a.f.b.a(this.B, aVar.B)) != 0) {
            return a4;
        }
        int compareTo29 = Boolean.valueOf(G()).compareTo(Boolean.valueOf(aVar.G()));
        if (compareTo29 != 0) {
            return compareTo29;
        }
        if (G() && (a3 = com.miui.hybrid.accessory.a.f.b.a(this.C, aVar.C)) != 0) {
            return a3;
        }
        int compareTo30 = Boolean.valueOf(H()).compareTo(Boolean.valueOf(aVar.H()));
        if (compareTo30 != 0) {
            return compareTo30;
        }
        if (!H() || (a2 = com.miui.hybrid.accessory.a.f.b.a(this.D, aVar.D)) == 0) {
            return 0;
        }
        return a2;
    }

    public String b() {
        return this.f5532b;
    }

    public void b(boolean z2) {
        this.ak.set(1, z2);
    }

    public void c(boolean z2) {
        this.ak.set(2, z2);
    }

    public boolean c() {
        return this.f5532b != null;
    }

    public void d(boolean z2) {
        this.ak.set(3, z2);
    }

    public boolean d() {
        return this.f5533c != null;
    }

    public void e(boolean z2) {
        this.ak.set(4, z2);
    }

    public boolean e() {
        return this.ak.get(1);
    }

    public boolean equals(Object obj) {
        if (obj != null && (obj instanceof a)) {
            return a((a) obj);
        }
        return false;
    }

    public void f(boolean z2) {
        this.ak.set(5, z2);
    }

    public boolean f() {
        return this.ak.get(2);
    }

    public void g(boolean z2) {
        this.ak.set(6, z2);
    }

    public boolean g() {
        return this.ak.get(3);
    }

    public String h() {
        return this.g;
    }

    public void h(boolean z2) {
        this.ak.set(7, z2);
    }

    public int hashCode() {
        return 0;
    }

    public void i(boolean z2) {
        this.ak.set(8, z2);
    }

    public boolean i() {
        return this.g != null;
    }

    public String j() {
        return this.h;
    }

    public void j(boolean z2) {
        this.ak.set(9, z2);
    }

    public boolean k() {
        return this.h != null;
    }

    public boolean l() {
        return this.i != null;
    }

    public boolean m() {
        return this.j != null;
    }

    public boolean n() {
        return this.k != null;
    }

    public boolean o() {
        return this.l != null;
    }

    public boolean p() {
        return this.m != null;
    }

    public boolean q() {
        return this.ak.get(4);
    }

    public boolean r() {
        return this.ak.get(5);
    }

    public boolean s() {
        return this.ak.get(6);
    }

    public boolean t() {
        return this.ak.get(7);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("AppInfo(");
        sb.append("appId:");
        sb.append(this.f5531a);
        sb.append(", ");
        sb.append("appName:");
        String str = this.f5532b;
        if (str == null) {
            sb.append("null");
        } else {
            sb.append(str);
        }
        sb.append(", ");
        sb.append("appSecret:");
        String str2 = this.f5533c;
        if (str2 == null) {
            sb.append("null");
        } else {
            sb.append(str2);
        }
        sb.append(", ");
        sb.append("appKey:");
        sb.append(this.f5534d);
        sb.append(", ");
        sb.append("appVersionCode:");
        sb.append(this.e);
        sb.append(", ");
        sb.append("sdkVersionCode:");
        sb.append(this.f);
        sb.append(", ");
        sb.append("packageName:");
        String str3 = this.g;
        if (str3 == null) {
            sb.append("null");
        } else {
            sb.append(str3);
        }
        sb.append(", ");
        sb.append("icon:");
        String str4 = this.h;
        if (str4 == null) {
            sb.append("null");
        } else {
            sb.append(str4);
        }
        sb.append(", ");
        sb.append("description:");
        String str5 = this.i;
        if (str5 == null) {
            sb.append("null");
        } else {
            sb.append(str5);
        }
        sb.append(", ");
        sb.append("downloadUrl:");
        String str6 = this.j;
        if (str6 == null) {
            sb.append("null");
        } else {
            sb.append(str6);
        }
        sb.append(", ");
        sb.append("status:");
        c cVar = this.k;
        if (cVar == null) {
            sb.append("null");
        } else {
            sb.append(cVar);
        }
        sb.append(", ");
        sb.append("categories:");
        Set<String> set = this.l;
        if (set == null) {
            sb.append("null");
        } else {
            sb.append(set);
        }
        sb.append(", ");
        sb.append("keywords:");
        Set<String> set2 = this.m;
        if (set2 == null) {
            sb.append("null");
        } else {
            sb.append(set2);
        }
        sb.append(", ");
        sb.append("createTime:");
        sb.append(this.n);
        sb.append(", ");
        sb.append("lastUpdateTime:");
        sb.append(this.o);
        sb.append(", ");
        sb.append("popularity:");
        sb.append(this.p);
        if (t()) {
            sb.append(", ");
            sb.append("developerId:");
            sb.append(this.q);
        }
        if (u()) {
            sb.append(", ");
            sb.append("company:");
            String str7 = this.r;
            if (str7 == null) {
                sb.append("null");
            } else {
                sb.append(str7);
            }
        }
        if (v()) {
            sb.append(", ");
            sb.append("models:");
            Set<String> set3 = this.s;
            if (set3 == null) {
                sb.append("null");
            } else {
                sb.append(set3);
            }
        }
        if (w()) {
            sb.append(", ");
            sb.append("locales:");
            Set<String> set4 = this.t;
            if (set4 == null) {
                sb.append("null");
            } else {
                sb.append(set4);
            }
        }
        if (x()) {
            sb.append(", ");
            sb.append("regions:");
            Set<String> set5 = this.u;
            if (set5 == null) {
                sb.append("null");
            } else {
                sb.append(set5);
            }
        }
        if (y()) {
            sb.append(", ");
            sb.append("defaultPageName:");
            String str8 = this.v;
            if (str8 == null) {
                sb.append("null");
            } else {
                sb.append(str8);
            }
        }
        if (z()) {
            sb.append(", ");
            sb.append("pages:");
            List<e> list = this.w;
            if (list == null) {
                sb.append("null");
            } else {
                sb.append(list);
            }
        }
        if (A()) {
            sb.append(", ");
            sb.append("nativePackageNames:");
            List<String> list2 = this.x;
            if (list2 == null) {
                sb.append("null");
            } else {
                sb.append(list2);
            }
        }
        if (C()) {
            sb.append(", ");
            sb.append("size:");
            sb.append(this.y);
        }
        if (D()) {
            sb.append(", ");
            sb.append("domains:");
            Set<String> set6 = this.z;
            if (set6 == null) {
                sb.append("null");
            } else {
                sb.append(set6);
            }
        }
        if (E()) {
            sb.append(", ");
            sb.append("changeLog:");
            String str9 = this.A;
            if (str9 == null) {
                sb.append("null");
            } else {
                sb.append(str9);
            }
        }
        if (F()) {
            sb.append(", ");
            sb.append("introduction:");
            String str10 = this.B;
            if (str10 == null) {
                sb.append("null");
            } else {
                sb.append(str10);
            }
        }
        if (G()) {
            sb.append(", ");
            sb.append("minMinaVersionCode:");
            sb.append(this.C);
        }
        if (H()) {
            sb.append(", ");
            sb.append("appVersionName:");
            String str11 = this.D;
            if (str11 == null) {
                sb.append("null");
            } else {
                sb.append(str11);
            }
        }
        sb.append(")");
        return sb.toString();
    }

    public boolean u() {
        return this.r != null;
    }

    public boolean v() {
        return this.s != null;
    }

    public boolean w() {
        return this.t != null;
    }

    public boolean x() {
        return this.u != null;
    }

    public boolean y() {
        return this.v != null;
    }

    public boolean z() {
        return this.w != null;
    }
}
