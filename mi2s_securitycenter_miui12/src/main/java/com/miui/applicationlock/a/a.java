package com.miui.applicationlock.a;

import java.util.HashMap;
import java.util.Map;

class a implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ String f3242a;

    a(String str) {
        this.f3242a = str;
    }

    public void run() {
        HashMap hashMap = new HashMap(1);
        hashMap.put("enter_way", this.f3242a);
        h.b("enter_applock_way", (Map<String, String>) hashMap);
    }
}
