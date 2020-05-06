package com.miui.gamebooster.globalgame.view;

import android.view.View;

public abstract class a implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private long f4425a;

    public abstract void a(View view);

    public final void onClick(View view) {
        long j = this.f4425a;
        long currentTimeMillis = System.currentTimeMillis();
        this.f4425a = currentTimeMillis;
        if (currentTimeMillis - j >= 500) {
            a(view);
        }
    }
}
