package com.miui.gamebooster.n.a;

import android.animation.TimeInterpolator;

public class b implements TimeInterpolator {

    /* renamed from: a  reason: collision with root package name */
    private float f4661a = 0.95f;

    /* renamed from: b  reason: collision with root package name */
    private float f4662b = 0.6f;

    /* renamed from: c  reason: collision with root package name */
    private float f4663c = -1.0f;

    /* renamed from: d  reason: collision with root package name */
    private float f4664d = this.f4663c;
    private float e = 1.0f;
    private float f;
    private float g;
    private float h;
    private float i;
    private float j;

    public b() {
        a();
    }

    private void a() {
        double pow = Math.pow(6.283185307179586d / ((double) this.f4662b), 2.0d);
        float f2 = this.e;
        this.f = (float) (pow * ((double) f2));
        this.g = (float) (((((double) this.f4661a) * 12.566370614359172d) * ((double) f2)) / ((double) this.f4662b));
        float f3 = f2 * 4.0f * this.f;
        float f4 = this.g;
        float f5 = this.e;
        this.h = ((float) Math.sqrt((double) (f3 - (f4 * f4)))) / (f5 * 2.0f);
        this.i = -((this.g / 2.0f) * f5);
        this.j = (0.0f - (this.i * this.f4663c)) / this.h;
    }

    public b a(float f2) {
        this.f4661a = f2;
        a();
        return this;
    }

    public b b(float f2) {
        this.f4662b = f2;
        a();
        return this;
    }

    public float getInterpolation(float f2) {
        return (float) ((Math.pow(2.718281828459045d, (double) (this.i * f2)) * ((((double) this.f4664d) * Math.cos((double) (this.h * f2))) + (((double) this.j) * Math.sin((double) (this.h * f2))))) + 1.0d);
    }
}
