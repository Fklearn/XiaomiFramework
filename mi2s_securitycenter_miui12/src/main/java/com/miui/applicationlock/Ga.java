package com.miui.applicationlock;

import android.content.DialogInterface;
import com.miui.applicationlock.a.h;
import com.miui.applicationlock.c.o;

class Ga implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ RecommendGuideActivity f3176a;

    Ga(RecommendGuideActivity recommendGuideActivity) {
        this.f3176a = recommendGuideActivity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (this.f3176a.f3211a.isChecked()) {
            o.e(-1);
            h.h();
        }
        this.f3176a.m();
        h.i();
        this.f3176a.finish();
    }
}
