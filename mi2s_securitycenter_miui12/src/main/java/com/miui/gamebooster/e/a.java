package com.miui.gamebooster.e;

import android.content.Context;
import android.util.Log;
import java.util.List;

class a implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f4277a;

    a(Context context) {
        this.f4277a = context;
    }

    public void run() {
        try {
            b.a(this.f4277a, (List<String>) null);
        } catch (Exception e) {
            Log.e("TopGameUtils", "run: " + e.toString());
        }
    }
}
