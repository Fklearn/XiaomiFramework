package com.miui.applicationlock.a;

import java.util.HashMap;
import java.util.Map;

class f implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ String f3247a;

    f(String str) {
        this.f3247a = str;
    }

    public void run() {
        HashMap hashMap = new HashMap(1);
        hashMap.put("display_type", this.f3247a);
        h.b("applock_display", (Map<String, String>) hashMap);
    }
}
