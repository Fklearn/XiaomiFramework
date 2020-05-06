package com.miui.powercenter.quickoptimize;

import android.content.Context;
import com.miui.powercenter.quickoptimize.C0530i;
import com.miui.powercenter.quickoptimize.z;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class y implements C0530i.b {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f7272a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ z.a f7273b;

    y(Context context, z.a aVar) {
        this.f7272a = context;
        this.f7273b = aVar;
    }

    public void a(Map<Integer, List<String>> map) {
        List a2 = z.c(this.f7272a);
        List list = map.get(0);
        List list2 = map.get(1);
        List list3 = map.get(2);
        List a3 = z.b();
        Iterator it = a2.iterator();
        while (it.hasNext()) {
            String str = (String) it.next();
            if ((list != null && list.contains(str)) || ((list2 != null && list2.contains(str)) || ((list3 != null && list3.contains(str)) || a3.contains(str)))) {
                it.remove();
            }
        }
        this.f7273b.a(a2);
    }
}
