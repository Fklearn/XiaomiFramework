package com.miui.securityscan.a;

import android.content.Context;
import com.miui.gamebooster.m.Z;
import java.util.HashMap;

class F implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f7566a;

    F(Context context) {
        this.f7566a = context;
    }

    public void run() {
        boolean b2 = Z.b(this.f7566a, (String) null);
        HashMap hashMap = new HashMap(1);
        hashMap.put("game_click", b2 ? "old_user" : "new_user");
        G.d("game_homepage_action", hashMap);
    }
}
