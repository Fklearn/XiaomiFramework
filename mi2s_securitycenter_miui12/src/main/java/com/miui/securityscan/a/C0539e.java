package com.miui.securityscan.a;

import android.content.Context;
import android.text.TextUtils;
import com.miui.common.card.GridFunctionData;
import java.util.HashMap;
import java.util.List;

/* renamed from: com.miui.securityscan.a.e  reason: case insensitive filesystem */
class C0539e implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ List f7581a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Context f7582b;

    C0539e(List list, Context context) {
        this.f7581a = list;
        this.f7582b = context;
    }

    public void run() {
        for (GridFunctionData gridFunctionData : this.f7581a) {
            String statKey = gridFunctionData.getStatKey();
            if (!TextUtils.isEmpty(statKey)) {
                HashMap hashMap = new HashMap(1);
                hashMap.put("module_show", statKey);
                G.d("slide_down_action_f", hashMap);
            }
            if ("#Intent;action=com.miui.gamebooster.action.ACCESS_MAINACTIVITY;S.jump_target=gamebox;end".equals(gridFunctionData.getAction())) {
                G.d(this.f7582b);
            }
        }
    }
}
