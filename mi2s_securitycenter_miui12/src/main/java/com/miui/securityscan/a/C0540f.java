package com.miui.securityscan.a;

import java.util.HashMap;

/* renamed from: com.miui.securityscan.a.f  reason: case insensitive filesystem */
class C0540f implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ String f7583a;

    C0540f(String str) {
        this.f7583a = str;
    }

    public void run() {
        HashMap hashMap = new HashMap(1);
        hashMap.put("module_click", this.f7583a);
        G.d("slide_down_action_news", hashMap);
    }
}
