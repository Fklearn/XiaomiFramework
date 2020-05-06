package com.miui.securityscan.a;

import java.util.HashMap;

/* renamed from: com.miui.securityscan.a.c  reason: case insensitive filesystem */
class C0537c implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ String f7579a;

    C0537c(String str) {
        this.f7579a = str;
    }

    public void run() {
        HashMap hashMap = new HashMap(1);
        hashMap.put("main_module", this.f7579a);
        G.d("homepage_click_new", hashMap);
    }
}
