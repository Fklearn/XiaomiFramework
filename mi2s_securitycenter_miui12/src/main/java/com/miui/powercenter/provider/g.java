package com.miui.powercenter.provider;

import android.util.Log;
import com.miui.powercenter.quickoptimize.C0530i;
import java.util.List;
import java.util.Map;

class g implements C0530i.b {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ PowerSaveService f7182a;

    g(PowerSaveService powerSaveService) {
        this.f7182a = powerSaveService;
    }

    public void a(Map<Integer, List<String>> map) {
        List list = map.get(2);
        if (list != null) {
            this.f7182a.g.clear();
            this.f7182a.g.addAll(list);
        }
        Log.i("PowerSaveService", "cloud data update " + this.f7182a.g.size());
    }
}
