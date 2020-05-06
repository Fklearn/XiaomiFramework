package com.miui.antivirus.result;

import android.content.Context;
import b.b.b.a.a;
import b.b.c.h.j;
import com.google.android.exoplayer2.upstream.DataSchemeDataSource;
import com.miui.activityutil.o;
import com.miui.luckymoney.config.Constants;
import com.miui.securitycenter.Application;
import com.miui.securityscan.M;
import com.miui.securityscan.i.k;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import miui.os.Build;
import org.json.JSONArray;
import org.json.JSONObject;

/* renamed from: com.miui.antivirus.result.j  reason: case insensitive filesystem */
public class C0247j implements Serializable {

    /* renamed from: a  reason: collision with root package name */
    private static int f2837a = 2;

    /* renamed from: b  reason: collision with root package name */
    private static int f2838b = 1;

    /* renamed from: c  reason: collision with root package name */
    private List<C0244g> f2839c = new ArrayList();

    /* renamed from: d  reason: collision with root package name */
    private String f2840d;
    private String e;
    private String f;
    private boolean g;
    private String h;
    private boolean i = false;

    public static C0247j a(JSONObject jSONObject, boolean z) {
        C0247j jVar = new C0247j();
        jVar.f2840d = jSONObject.optString("type");
        jVar.e = jSONObject.optString(Constants.JSON_KEY_DATA_VERSION);
        jVar.f = jSONObject.optString("layoutId");
        boolean z2 = true;
        if (jSONObject.optInt("status") != 1) {
            z2 = false;
        }
        jVar.g = z2;
        jVar.h = jSONObject.optString("tn");
        JSONArray optJSONArray = jSONObject.optJSONArray(DataSchemeDataSource.SCHEME_DATA);
        if (optJSONArray == null || optJSONArray.length() == 0) {
            return null;
        }
        JSONArray optJSONArray2 = jSONObject.optJSONArray("functions");
        ArrayList arrayList = new ArrayList(0);
        HashSet hashSet = new HashSet(0);
        if (optJSONArray2 != null) {
            for (int i2 = 0; i2 < optJSONArray2.length(); i2++) {
                arrayList.add(optJSONArray2.getJSONObject(i2));
            }
            for (int i3 = 0; i3 < optJSONArray.length(); i3++) {
                JSONObject jSONObject2 = optJSONArray.getJSONObject(i3);
                String optString = jSONObject2.optString("type");
                JSONObject optJSONObject = jSONObject2.optJSONObject(DataSchemeDataSource.SCHEME_DATA);
                if ("002".equals(optString)) {
                    hashSet.add(optJSONObject.optString("functionId"));
                }
            }
        }
        a(optJSONArray, arrayList, hashSet, jVar, (String) null, (C0246i) null, z);
        return jVar;
    }

