package com.miui.gamebooster.m;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.util.Log;
import b.b.c.f.a;
import b.b.c.j.x;
import com.miui.common.persistence.b;
import com.miui.earthquakewarning.Constants;
import com.miui.gamebooster.model.C0398d;
import com.miui.gamebooster.provider.a;
import com.miui.powercenter.utils.s;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miui.os.Build;
import miui.util.IOUtils;

public class U {
    public static long a(Context context) {
        return context.getSharedPreferences("game_booster_power", 0).getLong("game_booster_power_val", 0);
    }

    public static void a(Context context, String str) {
        ArrayList arrayList = new ArrayList();
        b(context, arrayList);
        a(context, (ArrayList<String>) arrayList);
        Log.i("PermissionUtils", "doclear");
        String a2 = C0384o.a("android.content.SystemIntent", "ACTION_SYSTEMUI_TASK_MANAGER_CLEAR");
        if (a2 != null) {
            Intent intent = new Intent(a2);
            arrayList.add("com.miui.securitycenter");
            arrayList.add(Constants.SECURITY_ADD_PACKAGE);
            arrayList.add("com.miui.vpnsdkmanager");
            arrayList.add("com.miui.screenrecorder");
            arrayList.add("com.xiaomi.gamecenter");
            arrayList.add(str);
            intent.putExtra("clean_type", 0);
            intent.putStringArrayListExtra("protected_pkgnames", arrayList);
            context.sendBroadcast(intent);
        }
    }

    private static void a(Context context, ArrayList<String> arrayList) {
        Cursor cursor;
        try {
            cursor = a.a(context, 0);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    try {
                        arrayList.add(cursor.getString(cursor.getColumnIndex("package_name")));
                    } catch (Throwable th) {
                        th = th;
                        IOUtils.closeQuietly(cursor);
                        throw th;
                    }
                }
            }
            IOUtils.closeQuietly(cursor);
        } catch (Throwable th2) {
            th = th2;
            cursor = null;
            IOUtils.closeQuietly(cursor);
            throw th;
        }
    }

    public static String b(Context context) {
        return s.d(context, a(context));
    }

    private static void b(Context context, ArrayList<String> arrayList) {
        Cursor cursor;
        try {
            cursor = a.a(context, 1);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    try {
                        arrayList.add(cursor.getString(cursor.getColumnIndex("package_name")));
                    } catch (Throwable th) {
                        th = th;
                        IOUtils.closeQuietly(cursor);
                        throw th;
                    }
                }
            }
            IOUtils.closeQuietly(cursor);
        } catch (Throwable th2) {
            th = th2;
            cursor = null;
            IOUtils.closeQuietly(cursor);
            throw th;
        }
    }

    public static void c(Context context) {
        P.a(context);
        if (!Build.IS_INTERNATIONAL_BUILD) {
            P.c(context);
            P.d(context);
        }
        if (C0388t.l()) {
            P.b(context);
        }
        ArrayList<ApplicationInfo> arrayList = new ArrayList<>();
        ArrayList arrayList2 = new ArrayList();
        com.miui.gamebooster.c.a.a(context);
        PackageManager packageManager = context.getPackageManager();
        if (com.miui.gamebooster.c.a.e() && com.miui.gamebooster.c.a.f()) {
            C0391w.a(context);
            C0393y.a(packageManager, (List<ApplicationInfo>) arrayList);
            for (ApplicationInfo applicationInfo : arrayList) {
                if (x.a(applicationInfo)) {
                    arrayList2.add(applicationInfo);
                }
            }
            ArrayList<C0398d> a2 = C0393y.a(context, packageManager, (List<ApplicationInfo>) arrayList2);
            ArrayList arrayList3 = new ArrayList();
            Iterator<C0398d> it = a2.iterator();
            while (it.hasNext()) {
                arrayList3.add(it.next().b().packageName);
            }
            b.b("gb_added_games", (ArrayList<String>) arrayList3);
            com.miui.gamebooster.c.a.Q(false);
            C0390v.a(context).a((a.C0027a) new T(arrayList3, context));
        }
    }
}
