package com.miui.securityscan.a;

import android.text.TextUtils;
import com.miui.common.card.GridFunctionData;
import java.util.HashMap;
import java.util.List;

class z implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ List f7599a;

    z(List list) {
        this.f7599a = list;
    }

    public void run() {
        for (GridFunctionData gridFunctionData : this.f7599a) {
            String dataId = gridFunctionData.getDataId();
            if (TextUtils.isEmpty(dataId)) {
                dataId = gridFunctionData.getStatKey();
            }
            if (!TextUtils.isEmpty(dataId)) {
                HashMap hashMap = new HashMap(1);
                hashMap.put("module_show", dataId);
                G.d("phone_manage_show_click", hashMap);
            }
        }
    }
}
