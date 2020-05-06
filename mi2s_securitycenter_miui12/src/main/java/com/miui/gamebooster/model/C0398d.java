package com.miui.gamebooster.model;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import com.miui.gamebooster.gamead.m;

/* renamed from: com.miui.gamebooster.model.d  reason: case insensitive filesystem */
public class C0398d {

    /* renamed from: a  reason: collision with root package name */
    private ApplicationInfo f4553a;

    /* renamed from: b  reason: collision with root package name */
    private boolean f4554b;

    /* renamed from: c  reason: collision with root package name */
    private CharSequence f4555c;

    /* renamed from: d  reason: collision with root package name */
    private Drawable f4556d;
    private m e;

    public C0398d(ApplicationInfo applicationInfo, boolean z, CharSequence charSequence, Drawable drawable) {
        this.f4553a = applicationInfo;
        this.f4554b = z;
        this.f4555c = charSequence;
        this.f4556d = drawable;
    }

    public m a() {
        return this.e;
    }

    public void a(String str) {
        this.f4555c = str;
    }

    public void a(boolean z) {
        this.f4554b = z;
    }

    public ApplicationInfo b() {
        return this.f4553a;
    }

    public Drawable c() {
        return this.f4556d;
    }

    public CharSequence d() {
        return this.f4555c;
    }

    public boolean e() {
        return this.f4554b;
    }
}
