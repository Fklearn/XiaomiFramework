package com.miui.applicationlock;

import android.content.DialogInterface;

class Ea implements DialogInterface.OnDismissListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ RecommendGuideActivity f3167a;

    Ea(RecommendGuideActivity recommendGuideActivity) {
        this.f3167a = recommendGuideActivity;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        this.f3167a.finish();
    }
}
