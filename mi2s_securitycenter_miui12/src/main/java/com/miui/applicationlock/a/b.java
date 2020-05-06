package com.miui.applicationlock.a;

import java.util.HashMap;
import java.util.Map;

class b implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ String f3243a;

    b(String str) {
        this.f3243a = str;
    }

    public void run() {
        HashMap hashMap = new HashMap(1);
        hashMap.put("enter_way", this.f3243a);
        h.b("enter_first_applock_way", (Map<String, String>) hashMap);
    }
}
