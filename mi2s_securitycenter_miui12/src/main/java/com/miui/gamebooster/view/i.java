package com.miui.gamebooster.view;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import com.miui.gamebooster.m.na;
import com.miui.superpower.b.k;

class i {

    /* renamed from: a  reason: collision with root package name */
    private static final String f5289a = "i";

    /* renamed from: b  reason: collision with root package name */
    private final WindowManager f5290b;

    /* renamed from: c  reason: collision with root package name */
    private final WindowManager.LayoutParams f5291c;

    /* renamed from: d  reason: collision with root package name */
    private final Context f5292d;
    private View e;
    private int f;
    private int g;
    private boolean h = false;
    private int i;
    private int j;
    private boolean k;

    i(Context context) {
        this.f5292d = context;
        this.f5290b = (WindowManager) context.getSystemService("window");
        this.f5291c = new WindowManager.LayoutParams();
        WindowManager.LayoutParams layoutParams = this.f5291c;
        layoutParams.format = 1;
        layoutParams.flags = 520;
        layoutParams.windowAnimations = 0;
        layoutParams.gravity = 51;
    }

    private int a(Context context) {
        int i2 = 0;
        if (90 == na.a(context)) {
            int e2 = na.e(context) - this.i;
            if (!b.b.c.j.i.h(this.f5292d)) {
                i2 = k.c(this.f5292d);
            }
            return e2 + i2;
        }
        int e3 = na.e(context) - this.i;
        if (b.b.c.j.i.e()) {
            i2 = k.d(context);
        }
        return e3 + i2;
    }

    private int b(Context context) {
        return (na.c(context) - this.j) - k.d(context);
    }

    private int c(Context context) {
        int i2 = 0;
        if (90 != na.a(context)) {
            if (!b.b.c.j.i.h(this.f5292d)) {
                i2 = k.c(this.f5292d);
            }
            return -i2;
        } else if (b.b.c.j.i.e()) {
            return -k.d(context);
        } else {
            return 0;
        }
    }

    private int f() {
        return -k.d(this.f5292d);
    }

    private void g() {
        try {
            if (e()) {
                this.f5290b.removeView(this.e);
            }
        } catch (Exception e2) {
            Log.e(f5289a, "remove float view error ", e2);
        }
    }

    public void a() {
        this.h = true;
        g();
    }

    public void a(int i2, int i3) {
        WindowManager.LayoutParams layoutParams = this.f5291c;
        this.f = i2;
        layoutParams.x = i2;
        this.g = i3;
        layoutParams.y = i3;
    }

    public void a(View view, int i2, int i3) {
        this.e = view;
        this.i = i2;
        this.j = i3;
    }

    public void a(boolean z) {
        this.k = z;
    }

    /* access modifiers changed from: package-private */
    public int b() {
        return this.f;
    }

    public void b(int i2, int i3) {
        if (!this.h && i2 > c(this.f5292d) && i2 < a(this.f5292d) && i3 > f() && i3 < b(this.f5292d)) {
            WindowManager.LayoutParams layoutParams = this.f5291c;
            this.f = i2;
            layoutParams.x = i2;
            this.g = i3;
            layoutParams.y = i3;
            this.f5290b.updateViewLayout(this.e, layoutParams);
        }
    }

    /* access modifiers changed from: package-private */
    public int c() {
        return this.g;
    }

    public void d() {
        try {
            this.f5291c.type = 2003;
            this.f5291c.width = -2;
            this.f5291c.height = -2;
            this.f5290b.addView(this.e, this.f5291c);
            a(true);
        } catch (Exception e2) {
            Log.e(f5289a, "add float view error ", e2);
            g();
        }
    }

    public boolean e() {
        return this.k;
    }
}
