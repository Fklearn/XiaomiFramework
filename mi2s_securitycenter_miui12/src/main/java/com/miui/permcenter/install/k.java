package com.miui.permcenter.install;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

class k implements Comparator<h> {

    /* renamed from: a  reason: collision with root package name */
    private Collator f6159a = Collator.getInstance(Locale.getDefault());

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ l f6160b;

    k(l lVar) {
        this.f6160b = lVar;
    }

    /* renamed from: a */
    public int compare(h hVar, h hVar2) {
        return this.f6159a.compare(hVar.b(), hVar2.b());
    }
}
