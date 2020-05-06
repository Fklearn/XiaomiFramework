package com.miui.antispam.service.b;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import b.b.c.h.j;
import com.miui.antispam.db.a.c;
import com.miui.antispam.db.b.a;
import com.miui.antispam.db.d;
import com.miui.antispam.service.b;
import com.miui.luckymoney.config.Constants;
import com.miui.securityscan.i.k;
import com.xiaomi.stat.MiStat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import miui.os.Build;
import org.json.JSONArray;
import org.json.JSONObject;

public class b extends com.miui.antispam.service.b {
    public b(Context context, b.a aVar) {
        super(context, aVar);
    }

    public String a() {
        return "CloudPhoneList";
    }

    public boolean d() {
        JSONArray jSONArray;
        char c2;
        List list;
        Object aVar;
        List list2;
        Object aVar2;
        try {
            HashMap hashMap = new HashMap();
            hashMap.put(Constants.JSON_KEY_DATA_VERSION, Long.toString(d.a()));
            String a2 = k.a((Map<String, String>) hashMap, "https://api.sec.miui.com/harassIntercept/intercepts", k.a.POST, "5cdd8678-cddf-4269-ab73-48387445bba5", new j("antispam_cloudphonelistservice"));
            if (TextUtils.isEmpty(a2)) {
                return false;
            }
            JSONObject jSONObject = new JSONObject(a2);
            long j = (long) jSONObject.getInt("l");
            if (d.a() < j) {
                SparseArray sparseArray = new SparseArray();
                sparseArray.put(0, new ArrayList());
                sparseArray.put(1, new ArrayList());
                sparseArray.put(2, new ArrayList());
                SparseArray sparseArray2 = new SparseArray();
                sparseArray2.put(0, new ArrayList());
                sparseArray2.put(1, new ArrayList());
                sparseArray2.put(2, new ArrayList());
                c cVar = new c();
                com.miui.antispam.db.a.b bVar = new com.miui.antispam.db.a.b();
                JSONArray jSONArray2 = jSONObject.getJSONArray("v");
                int i = 0;
                while (i < jSONArray2.length()) {
                    JSONObject jSONObject2 = jSONArray2.getJSONObject(i);
                    String optString = jSONObject2.optString("status");
                    int optInt = jSONObject2.optInt("type");
                    if (!"new".equals(optString)) {
                        jSONArray = jSONArray2;
                        if ("updated".equals(optString)) {
                            if (optInt != 1) {
                                c2 = 2;
                                if (optInt != 2) {
                                    i++;
                                    char c3 = c2;
                                    jSONArray2 = jSONArray;
                                } else if (bVar.a(jSONObject2.optString("uuid"))) {
                                    list2 = (List) sparseArray2.get(1);
                                    aVar2 = new a(jSONObject2.optString("uuid"), jSONObject2.optString(MiStat.Param.VALUE), Integer.valueOf(jSONObject2.optString("attributes")).intValue());
                                } else {
                                    list = (List) sparseArray2.get(0);
                                    aVar = new a(jSONObject2.optString("uuid"), jSONObject2.optString(MiStat.Param.VALUE), Integer.valueOf(jSONObject2.optString("attributes")).intValue());
                                }
                            } else if (cVar.a(jSONObject2.optString("uuid"))) {
                                list2 = (List) sparseArray.get(1);
                                aVar2 = new com.miui.antispam.db.b.b(jSONObject2.optString("uuid"), jSONObject2.optString(MiStat.Param.VALUE), Integer.valueOf(jSONObject2.optString("attributes").substring(0, 1)).intValue(), Integer.valueOf(jSONObject2.optString("attributes").substring(1, 2)).intValue());
                            } else {
                                list = (List) sparseArray.get(0);
                                aVar = new com.miui.antispam.db.b.b(jSONObject2.optString("uuid"), jSONObject2.optString(MiStat.Param.VALUE), Integer.valueOf(jSONObject2.optString("attributes").substring(0, 1)).intValue(), Integer.valueOf(jSONObject2.optString("attributes").substring(1, 2)).intValue());
                            }
                            list2.add(aVar2);
                            c2 = 2;
                            i++;
                            char c32 = c2;
                            jSONArray2 = jSONArray;
                        } else {
                            if ("deleted".equals(optString)) {
                                if (optInt == 1) {
                                    ((List) sparseArray.get(2)).add(new com.miui.antispam.db.b.b(jSONObject2.optString("uuid")));
                                } else {
                                    c2 = 2;
                                    if (optInt == 2) {
                                        ((List) sparseArray2.get(2)).add(new a(jSONObject2.optString("uuid")));
                                    }
                                    i++;
                                    char c322 = c2;
                                    jSONArray2 = jSONArray;
                                }
                            }
                            c2 = 2;
                            i++;
                            char c3222 = c2;
                            jSONArray2 = jSONArray;
                        }
                    } else if (optInt == 1) {
                        jSONArray = jSONArray2;
                        ((List) sparseArray.get(0)).add(new com.miui.antispam.db.b.b(jSONObject2.optString("uuid"), jSONObject2.optString(MiStat.Param.VALUE), Integer.valueOf(jSONObject2.optString("attributes").substring(0, 1)).intValue(), Integer.valueOf(jSONObject2.optString("attributes").substring(1, 2)).intValue()));
                        c2 = 2;
                        i++;
                        char c32222 = c2;
                        jSONArray2 = jSONArray;
                    } else {
                        jSONArray = jSONArray2;
                        c2 = 2;
                        if (optInt == 2) {
                            list = (List) sparseArray2.get(0);
                            aVar = new a(jSONObject2.optString("uuid"), jSONObject2.optString(MiStat.Param.VALUE), Integer.valueOf(jSONObject2.optString("attributes")).intValue());
                        } else {
                            i++;
                            char c322222 = c2;
                            jSONArray2 = jSONArray;
                        }
                    }
                    list.add(aVar);
                    c2 = 2;
                    i++;
                    char c3222222 = c2;
                    jSONArray2 = jSONArray;
                }
                cVar.a((SparseArray<List<com.miui.antispam.db.b.b>>) sparseArray);
                bVar.a((SparseArray<List<a>>) sparseArray2);
                d.a(j);
            }
            return true;
        } catch (Exception e) {
            Log.e("CloudPhoneListService", "Exception in pull cloud phone list:", e);
            return false;
        }
    }

    public void e() {
        if (!Build.IS_INTERNATIONAL_BUILD && d.d()) {
            b(true);
            b.b.c.j.d.a(new a(this));
        }
    }
}
