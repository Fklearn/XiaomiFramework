package com.miui.antivirus.activity;

import android.content.DialogInterface;
import b.b.b.p;
import miui.widget.SlidingButton;

class z implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ SlidingButton f2743a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ MainActivity f2744b;

    z(MainActivity mainActivity, SlidingButton slidingButton) {
        this.f2744b = mainActivity;
        this.f2743a = slidingButton;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        p.a(false);
        p.g(this.f2743a.isChecked());
        this.f2744b.o();
    }
}
