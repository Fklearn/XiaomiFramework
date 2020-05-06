package com.miui.applicationlock.a;

import java.util.HashMap;
import java.util.Map;

class e implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ String f3246a;

    e(String str) {
        this.f3246a = str;
    }

    public void run() {
        HashMap hashMap = new HashMap(1);
        hashMap.put("package_name", this.f3246a);
        h.b("locked_app_name", (Map<String, String>) hashMap);
    }
}
