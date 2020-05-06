package com.miui.powercenter.f;

import android.content.Context;
import android.text.TextUtils;
import com.miui.powercenter.legacypowerrank.BatteryData;
import com.miui.powercenter.legacypowerrank.i;
import java.util.ArrayList;
import java.util.List;

public class b {
    public static List<a> a(Context context) {
        return a(context, 3, true);
    }

    private static List<a> a(Context context, int i, boolean z) {
        i.d();
        List<BatteryData> a2 = i.a();
        ArrayList arrayList = new ArrayList();
        int i2 = 0;
        for (int i3 = 0; i3 < a2.size() && i2 < i; i3++) {
            if (!z || ((a2.get(i3) == null || a2.get(i3).uid != 1000) && !com.miui.powercenter.utils.b.b(context, a2.get(i3).getPackageName()))) {
                double value = (a2.get(i3).getValue() / i.c()) * 100.0d;
                if (value < 1.0d) {
                    break;
                }
                String a3 = com.miui.powercenter.legacypowerrank.b.a(context, a2.get(i3));
                if (!TextUtils.isEmpty(a3)) {
                    a aVar = new a();
                    aVar.f7063b = a3;
                    aVar.f7062a = a2.get(i3).getPackageName();
                    aVar.f7064c = value;
                    aVar.f7065d = com.miui.powercenter.legacypowerrank.b.a(a2.get(i3));
                    aVar.e = a2.get(i3).getUid();
                    arrayList.add(aVar);
                    i2++;
                }
            }
        }
        return arrayList;
    }
}
