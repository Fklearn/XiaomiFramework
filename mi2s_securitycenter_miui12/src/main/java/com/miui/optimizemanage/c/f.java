package com.miui.optimizemanage.c;

import android.content.Context;
import b.b.c.h.j;
import com.google.android.exoplayer2.upstream.DataSchemeDataSource;
import com.miui.activityutil.o;
import com.miui.luckymoney.config.Constants;
import com.miui.optimizemanage.memoryclean.a;
import com.miui.securitycenter.R;
import com.miui.securityscan.M;
import com.miui.securityscan.c.b;
import com.miui.securityscan.cards.d;
import com.miui.securityscan.i.k;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import miui.cloud.CloudPushConstants;
import miui.os.Build;
import org.json.JSONArray;
import org.json.JSONObject;

public class f implements Serializable {

    /* renamed from: a  reason: collision with root package name */
    private static Object f5894a = new Object();

    /* renamed from: b  reason: collision with root package name */
    public static final List<d> f5895b = new ArrayList();

    /* renamed from: c  reason: collision with root package name */
    private String f5896c;

    /* renamed from: d  reason: collision with root package name */
    private String f5897d;
    private String e;
    private String f;
    private int g;
    private int h;
    private boolean i;
    private boolean j;
    private String k = "";
    private ArrayList<d> l = new ArrayList<>();

    static {
        f5895b.add(new o());
    }

    public static f a(JSONObject jSONObject) {
        f fVar;
        synchronized (f5894a) {
            fVar = new f();
            fVar.l.clear();
            fVar.g = 0;
            fVar.j = jSONObject.optBoolean("isOverseaChannel");
            fVar.k = jSONObject.optString("lang");
            fVar.f5896c = jSONObject.optString("channel");
            fVar.f5897d = jSONObject.optString(Constants.JSON_KEY_DATA_VERSION);
            fVar.e = jSONObject.optString("layoutId");
            fVar.f = jSONObject.optString("tn");
            fVar.h = jSONObject.optInt("status");
            fVar.i = jSONObject.optBoolean("forceRefresh");
            JSONArray optJSONArray = jSONObject.optJSONArray(DataSchemeDataSource.SCHEME_DATA);
            if (optJSONArray != null) {
                for (int i2 = 0; i2 < optJSONArray.length(); i2++) {
                    a(fVar, (l) null, optJSONArray.getJSONObject(i2));
                }
            }
            for (int i3 = 0; i3 < fVar.l.size(); i3++) {
                d dVar = fVar.l.get(i3);
                if (dVar instanceof l) {
                    List<d> d2 = ((l) dVar).d();
                    int size = d2.size();
                    for (int i4 = 0; i4 < size; i4++) {
                        d dVar2 = d2.get(i4);
                        if (dVar2 instanceof k) {
                            k kVar = (k) dVar2;
                            if (size == 1) {
                                kVar.c(true);
                            } else if (i4 == 0) {
                                kVar.c(true);
                            } else if (i4 == size - 1) {
                            }
                            kVar.a(true);
                        }
                    }
                }
            }
        }
        return fVar;
    }

    public static n a(Context context, List<a> list) {
        if (list.isEmpty()) {
            return null;
        }
        n nVar = new n();
        nVar.b(context.getString(R.string.om_locked_apps_summary));
        nVar.a(context.getString(R.string.btn_text_goto_setup));
        nVar.a(context, list);
        return nVar;
    }

    public static String a(Context context, Map<String, String> map) {
        String str;
        if (map == null) {
            map = new HashMap<>();
        }
        if (Build.IS_INTERNATIONAL_BUILD) {
            map.put("channel", "02-22");
            map.put("nt", o.f2310b);
        } else {
            map.put("channel", "01-22");
        }
        d.a(map);
        boolean l2 = M.l();
        if (!M.m()) {
            str = "2";
        } else if (l2) {
            map.put("setting", o.f2310b);
            return k.a(map, b.f7626a, new j("optimizemanage_omdatamodel"));
        } else {
            str = o.f2312d;
        }
        map.put("setting", str);
        return k.a(map, b.f7626a, new j("optimizemanage_omdatamodel"));
    }

    public static List<d> a(Context context) {
        ArrayList arrayList = new ArrayList();
        arrayList.addAll(f5895b);
        List<a> b2 = com.miui.optimizemanage.memoryclean.b.b(context);
        if (!b2.isEmpty()) {
            arrayList.add(a(context, b2));
            arrayList.add(new j());
        }
        if (!Build.IS_INTERNATIONAL_BUILD) {
            arrayList.add(new g("assets://img/ziqidongguanli.png", context.getString(R.string.title_of_auto_launch_manage), context.getString(R.string.tips_of_auto_launch_manage), context.getString(R.string.cpu_usage_view_action_btn_text), "miui.intent.action.OP_AUTO_START"));
            arrayList.add(new j());
        }
        arrayList.add(new g("assets://img/xiezai.png", context.getString(R.string.optimize_result_title_uninstall_apps), context.getString(R.string.tips_of_app_manage), context.getString(R.string.cpu_usage_view_action_btn_text), "miui.intent.action.GARBAGE_UNINSTALL_APPS"));
        arrayList.add(new j());
        arrayList.add(new g("drawable://2131231693", context.getString(R.string.activity_title_garbage_cleanup), context.getString(R.string.clear_garbage), context.getString(R.string.clear_immediately), "miui.intent.action.GARBAGE_CLEANUP"));
        arrayList.add(new j());
        return arrayList;
    }

