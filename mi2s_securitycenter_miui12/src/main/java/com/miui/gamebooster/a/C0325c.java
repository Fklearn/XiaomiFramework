package com.miui.gamebooster.a;

import android.widget.ImageView;

/* renamed from: com.miui.gamebooster.a.c  reason: case insensitive filesystem */
/* compiled from: lambda */
public final /* synthetic */ class C0325c implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    private final /* synthetic */ x f4035a;

    /* renamed from: b  reason: collision with root package name */
    private final /* synthetic */ ImageView f4036b;

    /* renamed from: c  reason: collision with root package name */
    private final /* synthetic */ String f4037c;

    public /* synthetic */ C0325c(x xVar, ImageView imageView, String str) {
        this.f4035a = xVar;
        this.f4036b = imageView;
        this.f4037c = str;
    }

    public final void run() {
        this.f4035a.a(this.f4036b, this.f4037c);
    }
}
