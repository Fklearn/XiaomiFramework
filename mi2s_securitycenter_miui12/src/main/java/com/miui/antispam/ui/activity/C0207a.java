package com.miui.antispam.ui.activity;

import android.content.DialogInterface;

/* renamed from: com.miui.antispam.ui.activity.a  reason: case insensitive filesystem */
class C0207a implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int f2580a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ String[] f2581b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ int[] f2582c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ int f2583d;
    final /* synthetic */ int e;
    final /* synthetic */ AddAntiSpamActivity f;

    C0207a(AddAntiSpamActivity addAntiSpamActivity, int i, String[] strArr, int[] iArr, int i2, int i3) {
        this.f = addAntiSpamActivity;
        this.f2580a = i;
        this.f2581b = strArr;
        this.f2582c = iArr;
        this.f2583d = i2;
        this.e = i3;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.f.a(this.f2580a, this.f2581b, this.f2582c, this.f2583d, this.e);
    }
}
