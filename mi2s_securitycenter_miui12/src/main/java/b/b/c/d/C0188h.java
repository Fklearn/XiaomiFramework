package b.b.c.d;

import com.google.android.exoplayer2.upstream.DataSchemeDataSource;
import com.miui.luckymoney.config.Constants;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;

/* renamed from: b.b.c.d.h  reason: case insensitive filesystem */
public class C0188h {

    /* renamed from: a  reason: collision with root package name */
    private static final String f1678a = "https://adv.sec.miui.com/info/layout";

    /* renamed from: b  reason: collision with root package name */
    private static final String f1679b = "https://adv.sec.intl.miui.com/info/layout";

    /* renamed from: c  reason: collision with root package name */
    private static int f1680c = 2;

    /* renamed from: d  reason: collision with root package name */
    private static int f1681d = 1;
    private String e;
    private String f;
    private String g;
    private boolean h;
    private String i;
    private boolean j;
    private ArrayList<C0185e> k = new ArrayList<>();

    public static C0188h a(JSONObject jSONObject, boolean z) {
        int length;
        C0188h hVar = new C0188h();
        hVar.e = jSONObject.optString("type");
        hVar.f = jSONObject.optString(Constants.JSON_KEY_DATA_VERSION);
        hVar.g = jSONObject.optString("layoutId");
        hVar.h = jSONObject.optInt("status") == 1;
        hVar.i = jSONObject.optString("tn");
        hVar.a(true);
        JSONArray optJSONArray = jSONObject.optJSONArray(DataSchemeDataSource.SCHEME_DATA);
        if (optJSONArray == null || (length = optJSONArray.length()) == 0) {
            return null;
        }
        JSONArray optJSONArray2 = jSONObject.optJSONArray("functions");
        ArrayList arrayList = new ArrayList(0);
        HashSet hashSet = new HashSet(0);
        if (optJSONArray2 != null) {
            for (int i2 = 0; i2 < optJSONArray2.length(); i2++) {
                arrayList.add(optJSONArray2.getJSONObject(i2));
            }
            for (int i3 = 0; i3 < length; i3++) {
                JSONObject jSONObject2 = optJSONArray.getJSONObject(i3);
                String optString = jSONObject2.optString("type");
                JSONObject optJSONObject = jSONObject2.optJSONObject(DataSchemeDataSource.SCHEME_DATA);
                if ("002".equals(optString)) {
                    hashSet.add(optJSONObject.optString("functionId"));
                }
            }
        }
        a(optJSONArray, arrayList, hashSet, hVar, (String) null, (C0187g) null, z);
        if (!hVar.k.isEmpty() && (hVar.k.get(0) instanceof p)) {
            hVar.k.remove(0);
        }
        return hVar;
    }

