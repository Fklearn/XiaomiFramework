package com.miui.securityscan.a;

import java.util.HashMap;
import java.util.Map;

class k implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ long f7589a;

    k(long j) {
        this.f7589a = j;
    }

    public void run() {
        if (G.a(this.f7589a)) {
            HashMap hashMap = new HashMap(1);
            hashMap.put("after_slide_down", String.valueOf(this.f7589a));
            G.c("new_homepage_stay_time1", (Map<String, String>) hashMap);
        }
    }
}
