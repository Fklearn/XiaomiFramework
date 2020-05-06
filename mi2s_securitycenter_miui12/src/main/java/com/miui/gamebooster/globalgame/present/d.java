package com.miui.gamebooster.globalgame.present;

import android.view.ViewGroup;

/* compiled from: lambda */
public final /* synthetic */ class d implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    private final /* synthetic */ ViewGroup f4405a;

    public /* synthetic */ d(ViewGroup viewGroup) {
        this.f4405a = viewGroup;
    }

    public final void run() {
        this.f4405a.removeAllViews();
    }
}
