package com.miui.powercenter.legacypowerrank;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.j.B;
import b.b.o.b.a.a;
import b.b.o.g.c;
import com.miui.networkassistant.config.Constants;
import com.miui.powercenter.utils.j;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import miui.securitycenter.powercenter.BatterySipper;

public class i {

    /* renamed from: a  reason: collision with root package name */
    private static List<BatteryData> f7098a = new ArrayList();

    /* renamed from: b  reason: collision with root package name */
    private static List<BatteryData> f7099b = new ArrayList();

    /* renamed from: c  reason: collision with root package name */
    private static double f7100c = 0.0d;

    /* renamed from: d  reason: collision with root package name */
    private static double f7101d = 0.0d;

    private static BatteryData a(List<BatteryData> list, BatteryData batteryData) {
        for (BatteryData next : list) {
            if (next.uid == batteryData.uid) {
                return next;
            }
        }
        return null;
    }

    public static synchronized List<BatteryData> a() {
        List<BatteryData> a2;
        synchronized (i.class) {
            a2 = a(f7098a);
        }
        return a2;
    }

    private static List<BatterySipper> a(Object obj) {
        try {
            return (List) c.a(obj, List.class, "getSystemAppUsageList", (Class<?>[]) null, new Object[0]);
        } catch (Exception unused) {
            return new ArrayList();
        }
    }

    private static List<BatteryData> a(List<BatteryData> list) {
        ArrayList arrayList = new ArrayList();
        for (BatteryData batteryData : list) {
            arrayList.add(new BatteryData(batteryData));
        }
        return arrayList;
    }

    private static List<BatteryData> a(List<BatteryData> list, List<BatteryData> list2) {
        ArrayList arrayList = new ArrayList();
        BatteryData batteryData = new BatteryData();
        BatteryData batteryData2 = new BatteryData();
        if (!(list2 != null && list2.isEmpty())) {
            for (BatteryData add : list2) {
                arrayList.add(add);
            }
            for (BatteryData next : list) {
                if (a(next.uid) && UserHandle.getAppId(next.getUid()) != 1000) {
                    if ("dex2oat".equals(next.name)) {
                        if (TextUtils.isEmpty(batteryData2.name)) {
                            batteryData2.name = next.name;
                            batteryData2.drainType = next.drainType;
                        }
                        batteryData2.add(next);
                        Log.i("PowerRankHelperHolder", "dex2oat uid " + next.uid);
                    } else {
                        BatteryData a2 = a((List<BatteryData>) arrayList, next);
                        if (a2 == null) {
                            arrayList.add(next);
                        } else {
                            a2.add(next);
                        }
                    }
                }
            }
        } else {
            for (BatteryData next2 : list) {
                if (a(next2.uid)) {
                    if (b(next2.getUid())) {
                        if (UserHandle.getAppId(next2.getUid()) == 1000) {
                            batteryData.name = Application.d().getResources().getString(R.string.cpu_usage_android_system);
                            batteryData.uid = next2.getUid();
                            batteryData.drainType = next2.drainType;
                            batteryData.defaultPackageName = Constants.System.ANDROID_PACKAGE_NAME;
                        }
                        batteryData.add(next2);
                    } else if ("dex2oat".equals(next2.name)) {
                        if (TextUtils.isEmpty(batteryData2.name)) {
                            batteryData2.name = next2.name;
                            batteryData2.drainType = next2.drainType;
                        }
                        batteryData2.add(next2);
                        Log.i("PowerRankHelperHolder", "dex2oat uid " + next2.uid);
                    } else {
                        BatteryData a3 = a((List<BatteryData>) arrayList, next2);
                        if (a3 == null) {
                            arrayList.add(next2);
                        } else {
                            a3.add(next2);
                        }
                    }
                }
            }
            if (batteryData.value > 0.0d) {
                if (TextUtils.isEmpty(batteryData.name)) {
                    batteryData.name = Application.d().getResources().getString(R.string.cpu_usage_android_system);
                    batteryData.uid = 1000;
                    batteryData.drainType = 6;
                }
                arrayList.add(batteryData);
            }
        }
        if (batteryData2.value > 0.0d) {
            arrayList.add(batteryData2);
        }
        Collections.sort(arrayList);
        return arrayList;
    }

    private static boolean a(int i) {
        return j.c() ? j.a(i) == B.j() : j.a() == 0 || j.a(i) == 0 || j.a(i) == 999;
    }

    private static boolean a(List<ApplicationInfo> list, String str, int i) {
        if (list == null) {
            return false;
        }
        for (ApplicationInfo next : list) {
            if (next.uid == i && next.packageName.equals(str)) {
                return true;
            }
        }
        return false;
    }

    public static synchronized List<BatteryData> b() {
        List<BatteryData> a2;
        synchronized (i.class) {
            a2 = a(f7099b);
        }
        return a2;
    }

    private static List<BatteryData> b(List<BatterySipper> list) {
        ArrayList arrayList = new ArrayList();
        for (BatterySipper batteryData : list) {
            arrayList.add(new BatteryData(batteryData));
        }
        return arrayList;
    }

    private static boolean b(int i) {
        return i >= 1000 && i < 10000;
    }

    public static synchronized double c() {
        double d2;
        synchronized (i.class) {
            d2 = f7100c;
        }
        return d2;
    }

    public static synchronized void d() {
        String str;
        String str2;
        synchronized (i.class) {
            Log.i("PowerRankHelperHolder", "refreshStats begin");
            try {
                Object a2 = c.a(Class.forName("miui.securitycenter.powercenter.PowerRankHelper"), (Class<?>[]) new Class[]{Context.class}, Application.d());
                c.a(a2, (Class) null, "refreshStats", (Class<?>[]) null, new Object[0]);
                f7098a.clear();
                f7098a.addAll(a(b((List<BatterySipper>) (List) c.a(a2, List.class, "getAppUsageList", (Class<?>[]) null, new Object[0])), b(a(a2))));
                Iterator<BatteryData> it = f7098a.iterator();
                List<ApplicationInfo> list = null;
                while (it.hasNext()) {
                    BatteryData next = it.next();
                    if (B.c(next.uid) == 999) {
                        if (list == null && !B.g()) {
                            list = a.a(0, 999);
                        }
                        if (!a(list, next.getPackageName(), next.getUid())) {
                            it.remove();
                        }
                    }
                }
                f7099b.clear();
                f7099b.addAll(b((List<BatterySipper>) (List) c.a(a2, List.class, "getMiscUsageList", (Class<?>[]) null, new Object[0])));
                double d2 = 0.0d;
                for (BatteryData batteryData : f7098a) {
                    d2 += batteryData.value;
                }
                f7101d = 0.0d;
                for (BatteryData batteryData2 : f7099b) {
                    f7101d += batteryData2.value;
                }
                f7100c = d2 + f7101d;
            } catch (NullPointerException e) {
                e = e;
                str = "PowerRankHelperHolder";
                str2 = "refreshStats";
                Log.e(str, str2, e);
                Log.i("PowerRankHelperHolder", "refreshStats end");
            } catch (OutOfMemoryError e2) {
                e = e2;
                str = "PowerRankHelperHolder";
                str2 = "refreshStats";
                Log.e(str, str2, e);
                Log.i("PowerRankHelperHolder", "refreshStats end");
            } catch (Exception e3) {
                e = e3;
                str = "PowerRankHelperHolder";
                str2 = "refreshStats";
                Log.e(str, str2, e);
                Log.i("PowerRankHelperHolder", "refreshStats end");
            }
            Log.i("PowerRankHelperHolder", "refreshStats end");
        }
    }
}
