package com.miui.applicationlock.c;

import android.content.Context;
import android.content.Intent;
import com.miui.applicationlock.RecommendGuideActivity;

/* renamed from: com.miui.applicationlock.c.j  reason: case insensitive filesystem */
class C0266j implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f3308a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ String f3309b;

    C0266j(Context context, String str) {
        this.f3308a = context;
        this.f3309b = str;
    }

    public void run() {
        Intent intent = new Intent(this.f3308a, RecommendGuideActivity.class);
        intent.setFlags(268435456);
        intent.putExtra("packageName", this.f3309b);
        this.f3308a.startActivity(intent);
    }
}
