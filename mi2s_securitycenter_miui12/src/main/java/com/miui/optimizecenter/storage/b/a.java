package com.miui.optimizecenter.storage.b;

import android.app.Activity;
import android.content.DialogInterface;

/* compiled from: lambda */
public final /* synthetic */ class a implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    private final /* synthetic */ String f5713a;

    /* renamed from: b  reason: collision with root package name */
    private final /* synthetic */ String f5714b;

    /* renamed from: c  reason: collision with root package name */
    private final /* synthetic */ boolean f5715c;

    /* renamed from: d  reason: collision with root package name */
    private final /* synthetic */ Activity f5716d;

    public /* synthetic */ a(String str, String str2, boolean z, Activity activity) {
        this.f5713a = str;
        this.f5714b = str2;
        this.f5715c = z;
        this.f5716d = activity;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        c.a(this.f5713a, this.f5714b, this.f5715c, this.f5716d, dialogInterface, i);
    }
}
