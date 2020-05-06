package com.miui.optimizemanage.memoryclean;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import b.b.c.j.B;
import b.b.o.b.a.a;
import com.miui.optimizemanage.d.e;
import java.util.ArrayList;
import java.util.List;

public class b {
    public static int a(Context context) {
        int i = 0;
        for (c cVar : a(context, i.a())) {
            if (cVar.f5954c) {
                i++;
            }
        }
        return i;
    }

    public static List<c> a(Context context, List<a> list) {
        ArrayList arrayList = new ArrayList();
        List<String> a2 = e.a(context);
        if (!B.g()) {
            List<ApplicationInfo> a3 = a.a(0, 0);
            if (a3 != null) {
                for (ApplicationInfo a4 : a3) {
                    a(arrayList, a2, list, a4);
                }
            }
            List<ApplicationInfo> a5 = a.a(0, 999);
            if (a5 != null) {
                for (ApplicationInfo a6 : a5) {
                    a(arrayList, a2, list, a6);
                }
            }
        } else {
            List<ApplicationInfo> a7 = a.a(0, B.c());
            if (a7 != null) {
                for (ApplicationInfo a8 : a7) {
                    a(arrayList, a2, list, a8);
                }
            }
        }
        return arrayList;
    }

    private static void a(ArrayList<c> arrayList, List<String> list, List<a> list2, ApplicationInfo applicationInfo) {
        if (!((applicationInfo.flags & 1) != 0) || list.contains(applicationInfo.packageName)) {
            c cVar = new c();
            cVar.f5953b = applicationInfo.packageName;
            cVar.f5952a = applicationInfo.uid;
            cVar.f5954c = false;
            for (a next : list2) {
                if (next.f5950a.equals(cVar.f5953b) && next.f5951b == cVar.f5952a) {
                    cVar.f5954c = true;
                }
            }
            arrayList.add(cVar);
        }
    }

    public static List<a> b(Context context) {
        List<String> a2 = e.a(context);
        List<a> a3 = i.a();
        ArrayList arrayList = new ArrayList();
        for (a next : a3) {
            try {
                boolean z = true;
                if ((context.getPackageManager().getApplicationInfo(next.f5950a, 0).flags & 1) == 0) {
                    z = false;
                }
                if (!z || a2.contains(next.f5950a)) {
                    arrayList.add(next);
                }
            } catch (Exception unused) {
            }
        }
        return arrayList;
    }
}