    private static void a(f fVar, l lVar, JSONObject jSONObject) {
        d a2;
        String optString = jSONObject.optString("rowType");
        if (CloudPushConstants.XML_ITEM.equals(optString)) {
            String optString2 = jSONObject.optString("type");
            int optInt = jSONObject.optInt("template");
            JSONObject optJSONObject = jSONObject.optJSONObject(DataSchemeDataSource.SCHEME_DATA);
            if ("001".equals(optString2)) {
                fVar.a(fVar.b() + 1);
                int b2 = fVar.b();
                String str = b2 != 1 ? b2 != 2 ? "" : "1.306.1.8" : "1.306.1.7";
                i iVar = new i(optJSONObject, R.layout.result_template_ad_global_empty);
                iVar.c(str);
                iVar.d(optInt);
                fVar.l.add(iVar);
                if (lVar != null) {
                    lVar.a((d) iVar);
                    return;
                }
                return;
            }
            if ("002".equals(optString2)) {
                a2 = g.a(optInt, optJSONObject);
                if (a2 != null) {
                    fVar.l.add(a2);
                    if (lVar == null) {
                        return;
                    }
                } else {
                    return;
                }
            } else if ("003".equals(optString2)) {
                a2 = c.a(optInt, optJSONObject);
                if (a2 != null) {
                    fVar.l.add(a2);
                    if (lVar == null) {
                        return;
                    }
                } else {
                    return;
                }
            } else if ("004".equals(optString2)) {
                k kVar = new k(optJSONObject);
                fVar.l.add(kVar);
                if (lVar != null) {
                    lVar.a((d) kVar);
                    return;
                }
                return;
            } else if ("005".equals(optString2)) {
                fVar.l.add(new j());
                return;
            } else {
                return;
            }
            lVar.a(a2);
        } else if ("card".equals(optString)) {
            JSONArray optJSONArray = jSONObject.optJSONArray("list");
            l lVar2 = new l(jSONObject);
            if (optJSONArray != null && optJSONArray.length() > 0) {
                fVar.l.add(lVar2);
                for (int i2 = 0; i2 < optJSONArray.length(); i2++) {
                    a(fVar, lVar2, optJSONArray.getJSONObject(i2));
                }
            }
        }
    }

    public void a(int i2) {
        this.g = i2;
    }

    public boolean a() {
        String d2 = d();
        return !Build.IS_INTERNATIONAL_BUILD && !f() && d2 != null && d2.equalsIgnoreCase(Locale.getDefault().toString()) && "zh_CN".equalsIgnoreCase(Locale.getDefault().toString());
    }

    public int b() {
        return this.g;
    }

    public String c() {
        return this.f5897d;
    }

    public String d() {
        return this.k;
    }

    public List<d> e() {
        ArrayList arrayList;
        d dVar;
        synchronized (f5894a) {
            ArrayList arrayList2 = new ArrayList();
            for (int i2 = 0; i2 < this.l.size(); i2++) {
                d dVar2 = this.l.get(i2);
                if (dVar2 instanceof l) {
                    l lVar = (l) dVar2;
                    List<d> d2 = lVar.d();
                    if (d2.isEmpty() || !lVar.f()) {
                        arrayList2.add(dVar2);
                        if (!d2.isEmpty() && (dVar = d2.get(0)) != null) {
                            ((k) dVar).b(true);
                        }
                    }
                }
            }
            if (!arrayList2.isEmpty()) {
                this.l.removeAll(arrayList2);
            }
            ArrayList arrayList3 = new ArrayList();
            for (int size = this.l.size() - 1; size > 0; size--) {
                if ((this.l.get(size) instanceof j) && (this.l.get(size - 1) instanceof j)) {
                    arrayList3.add(this.l.get(size));
                }
            }
            if (!this.l.isEmpty() && (this.l.get(0) instanceof j)) {
                arrayList3.add(this.l.get(0));
            }
            this.l.removeAll(arrayList3);
            int size2 = this.l.size();
            if (size2 > 0) {
                int i3 = size2 - 1;
                if (this.l.get(i3) instanceof j) {
                    this.l.remove(i3);
                }
            }
            arrayList = new ArrayList(this.l);
        }
        return arrayList;
    }

    public boolean f() {
        return this.j;
    }
}
