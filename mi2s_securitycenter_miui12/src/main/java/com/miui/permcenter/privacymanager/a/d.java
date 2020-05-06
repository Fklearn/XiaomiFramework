package com.miui.permcenter.privacymanager.a;

import android.text.TextUtils;

public class d {

    /* renamed from: a  reason: collision with root package name */
    public int f6336a;

    /* renamed from: b  reason: collision with root package name */
    public String f6337b;

    /* renamed from: c  reason: collision with root package name */
    public int f6338c = 0;

    public d(int i, String str) {
        this.f6336a = i;
        this.f6337b = str;
    }

    public void a(int i) {
        this.f6338c += i;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof d)) {
            return false;
        }
        d dVar = (d) obj;
        return this.f6336a == dVar.f6336a && TextUtils.equals(this.f6337b, dVar.f6337b);
    }

    public int hashCode() {
        return (this.f6336a + this.f6337b).hashCode();
    }
}
