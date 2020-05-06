package com.xiaomi.stat.a;

import android.text.TextUtils;
import com.xiaomi.stat.a.l;

public class b {

    /* renamed from: a  reason: collision with root package name */
    public static final int f8363a = 0;

    /* renamed from: b  reason: collision with root package name */
    public static final int f8364b = 1;

    /* renamed from: c  reason: collision with root package name */
    private String f8365c;

    /* renamed from: d  reason: collision with root package name */
    private int f8366d;
    private boolean e;
    private boolean f;

    public b(String str, int i, boolean z) {
        this.f8365c = str;
        this.f8366d = i;
        this.e = z;
        this.f = TextUtils.isEmpty(str);
    }

    public String a() {
        StringBuilder sb = new StringBuilder();
        sb.append(j.i);
        if (this.f) {
            sb.append(" is null");
        } else {
            sb.append(" = \"");
            sb.append(this.f8365c);
            sb.append("\"");
        }
        if (this.f8366d != 0) {
            sb.append(" and ");
            sb.append("eg");
            sb.append(" = \"");
            sb.append(l.a.h);
            sb.append("\"");
        }
        sb.append(" and ");
        sb.append(j.j);
        sb.append(" = ");
        sb.append(this.e ? 1 : 0);
        return sb.toString();
    }

    public boolean a(String str, String str2, boolean z) {
        if (TextUtils.equals(str, this.f8365c) && this.e == z) {
            if (this.f8366d == 0) {
                return true;
            }
            return this.f && TextUtils.equals(str2, l.a.h);
        }
    }
}
