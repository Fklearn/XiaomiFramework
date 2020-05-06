package com.miui.powercenter.deepsave;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import b.b.c.d.C0184d;
import b.b.c.d.C0185e;
import b.b.c.d.p;
import b.b.c.d.q;
import com.miui.powercenter.a.a;
import com.miui.powercenter.c.c;
import com.miui.powercenter.deepsave.a.b;
import com.miui.powercenter.deepsave.a.d;
import com.miui.powercenter.deepsave.a.f;
import com.miui.powercenter.deepsave.a.h;
import com.miui.powercenter.deepsave.a.i;
import com.miui.powercenter.deepsave.a.k;
import com.miui.powercenter.deepsave.a.m;
import com.miui.powercenter.deepsave.a.o;
import com.miui.powercenter.utils.u;
import com.miui.securitycenter.Application;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miui.os.Build;
import org.json.JSONObject;

public class g {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public static ArrayList<C0185e> f7056a = new ArrayList<>();

    public static void a(String str) {
        new f(str).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }

    private static void a(ArrayList<C0185e> arrayList) {
        String str;
        Application d2 = Application.d();
        Iterator<C0185e> it = arrayList.iterator();
        while (true) {
            boolean z = false;
            while (it.hasNext()) {
                C0185e next = it.next();
                if (z) {
                    if (p.class.isInstance(next)) {
                        it.remove();
                    } else {
                        z = false;
                    }
                }
                boolean z2 = true;
                if (q.class.isInstance(next)) {
                    q qVar = (q) next;
                    if ("001".equals(qVar.b())) {
                        qVar.a((C0185e) new o());
                        str = "save_mode";
                    } else {
                        if ("002".equals(qVar.b())) {
                            if (a((Context) d2)) {
                                qVar.a((C0185e) new f());
                                str = "save_idea";
                            }
                        } else if ("003".equals(qVar.b())) {
                            qVar.a((C0185e) new com.miui.powercenter.deepsave.a.q());
                            str = "expend_top";
                        } else if (!"005".equals(qVar.b())) {
                            if ("006".equals(qVar.b())) {
                                qVar.a((C0185e) new d());
                            } else if ("007".equals(qVar.b())) {
                                qVar.a((C0185e) new m());
                                str = "app_smart_save";
                            } else if ("008".equals(qVar.b())) {
                                if (u.d()) {
                                    qVar.a((C0185e) new b());
                                    str = "auto_task";
                                }
                            } else if ("009".equals(qVar.b())) {
                                if (com.miui.powercenter.utils.o.n(d2)) {
                                    qVar.a((C0185e) new k());
                                    str = "extreme_save_mode";
                                }
                            }
                            z2 = z;
                        } else if (u.e()) {
                            qVar.a((C0185e) new h());
                            str = "power_on_off_plan";
                        }
                        it.remove();
                    }
                    a.e(str);
                    z2 = z;
                } else if (C0184d.class.isInstance(next) && Build.IS_INTERNATIONAL_BUILD) {
                    C0184d dVar = (C0184d) next;
                    if (!dVar.n()) {
                        C0184d a2 = b.b.c.d.o.a(0, (JSONObject) null, dVar.i(), dVar.k());
                        if (a2.n()) {
                            dVar.d(a2);
                        } else {
                            Log.d("DataModelManager", "international ad hide");
                        }
                    }
                }
                z = z2;
            }
            return;
        }
    }

    private static boolean a(Context context) {
        return !Build.IS_INTERNATIONAL_BUILD && b.b.c.j.f.b(context) && com.miui.securitycenter.h.i() && !e.b().a().isEmpty();
    }

    public static List<C0185e> b() {
        ArrayList<C0185e> arrayList;
        ArrayList arrayList2 = new ArrayList();
        if (!f7056a.isEmpty()) {
            arrayList2.addAll(c());
            a(f7056a);
            arrayList = f7056a;
        } else {
            arrayList2.addAll(c());
            arrayList = d();
        }
        arrayList2.addAll(arrayList);
        return arrayList2;
    }

    private static ArrayList<C0185e> c() {
        ArrayList<C0185e> arrayList = new ArrayList<>();
        arrayList.add(new i());
        a.e("top_card");
        return arrayList;
    }

    private static ArrayList<C0185e> d() {
        Application d2 = Application.d();
        ArrayList<C0185e> arrayList = new ArrayList<>();
        arrayList.add(new o());
        arrayList.add(new com.miui.powercenter.c.d());
        a.e("save_mode");
        if (com.miui.powercenter.utils.o.n(d2)) {
            arrayList.add(new k());
            arrayList.add(new com.miui.powercenter.c.d());
            a.e("extreme_save_mode");
        }
        arrayList.add(new m());
        arrayList.add(new com.miui.powercenter.c.d());
        a.e("app_smart_save");
        if (u.d()) {
            arrayList.add(new b());
            arrayList.add(new com.miui.powercenter.c.d());
            a.e("auto_task");
        }
        if (a((Context) d2)) {
            arrayList.add(new f());
            arrayList.add(new com.miui.powercenter.c.d());
            a.e("save_idea");
        }
        arrayList.add(new com.miui.powercenter.deepsave.a.q());
        arrayList.add(new com.miui.powercenter.c.d());
        a.e("expend_top");
        if (u.e()) {
            arrayList.add(new h());
            arrayList.add(new com.miui.powercenter.c.d());
            a.e("power_on_off_plan");
        }
        arrayList.add(new d());
        arrayList.add(new c());
        return arrayList;
    }
}
