package com.miui.securityscan.a;

import com.miui.common.card.models.BaseCardModel;
import com.miui.firstaidkit.b.h;
import com.miui.securityscan.model.AbsModel;
import java.util.HashMap;

class y implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ BaseCardModel f7598a;

    y(BaseCardModel baseCardModel) {
        this.f7598a = baseCardModel;
    }

    public void run() {
        BaseCardModel baseCardModel = this.f7598a;
        if (baseCardModel instanceof h) {
            AbsModel a2 = ((h) baseCardModel).a();
            HashMap hashMap = new HashMap(1);
            hashMap.put("module_show", a2.getTrackStr());
            G.d("firstaidkit_resultpage_function", hashMap);
        }
    }
}
