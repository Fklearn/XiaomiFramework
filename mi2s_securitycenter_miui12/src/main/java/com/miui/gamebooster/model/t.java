package com.miui.gamebooster.model;

import android.text.TextUtils;
import com.miui.activityutil.o;
import java.text.DecimalFormat;

public class t {

    /* renamed from: a  reason: collision with root package name */
    private int f4591a;

    /* renamed from: b  reason: collision with root package name */
    private String f4592b;

    /* renamed from: c  reason: collision with root package name */
    private long f4593c;

    /* renamed from: d  reason: collision with root package name */
    private String f4594d;
    private String e;
    private String f;
    private String g;
    private String h;
    private String i;
    private int j;
    private boolean k;

    private String b(int i2) {
        StringBuilder sb;
        String str;
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        if (i2 / 1000000000 >= 1) {
            sb = new StringBuilder();
            sb.append(decimalFormat.format((double) (((float) i2) / ((float) 1000000000))));
            str = "GB   ";
        } else if (i2 / 1000000 >= 1) {
            sb = new StringBuilder();
            sb.append(decimalFormat.format((double) (((float) i2) / ((float) 1000000))));
            str = "MB   ";
        } else if (i2 / 1000 >= 1) {
            return decimalFormat.format((double) (((float) i2) / ((float) 1000))) + "KB   ";
        } else {
            return i2 + "B   ";
        }
        sb.append(str);
        return sb.toString();
    }

    public String a() {
        String str;
        String str2;
        long j2 = this.f4593c;
        int i2 = (int) (j2 / 60);
        int i3 = (int) (j2 % 60);
        StringBuilder sb = new StringBuilder();
        if (i2 < 10) {
            str = o.f2309a + i2;
        } else {
            str = String.valueOf(i2);
        }
        sb.append(str);
        sb.append(":");
        if (i3 < 10) {
            str2 = o.f2309a + i3;
        } else {
            str2 = String.valueOf(i3);
        }
        sb.append(str2);
        return sb.toString();
    }

    public void a(int i2) {
        this.f4591a = i2;
    }

    public void a(long j2) {
        this.f4593c = j2 / 1000;
    }

    public void a(String str) {
        this.e = str;
    }

    public void a(boolean z) {
        this.k = z;
    }

    public String b() {
        return b(this.j);
    }

    public void b(long j2) {
        this.j = (int) j2;
    }

    public void b(String str) {
        this.f4594d = str;
    }

    public String c() {
        return this.f4594d;
    }

    public void c(String str) {
        this.g = str;
    }

    public String d() {
        return this.g;
    }

    public void d(String str) {
        this.f4592b = str;
    }

    public int e() {
        return this.f4591a;
    }

    public void e(String str) {
        this.i = str;
    }

    public String f() {
        return this.i;
    }

    public void f(String str) {
        this.f = str;
    }

    public String g() {
        return this.f;
    }

    public void g(String str) {
        this.h = str;
    }

    public boolean h() {
        return TextUtils.equals("ai", this.h);
    }

    public boolean i() {
        return this.k;
    }
}
