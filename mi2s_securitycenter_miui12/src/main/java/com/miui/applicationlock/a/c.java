package com.miui.applicationlock.a;

import java.util.HashMap;
import java.util.Map;

class c implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ String f3244a;

    c(String str) {
        this.f3244a = str;
    }

    public void run() {
        HashMap hashMap = new HashMap(1);
        hashMap.put("type", this.f3244a);
        h.b("change_password_type", (Map<String, String>) hashMap);
    }
}