    public static C0248k a(C0248k kVar, ArrayList<JSONObject> arrayList, Set<String> set) {
        C0248k a2;
        JSONObject jSONObject = null;
        if (kVar != null) {
            Iterator<JSONObject> it = arrayList.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                JSONObject next = it.next();
                if (next.optString("functionId").equals(String.valueOf(kVar.c()))) {
                    jSONObject = next;
                    break;
                }
            }
            if (jSONObject != null) {
                arrayList.remove(jSONObject);
            }
            return kVar;
        }
        while (arrayList.size() > 0) {
            JSONObject remove = arrayList.remove(0);
            if (!set.contains(remove.optString("functionId")) && (a2 = C0248k.a(remove)) != null) {
                return a2;
            }
        }
        return null;
    }

    public static String a(Map<String, String> map) {
        if (map == null) {
            map = new HashMap<>();
        }
        Application d2 = Application.d();
        if (Build.IS_INTERNATIONAL_BUILD) {
            map.put("channel", "01-16");
            map.put("nt", o.f2310b);
        } else {
            map.put("channel", "01-10");
            map.put("deviceId", a.a((Context) d2));
            map.put("landingPageUrlType", "market");
        }
        boolean l = M.l();
        if (!M.m()) {
            map.put("setting", "2");
        } else if (l) {
            map.put("setting", o.f2310b);
        }
        return k.a(map, Build.IS_INTERNATIONAL_BUILD ? "https://adv.sec.intl.miui.com/info/layout" : "https://adv.sec.miui.com/info/layout", k.a.POST, "2dcd9s0c-ad3f-2fas-0l3a-abzo301jd0s9", new j("antivirus_datamodel_post"));
    }

    private void a(C0244g gVar) {
        if (gVar != null) {
            gVar.setTestKey(this.h);
            this.f2839c.add(gVar);
        }
    }

    private static void a(C0247j jVar, C0246i iVar, C0244g gVar) {
        if (iVar == null) {
            jVar.a(gVar);
            return;
        }
        if (iVar.g()) {
            if (iVar.f() < iVar.d()) {
                jVar.a(gVar);
            }
            iVar.a(gVar);
            gVar.setTestKey(jVar.c());
        } else {
            jVar.a(gVar);
        }
        if (gVar instanceof r) {
            ((r) gVar).a(iVar.c());
        }
    }

    private static void a(JSONArray jSONArray, q qVar) {
        int length = jSONArray.length();
        for (int i2 = 0; i2 < length; i2++) {
            JSONObject jSONObject = jSONArray.getJSONObject(i2);
            String optString = jSONObject.optString("type");
            JSONObject optJSONObject = jSONObject.optJSONObject(DataSchemeDataSource.SCHEME_DATA);
            if ("003".equals(optString)) {
                q a2 = q.a(optJSONObject);
                if (!(qVar == null || a2 == null)) {
                    qVar.a(a2);
                }
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:69:0x0155  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x0166  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void a(org.json.JSONArray r17, java.util.ArrayList<org.json.JSONObject> r18, java.util.Set<java.lang.String> r19, com.miui.antivirus.result.C0247j r20, java.lang.String r21, com.miui.antivirus.result.C0246i r22, boolean r23) {
        /*
            r7 = r20
            r8 = r22
            r10 = 1
            if (r8 == 0) goto L_0x0009
            r11 = r10
            goto L_0x000a
        L_0x0009:
            r11 = 0
        L_0x000a:
            if (r8 == 0) goto L_0x0014
            boolean r0 = r22.h()
            if (r0 == 0) goto L_0x0014
            r12 = r10
            goto L_0x0015
        L_0x0014:
            r12 = 0
        L_0x0015:
            r14 = r21
            r13 = 0
        L_0x0018:
            int r0 = r17.length()
            if (r13 >= r0) goto L_0x01a8
            if (r13 != 0) goto L_0x0022
            if (r12 == 0) goto L_0x0024
        L_0x0022:
            if (r11 != 0) goto L_0x0026
        L_0x0024:
            r0 = r10
            goto L_0x0027
        L_0x0026:
            r0 = 0
        L_0x0027:
            int r1 = r17.length()
            int r1 = r1 - r10
            if (r13 == r1) goto L_0x0035
            if (r11 != 0) goto L_0x0031
            goto L_0x0035
        L_0x0031:
            r1 = 0
            r15 = r17
            goto L_0x0038
        L_0x0035:
            r15 = r17
            r1 = r10
        L_0x0038:
            org.json.JSONObject r6 = r15.getJSONObject(r13)
            java.lang.String r2 = "type"
            java.lang.String r2 = r6.optString(r2)
            java.lang.String r3 = "data"
            org.json.JSONObject r3 = r6.optJSONObject(r3)
            java.lang.String r4 = "002"
            boolean r4 = r4.equals(r2)
            if (r4 == 0) goto L_0x006a
            com.miui.antivirus.result.k r2 = com.miui.antivirus.result.C0248k.a((org.json.JSONObject) r3)
            r5 = r18
            r4 = r19
            com.miui.antivirus.result.k r2 = a((com.miui.antivirus.result.C0248k) r2, (java.util.ArrayList<org.json.JSONObject>) r5, (java.util.Set<java.lang.String>) r4)
            if (r2 == 0) goto L_0x0067
            r2.b(r0)
            r2.a(r1)
            r7.a((com.miui.antivirus.result.C0244g) r2)
        L_0x0067:
            r4 = r10
            goto L_0x01a3
        L_0x006a:
            r5 = r18
            r4 = r19
            java.lang.String r10 = "001"
            boolean r10 = r10.equals(r2)
            java.lang.String r9 = "template"
            if (r10 != 0) goto L_0x0168
            java.lang.String r10 = "0010"
            boolean r10 = r10.equals(r2)
            if (r10 == 0) goto L_0x0082
            goto L_0x0168
        L_0x0082:
            java.lang.String r10 = "003"
            boolean r10 = r10.equals(r2)
            if (r10 == 0) goto L_0x009a
            com.miui.antivirus.result.q r2 = com.miui.antivirus.result.q.a((org.json.JSONObject) r3)
            if (r2 == 0) goto L_0x00c4
            r2.b(r0)
            r2.a(r1)
            r7.a((com.miui.antivirus.result.C0244g) r2)
            goto L_0x00c4
        L_0x009a:
            java.lang.String r10 = "004"
            boolean r2 = r10.equals(r2)
            if (r2 == 0) goto L_0x00c7
            boolean r2 = android.text.TextUtils.isEmpty(r14)
            if (r2 == 0) goto L_0x00b3
            com.miui.securitycenter.Application r2 = com.miui.securitycenter.Application.d()
            r6 = 2131756597(0x7f100635, float:1.9144106E38)
            java.lang.String r14 = r2.getString(r6)
        L_0x00b3:
            com.miui.antivirus.result.r r2 = new com.miui.antivirus.result.r
            r2.<init>(r3)
            r2.b(r14)
            r2.b(r0)
            r2.a(r1)
            a((com.miui.antivirus.result.C0247j) r7, (com.miui.antivirus.result.C0246i) r8, (com.miui.antivirus.result.C0244g) r2)
        L_0x00c4:
            r4 = 1
            goto L_0x01a3
        L_0x00c7:
            java.lang.String r0 = "rowType"
            java.lang.String r0 = r6.optString(r0)
            java.lang.String r1 = "card"
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x00c4
            int r0 = r6.optInt(r9)
            boolean r0 = com.miui.antivirus.result.C0246i.a((int) r0)
            if (r0 != 0) goto L_0x00e0
            goto L_0x00c4
        L_0x00e0:
            java.lang.String r0 = "list"
            org.json.JSONArray r0 = r6.optJSONArray(r0)
            com.miui.antivirus.result.i r9 = new com.miui.antivirus.result.i
            r9.<init>(r6)
            if (r0 == 0) goto L_0x013f
            int r1 = r0.length()
            if (r1 <= 0) goto L_0x013f
            int r1 = r9.e()
            r2 = 5
            if (r1 != r2) goto L_0x011f
            com.miui.antivirus.result.q r1 = new com.miui.antivirus.result.q
            r1.<init>()
            r2 = 1001(0x3e9, float:1.403E-42)
            r1.a((int) r2)
            a((org.json.JSONArray) r0, (com.miui.antivirus.result.q) r1)
            int r0 = r1.d()
            r2 = 3
            if (r0 < r2) goto L_0x011d
            r10 = 0
            r1.b(r10)
            r0 = 1
            r1.a(r0)
            r7.a((com.miui.antivirus.result.C0244g) r9)
            r7.a((com.miui.antivirus.result.C0244g) r1)
            goto L_0x013f
        L_0x011d:
            r10 = 0
            goto L_0x013f
        L_0x011f:
            r10 = 0
            boolean r1 = r9.h()
            if (r1 == 0) goto L_0x0129
            r7.a((com.miui.antivirus.result.C0244g) r9)
        L_0x0129:
            java.lang.String r1 = "title"
            java.lang.String r16 = r6.optString(r1)
            r1 = r18
            r2 = r19
            r3 = r20
            r4 = r16
            r5 = r9
            r10 = r6
            r6 = r23
            a(r0, r1, r2, r3, r4, r5, r6)
            goto L_0x0140
        L_0x013f:
            r10 = r6
        L_0x0140:
            boolean r0 = r9.g()
            if (r0 == 0) goto L_0x00c4
            java.lang.String r0 = "module"
            org.json.JSONArray r0 = r10.optJSONArray(r0)
            if (r0 == 0) goto L_0x00c4
            int r0 = r0.length()
            r1 = 1
            if (r0 != r1) goto L_0x0166
            com.miui.antivirus.result.h r0 = new com.miui.antivirus.result.h
            r0.<init>(r10)
            java.lang.String r1 = r9.c()
            r0.a(r1)
            r7.a((com.miui.antivirus.result.C0244g) r0)
            goto L_0x00c4
        L_0x0166:
            r4 = r1
            goto L_0x01a3
        L_0x0168:
            if (r13 != 0) goto L_0x0174
            int r2 = r3.optInt(r9)
            r4 = 70
            if (r2 != r4) goto L_0x0174
            goto L_0x00c4
        L_0x0174:
            if (r23 == 0) goto L_0x00c4
            int r2 = f2838b
            r4 = 1
            if (r2 == r4) goto L_0x017c
            r5 = 2
        L_0x017c:
            int r2 = f2838b
            int r2 = r2 + r4
            f2838b = r2
            int r2 = f2838b
            int r5 = f2837a
            int r5 = r5 + r4
            if (r2 != r5) goto L_0x018a
            f2838b = r4
        L_0x018a:
            java.lang.String r2 = ""
            com.miui.antivirus.result.f r3 = com.miui.antivirus.result.C0243f.a((org.json.JSONObject) r3, (java.lang.String) r2)
            if (r3 == 0) goto L_0x01a3
            java.lang.String r5 = r7.f
            r3.d((java.lang.String) r5)
            r3.b(r0)
            r3.a(r1)
            r3.a((java.lang.String) r2)
            a((com.miui.antivirus.result.C0247j) r7, (com.miui.antivirus.result.C0246i) r8, (com.miui.antivirus.result.C0244g) r3)
        L_0x01a3:
            int r13 = r13 + 1
            r10 = r4
            goto L_0x0018
        L_0x01a8:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.antivirus.result.C0247j.a(org.json.JSONArray, java.util.ArrayList, java.util.Set, com.miui.antivirus.result.j, java.lang.String, com.miui.antivirus.result.i, boolean):void");
    }

    public String a() {
        return this.e;
    }

    public void a(String str) {
        this.e = str;
    }

    public void a(List<C0244g> list) {
        this.f2839c = list;
    }

    public List<C0244g> b() {
        return this.f2839c;
    }

    public void b(String str) {
        this.f2840d = str;
    }

    public String c() {
        return this.h;
    }

    public boolean d() {
        return this.g;
    }
}
