package com.miui.securityscan.a;

import android.content.Context;
import com.miui.gamebooster.m.Z;
import java.util.HashMap;

/* renamed from: com.miui.securityscan.a.d  reason: case insensitive filesystem */
class C0538d implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f7580a;

    C0538d(Context context) {
        this.f7580a = context;
    }

    public void run() {
        boolean b2 = Z.b(this.f7580a, (String) null);
        HashMap hashMap = new HashMap(1);
        hashMap.put("game_show", b2 ? "old_user" : "new_user");
        G.d("game_homepage_action", hashMap);
    }
}
