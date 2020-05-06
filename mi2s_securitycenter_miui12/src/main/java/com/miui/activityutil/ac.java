package com.miui.activityutil;

import android.text.TextUtils;
import java.io.File;

final class ac implements x {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ h f2260a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ File f2261b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ ab f2262c;

    ac(ab abVar, h hVar, File file) {
        this.f2262c = abVar;
        this.f2260a = hVar;
        this.f2261b = file;
    }

    public final void a(int i) {
        this.f2262c.f2259b.t = false;
        if (i != 0) {
            this.f2261b.delete();
        }
    }

    public final void a(String str) {
        if (!TextUtils.isEmpty(str)) {
            File a2 = aj.a(this.f2262c.f2259b.r, aa.f);
            if (a2.exists()) {
                a2.delete();
            }
            if (this.f2262c.f2259b.b(str.getBytes())) {
                h.a(a2, str.getBytes());
                this.f2260a.a();
                this.f2262c.f2259b.m();
            }
        }
        this.f2262c.f2259b.t = false;
    }
}
