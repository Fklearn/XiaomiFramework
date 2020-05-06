package com.miui.applicationlock.a;

import java.util.HashMap;
import java.util.Map;

class d implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ String f3245a;

    d(String str) {
        this.f3245a = str;
    }

    public void run() {
        HashMap hashMap = new HashMap(1);
        hashMap.put("type", this.f3245a);
        h.b("applock_guide_notification", (Map<String, String>) hashMap);
    }
}
