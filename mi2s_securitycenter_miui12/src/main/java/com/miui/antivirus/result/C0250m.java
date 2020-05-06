package com.miui.antivirus.result;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;
import com.miui.applicationlock.c.o;
import com.miui.gamebooster.m.C0381l;
import com.miui.powercenter.quickoptimize.s;
import com.miui.powercenter.y;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.R;
import com.miui.securitycenter.p;
import com.miui.securityscan.M;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import miui.os.Build;

/* renamed from: com.miui.antivirus.result.m  reason: case insensitive filesystem */
public class C0250m {

    /* renamed from: a  reason: collision with root package name */
    public static final Application f2841a = Application.d();

    /* renamed from: b  reason: collision with root package name */
    private static final HashMap<String, String> f2842b = new HashMap<>();

    /* renamed from: c  reason: collision with root package name */
    public static HashMap<Integer, Boolean> f2843c = new C0249l();

    /* renamed from: d  reason: collision with root package name */
    static List<String> f2844d = new ArrayList();

    static {
        f2842b.put("http://sec-cdn.static.xiaomi.net/secStatic/icon/ziqidongguanli.png", "assets://img/ziqidongguanli.png");
        f2842b.put("https://sec-cdn.static.xiaomi.net/secStatic/proj/xiezai.png", "assets://img/xiezai.png");
    }

    public static C0247j a() {
        ArrayList<C0248k> e = e();
        ArrayList<C0248k> d2 = d();
        c();
        C0247j jVar = new C0247j();
        jVar.a("******************");
        jVar.b("01");
        ArrayList arrayList = new ArrayList();
        jVar.a((List<C0244g>) arrayList);
        Iterator<C0248k> it = e.iterator();
        while (it.hasNext()) {
            C0248k next = it.next();
            if (b(next.c())) {
                arrayList.add(next.clone());
            } else {
                while (true) {
                    if (d2.size() <= 0) {
                        break;
                    }
                    next = d2.remove(0);
                    if (b(next.c())) {
                        break;
                    }
                }
                arrayList.add(next.clone());
            }
        }
        return jVar;
    }

    public static final String a(int i) {
        return f2841a.getString(i);
    }

    public static String a(String str) {
        return f2842b.get(str);
    }

    public static boolean a(Context context) {
        if (p.a() < 1) {
            return false;
        }
        f2844d = o.f((Context) Application.d());
        Log.i("appsArrayList Number", String.valueOf(f2844d.size()));
        return f2844d.size() >= 4;
    }

    public static List<String> b() {
        if (f2844d.size() == 0) {
            f2844d = o.f((Context) Application.d());
        }
        return f2844d;
    }

    public static boolean b(int i) {
        if (i == 34) {
            return a((Context) Application.d());
        }
        switch (i) {
            case 42:
                return !Build.IS_INTERNATIONAL_BUILD && C0381l.b(Application.d());
            case 43:
                return !f2843c.get(new Integer(43)).booleanValue();
            case 44:
                return b((Context) Application.d());
            default:
                return true;
        }
    }

    public static boolean b(Context context) {
        s.a a2 = s.a();
        long currentTimeMillis = System.currentTimeMillis() - M.a(0);
        if (!(currentTimeMillis < 600000 && currentTimeMillis > 0) || !a2.f7256a || a2.f7257b != 0) {
            return c(context) >= y.e();
        }
        return false;
    }

    private static int c(Context context) {
        return context.registerReceiver((BroadcastReceiver) null, new IntentFilter("android.intent.action.BATTERY_CHANGED")).getIntExtra("temperature", 0) / 10;
    }

    public static void c() {
        for (Map.Entry<Integer, Boolean> value : f2843c.entrySet()) {
            value.setValue(false);
        }
    }

    private static ArrayList<C0248k> d() {
        return new ArrayList<>();
    }

    private static ArrayList<C0248k> e() {
        ArrayList<C0248k> arrayList = new ArrayList<>();
        if (!f2843c.get(43).booleanValue()) {
            C0248k kVar = new C0248k();
            kVar.a(43);
            kVar.d(a((int) R.string.activity_title_garbage_cleanup));
            kVar.c(a((int) R.string.clear_garbage));
            kVar.a(a((int) R.string.clear_immediately));
            kVar.b(2);
            kVar.c(3);
            kVar.b("drawable://2131231693");
            arrayList.add(kVar);
        }
        C0248k kVar2 = new C0248k();
        kVar2.a(25);
        kVar2.d(a((int) R.string.title_of_auto_launch_manage));
        kVar2.c(a((int) R.string.tips_of_auto_launch_manage));
        kVar2.a(a((int) R.string.go_to));
        kVar2.b(2);
        kVar2.c(3);
        kVar2.b("assets://img/ziqidongguanli.png");
        arrayList.add(kVar2);
        C0248k kVar3 = new C0248k();
        kVar3.a(28);
        kVar3.d(a((int) R.string.title_of_app_manage));
        kVar3.c(a((int) R.string.tips_of_app_manage));
        kVar3.a(a((int) R.string.go_to));
        kVar3.b(2);
        kVar3.c(3);
        kVar3.b("assets://img/xiezai.png");
        arrayList.add(kVar3);
        C0248k kVar4 = new C0248k();
        kVar4.a(44);
        kVar4.d(a((int) R.string.activity_title_power_manager));
        kVar4.c(a((int) R.string.summary_consume_power));
        kVar4.a(a((int) R.string.optimize_result_button_cooldown_now));
        kVar4.b(2);
        kVar4.c(3);
        kVar4.b("drawable://2131231690");
        arrayList.add(kVar4);
        return arrayList;
    }
}
