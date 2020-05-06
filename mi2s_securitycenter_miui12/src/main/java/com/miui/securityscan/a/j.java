package com.miui.securityscan.a;

import java.util.HashMap;
import java.util.Map;

class j implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ long f7588a;

    j(long j) {
        this.f7588a = j;
    }

    public void run() {
        if (G.a(this.f7588a)) {
            HashMap hashMap = new HashMap(1);
            hashMap.put("before_slide_down", String.valueOf(this.f7588a));
            G.c("new_homepage_stay_time1", (Map<String, String>) hashMap);
        }
    }
}
