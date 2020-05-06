package com.miui.optimizemanage.memoryclean;

import android.content.pm.ApplicationInfo;
import b.b.c.j.B;
import b.b.o.b.a.a;
import com.miui.securitycenter.utils.f;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class i {
    public static List<a> a() {
        int i;
        ArrayList arrayList = new ArrayList();
        if (!B.g()) {
            a(arrayList, 0);
            i = 999;
        } else {
            i = B.c();
        }
        a(arrayList, i);
        return arrayList;
    }

    private static void a(ArrayList<a> arrayList, int i) {
        HashMap hashMap = new HashMap();
        List<ApplicationInfo> a2 = a.a(0, i);
        if (a2 != null) {
            for (ApplicationInfo next : a2) {
                hashMap.put(next.packageName, Integer.valueOf(next.uid));
            }
        }
        for (String str : f.a(i)) {
            a aVar = new a();
            aVar.f5950a = str;
            Integer num = (Integer) hashMap.get(aVar.f5950a);
            if (num != null) {
                aVar.f5951b = num.intValue();
                arrayList.add(aVar);
            }
        }
    }
}
