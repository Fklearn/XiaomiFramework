package com.miui.gamebooster.view;

import android.content.Context;
import android.view.View;
import android.view.ViewConfiguration;
import java.util.HashMap;
import java.util.Map;

public class k {

    /* renamed from: a  reason: collision with root package name */
    private static a f5297a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public static Map<String, k> f5298b = new HashMap();
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public i f5299c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public float f5300d;
    /* access modifiers changed from: private */
    public float e;
    /* access modifiers changed from: private */
    public float f;
    /* access modifiers changed from: private */
    public float g;
    /* access modifiers changed from: private */
    public boolean h = false;
    /* access modifiers changed from: private */
    public int i;

    public static class a {

        /* renamed from: a  reason: collision with root package name */
        Context f5301a;

        /* renamed from: b  reason: collision with root package name */
        View f5302b;

        /* renamed from: c  reason: collision with root package name */
        int f5303c;

        /* renamed from: d  reason: collision with root package name */
        int f5304d;
        int e;
        int f;

        a(Context context) {
            this.f5301a = context;
        }

        public a a(int i, int i2) {
            this.f5303c = i;
            this.f5304d = i2;
            return this;
        }

        public a a(View view, int i, int i2) {
            this.f5302b = view;
            this.e = i;
            this.f = i2;
            return this;
        }

        public void a() {
            if (k.f5298b.containsKey("default_float_window_tag")) {
                ((k) k.f5298b.get("default_float_window_tag")).c();
                k.f5298b.remove("default_float_window_tag");
            }
            k kVar = new k(this);
            k.f5298b.put("default_float_window_tag", kVar);
            kVar.e();
        }
    }

    private k() {
    }

    k(a aVar) {
        this.f5299c = new i(aVar.f5301a);
        f();
        this.f5299c.a(aVar.f5303c, aVar.f5304d);
        this.f5299c.a(aVar.f5302b, aVar.e, aVar.f);
    }

    public static a a(Context context) {
        if (f5297a == null) {
            f5297a = new a(context);
        }
        return f5297a;
    }

    public static void b() {
        if (f5298b.containsKey("default_float_window_tag")) {
            f5298b.get("default_float_window_tag").c();
            f5298b.remove("default_float_window_tag");
        }
        f5297a = null;
    }

    private void f() {
        View d2 = d();
        if (d2 != null) {
            d2.setOnTouchListener(new j(this));
        }
    }

    public void c() {
        this.f5299c.a();
    }

    public View d() {
        this.i = ViewConfiguration.get(f5297a.f5301a).getScaledTouchSlop();
        return f5297a.f5302b;
    }

    public void e() {
        this.f5299c.d();
    }
}
