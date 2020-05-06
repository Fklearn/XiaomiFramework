package com.miui.permcenter.permissions;

import com.miui.permcenter.a;
import com.miui.permcenter.l;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public class v implements Comparator<a> {

    /* renamed from: a  reason: collision with root package name */
    private Collator f6298a = Collator.getInstance(Locale.getDefault());

    /* renamed from: b  reason: collision with root package name */
    private long f6299b;

    public v(long j) {
        this.f6299b = j;
    }

    /* renamed from: a */
    public int compare(a aVar, a aVar2) {
        int a2 = l.a(aVar.f().get(Long.valueOf(this.f6299b)).intValue());
        int a3 = l.a(aVar2.f().get(Long.valueOf(this.f6299b)).intValue());
        return a2 == a3 ? this.f6298a.compare(aVar.d(), aVar2.d()) : a3 - a2;
    }
}
