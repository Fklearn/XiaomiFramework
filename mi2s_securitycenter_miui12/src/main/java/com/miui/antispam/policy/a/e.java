package com.miui.antispam.policy.a;

import android.text.TextUtils;
import android.util.Log;
import b.b.a.e.n;

public class e {

    /* renamed from: a  reason: collision with root package name */
    public String f2368a;

    /* renamed from: b  reason: collision with root package name */
    public String f2369b = n.f(this.f2368a);

    /* renamed from: c  reason: collision with root package name */
    public int f2370c;

    /* renamed from: d  reason: collision with root package name */
    public int f2371d;
    public String e;
    public boolean f;
    public boolean g;
    public boolean h;
    public boolean i;

    public e(String str, int i2, int i3, String str2, boolean z, boolean z2, boolean z3) {
        this.f2368a = n.c(str);
        this.f2370c = i2;
        this.f2371d = i3;
        this.e = str2;
        this.i = TextUtils.isEmpty(str2);
        this.f = z;
        this.g = z2;
        this.h = z3;
        if (this.f2369b.length() > 3) {
            StringBuilder sb = new StringBuilder();
            sb.append("Normalized number is ");
            sb.append(this.f2369b.substring(0, 3));
            sb.append("*****");
            String str3 = this.f2369b;
            sb.append(str3.substring(str3.length() - 3, this.f2369b.length()));
            Log.i("AntiSpamTest", sb.toString());
        }
    }
}
