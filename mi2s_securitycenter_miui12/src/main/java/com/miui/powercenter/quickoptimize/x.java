package com.miui.powercenter.quickoptimize;

import android.os.Handler;
import android.os.Looper;
import com.miui.powercenter.quickoptimize.C0530i;
import com.miui.powercenter.quickoptimize.z;
import com.miui.securitycenter.memory.MemoryModel;
import java.util.ArrayList;
import java.util.List;

class x implements C0530i.a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ z.a f7271a;

    x(z.a aVar) {
        this.f7271a = aVar;
    }

    public void a(List<MemoryModel> list) {
        ArrayList arrayList = new ArrayList();
        if (list != null) {
            List a2 = z.b();
            for (MemoryModel next : list) {
                if (!next.getLockState().get(1) && !a2.contains(next.getPackageName())) {
                    arrayList.add(next.getPackageName());
                }
            }
        }
        new Handler(Looper.getMainLooper()).post(new w(this, arrayList));
    }
}