    public static C0193m a(C0193m mVar, ArrayList<JSONObject> arrayList, Set<String> set) {
        C0193m a2;
        JSONObject jSONObject = null;
        if (mVar != null) {
            Iterator<JSONObject> it = arrayList.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                JSONObject next = it.next();
                if (next.optString("functionId").equals(String.valueOf(mVar.b()))) {
                    jSONObject = next;
                    break;
                }
            }
            if (jSONObject != null) {
                arrayList.remove(jSONObject);
            }
            return mVar;
        }
        while (arrayList.size() > 0) {
            JSONObject remove = arrayList.remove(0);
            if (!set.contains(remove.optString("functionId")) && (a2 = C0193m.a(remove)) != null) {
                return a2;
            }
        }
        return null;
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:0x002d  */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x0036  */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0039  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String a(java.lang.String r3, java.util.Map<java.lang.String, java.lang.String> r4) {
        /*
            if (r4 != 0) goto L_0x0007
            java.util.HashMap r4 = new java.util.HashMap
            r4.<init>()
        L_0x0007:
            java.lang.String r0 = "channel"
            r4.put(r0, r3)
            boolean r3 = com.miui.securityscan.M.l()
            boolean r0 = com.miui.securityscan.M.m()
            java.lang.String r1 = "1"
            java.lang.String r2 = "setting"
            if (r0 != 0) goto L_0x0020
            java.lang.String r3 = "2"
        L_0x001c:
            r4.put(r2, r3)
            goto L_0x0029
        L_0x0020:
            if (r3 == 0) goto L_0x0026
            r4.put(r2, r1)
            goto L_0x0029
        L_0x0026:
            java.lang.String r3 = "3"
            goto L_0x001c
        L_0x0029:
            boolean r3 = miui.os.Build.IS_INTERNATIONAL_BUILD
            if (r3 == 0) goto L_0x0032
            java.lang.String r3 = "nt"
            r4.put(r3, r1)
        L_0x0032:
            boolean r3 = miui.os.Build.IS_INTERNATIONAL_BUILD
            if (r3 == 0) goto L_0x0039
            java.lang.String r3 = f1679b
            goto L_0x003b
        L_0x0039:
            java.lang.String r3 = f1678a
        L_0x003b:
            b.b.c.h.j r0 = new b.b.c.h.j
            java.lang.String r1 = "common_datamodel"
            r0.<init>(r1)
            java.lang.String r3 = com.miui.securityscan.i.k.a((java.util.Map<java.lang.String, java.lang.String>) r4, (java.lang.String) r3, (b.b.c.h.j) r0)
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: b.b.c.d.C0188h.a(java.lang.String, java.util.Map):java.lang.String");
    }

    public static void a(int i2) {
        f1680c = i2;
    }

    private void a(C0185e eVar) {
        if (eVar != null) {
            eVar.a(this.i);
            this.k.add(eVar);
        }
    }

    private static void a(C0188h hVar, C0187g gVar, C0185e eVar) {
        if (gVar == null) {
            hVar.a(eVar);
            return;
        }
        if (gVar.f()) {
            if (gVar.e() < gVar.c()) {
                hVar.a(eVar);
            }
            gVar.a(eVar);
            eVar.a(hVar.d());
        } else {
            hVar.a(eVar);
        }
        if (eVar instanceof s) {
            ((s) eVar).b(gVar.b());
        }
    }

    private static void a(JSONArray jSONArray, r rVar) {
        int length = jSONArray.length();
        for (int i2 = 0; i2 < length; i2++) {
            JSONObject jSONObject = jSONArray.getJSONObject(i2);
            String optString = jSONObject.optString("type");
            JSONObject optJSONObject = jSONObject.optJSONObject(DataSchemeDataSource.SCHEME_DATA);
            if ("003".equals(optString)) {
                r a2 = r.a(optJSONObject);
                if (!(rVar == null || a2 == null)) {
                    rVar.a(a2);
                }
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v7, resolved type: b.b.c.d.q} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v16, resolved type: b.b.c.d.f} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v28, resolved type: b.b.c.d.q} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v29, resolved type: b.b.c.d.q} */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x005f, code lost:
        if (r0 != null) goto L_0x0036;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:5:0x0034, code lost:
        if (r0 != null) goto L_0x0036;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void a(org.json.JSONArray r18, java.util.ArrayList<org.json.JSONObject> r19, java.util.Set<java.lang.String> r20, b.b.c.d.C0188h r21, java.lang.String r22, b.b.c.d.C0187g r23, boolean r24) {
        /*
            r7 = r21
            r8 = r23
            int r9 = r18.length()
            r0 = 0
            r11 = r22
            r10 = r0
        L_0x000c:
            if (r10 >= r9) goto L_0x01c4
            r12 = r18
            org.json.JSONObject r13 = r12.getJSONObject(r10)
            java.lang.String r0 = "type"
            java.lang.String r0 = r13.optString(r0)
            java.lang.String r1 = "data"
            org.json.JSONObject r1 = r13.optJSONObject(r1)
            java.lang.String r2 = "002"
            boolean r2 = r2.equals(r0)
            if (r2 == 0) goto L_0x003a
            b.b.c.d.m r0 = b.b.c.d.C0193m.a((org.json.JSONObject) r1)
            r14 = r19
            r15 = r20
            b.b.c.d.m r0 = a((b.b.c.d.C0193m) r0, (java.util.ArrayList<org.json.JSONObject>) r14, (java.util.Set<java.lang.String>) r15)
            if (r0 == 0) goto L_0x0086
        L_0x0036:
            r7.a((b.b.c.d.C0185e) r0)
            goto L_0x0086
        L_0x003a:
            r14 = r19
            r15 = r20
            java.lang.String r2 = "001"
            boolean r2 = r2.equals(r0)
            java.lang.String r3 = "template"
            r6 = 1
            if (r2 != 0) goto L_0x0162
            java.lang.String r2 = "0010"
            boolean r2 = r2.equals(r0)
            if (r2 == 0) goto L_0x0053
            goto L_0x0162
        L_0x0053:
            java.lang.String r2 = "003"
            boolean r2 = r2.equals(r0)
            if (r2 == 0) goto L_0x0062
            b.b.c.d.r r0 = b.b.c.d.r.a((org.json.JSONObject) r1)
            if (r0 == 0) goto L_0x0086
            goto L_0x0036
        L_0x0062:
            java.lang.String r2 = "004"
            boolean r2 = r2.equals(r0)
            if (r2 == 0) goto L_0x008a
            b.b.c.d.s r0 = new b.b.c.d.s
            r0.<init>(r1)
            boolean r1 = android.text.TextUtils.isEmpty(r11)
            if (r1 == 0) goto L_0x0080
            com.miui.securitycenter.Application r1 = com.miui.securitycenter.Application.d()
            r2 = 2131756597(0x7f100635, float:1.9144106E38)
            java.lang.String r11 = r1.getString(r2)
        L_0x0080:
            r0.c(r11)
            a((b.b.c.d.C0188h) r7, (b.b.c.d.C0187g) r8, (b.b.c.d.C0185e) r0)
        L_0x0086:
            r17 = r9
            goto L_0x01be
        L_0x008a:
            java.lang.String r2 = "005"
            boolean r2 = r2.equals(r0)
            if (r2 == 0) goto L_0x00b2
            java.util.ArrayList<b.b.c.d.e> r0 = r7.k
            boolean r1 = r0.isEmpty()
            if (r1 != 0) goto L_0x0086
            int r1 = r0.size()
            int r1 = r1 - r6
            java.lang.Object r0 = r0.get(r1)
            boolean r0 = r0 instanceof b.b.c.d.p
            if (r0 != 0) goto L_0x0086
            java.util.ArrayList<b.b.c.d.e> r0 = r7.k
            b.b.c.d.p r1 = new b.b.c.d.p
            r1.<init>()
            r0.add(r1)
            goto L_0x0086
        L_0x00b2:
            java.lang.String r2 = "rowType"
            java.lang.String r2 = r13.optString(r2)
            java.lang.String r4 = "card"
            boolean r2 = r4.equals(r2)
            if (r2 == 0) goto L_0x0152
            int r0 = r13.optInt(r3)
            boolean r0 = b.b.c.d.C0187g.a((int) r0)
            if (r0 != 0) goto L_0x00cb
            goto L_0x0086
        L_0x00cb:
            java.lang.String r0 = "list"
            org.json.JSONArray r0 = r13.optJSONArray(r0)
            b.b.c.d.g r5 = new b.b.c.d.g
            r5.<init>(r13)
            if (r0 == 0) goto L_0x0122
            int r1 = r0.length()
            if (r1 <= 0) goto L_0x0122
            int r1 = r5.d()
            r2 = 5
            if (r1 != r2) goto L_0x0102
            b.b.c.d.r r1 = new b.b.c.d.r
            r1.<init>()
            r2 = 1001(0x3e9, float:1.403E-42)
            r1.a((int) r2)
            a((org.json.JSONArray) r0, (b.b.c.d.r) r1)
            int r0 = r1.c()
            r2 = 3
            if (r0 < r2) goto L_0x0122
            java.util.ArrayList<b.b.c.d.e> r0 = r7.k
            r0.add(r5)
            r7.a((b.b.c.d.C0185e) r1)
            goto L_0x0122
        L_0x0102:
            boolean r1 = r5.g()
            if (r1 == 0) goto L_0x010b
            r7.a((b.b.c.d.C0185e) r5)
        L_0x010b:
            java.lang.String r1 = "title"
            java.lang.String r4 = r13.optString(r1)
            r1 = r19
            r2 = r20
            r3 = r21
            r16 = r5
            r17 = r9
            r9 = r6
            r6 = r24
            a(r0, r1, r2, r3, r4, r5, r6)
            goto L_0x0127
        L_0x0122:
            r16 = r5
            r17 = r9
            r9 = r6
        L_0x0127:
            boolean r0 = r16.f()
            if (r0 == 0) goto L_0x01be
            java.lang.String r0 = "module"
            org.json.JSONArray r0 = r13.optJSONArray(r0)
            if (r0 == 0) goto L_0x01be
            int r1 = r0.length()
            if (r1 <= 0) goto L_0x01be
            int r0 = r0.length()
            if (r0 != r9) goto L_0x01be
            b.b.c.d.f r0 = new b.b.c.d.f
            r0.<init>(r13)
            java.lang.String r1 = r16.b()
            r0.b(r1)
        L_0x014d:
            r7.a((b.b.c.d.C0185e) r0)
            goto L_0x01be
        L_0x0152:
            r17 = r9
            java.lang.String r2 = "020"
            boolean r0 = r2.equals(r0)
            if (r0 == 0) goto L_0x01be
            b.b.c.d.q r0 = new b.b.c.d.q
            r0.<init>(r1)
            goto L_0x014d
        L_0x0162:
            r17 = r9
            r9 = r6
            java.lang.String r0 = "DataModel"
            if (r10 != 0) goto L_0x0177
            int r2 = r1.optInt(r3)
            r3 = 70
            if (r2 != r3) goto L_0x0177
            java.lang.String r1 = "clean it"
            android.util.Log.i(r0, r1)
            goto L_0x01be
        L_0x0177:
            if (r24 == 0) goto L_0x01be
            boolean r2 = r21.c()
            if (r2 == 0) goto L_0x0182
            r2 = 123(0x7b, double:6.1E-322)
            goto L_0x0184
        L_0x0182:
            r2 = 456(0x1c8, double:2.253E-321)
        L_0x0184:
            int r4 = f1681d
            if (r4 == r9) goto L_0x0189
            r5 = 2
        L_0x0189:
            int r4 = f1681d
            int r4 = r4 + r9
            f1681d = r4
            int r4 = f1681d
            int r5 = f1680c
            int r5 = r5 + r9
            if (r4 != r5) goto L_0x0197
            f1681d = r9
        L_0x0197:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "DataModel addItem: placeId: "
            r4.append(r5)
            java.lang.String r5 = ""
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            android.util.Log.d(r0, r4)
            b.b.c.d.d r0 = b.b.c.d.C0184d.a((long) r2, (org.json.JSONObject) r1, (java.lang.String) r5)
            if (r0 == 0) goto L_0x01be
            java.lang.String r1 = r7.g
            r0.e(r1)
            r0.c((java.lang.String) r5)
            a((b.b.c.d.C0188h) r7, (b.b.c.d.C0187g) r8, (b.b.c.d.C0185e) r0)
        L_0x01be:
            int r10 = r10 + 1
            r9 = r17
            goto L_0x000c
        L_0x01c4:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: b.b.c.d.C0188h.a(org.json.JSONArray, java.util.ArrayList, java.util.Set, b.b.c.d.h, java.lang.String, b.b.c.d.g, boolean):void");
    }

    private String d() {
        return this.i;
    }

    public void a(boolean z) {
        this.j = z;
    }

    public boolean a() {
        return this.h;
    }

    public List<C0185e> b() {
        return this.k;
    }

    public boolean c() {
        return this.j;
    }
}
