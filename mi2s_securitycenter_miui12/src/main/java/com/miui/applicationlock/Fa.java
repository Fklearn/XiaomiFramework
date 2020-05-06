package com.miui.applicationlock;

import android.content.DialogInterface;
import com.miui.applicationlock.a.h;
import com.miui.applicationlock.c.o;

class Fa implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ RecommendGuideActivity f3169a;

    Fa(RecommendGuideActivity recommendGuideActivity) {
        this.f3169a = recommendGuideActivity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (this.f3169a.f3211a.isChecked()) {
            o.e(-1);
            h.h();
        }
        h.g();
        this.f3169a.finish();
    }
}
