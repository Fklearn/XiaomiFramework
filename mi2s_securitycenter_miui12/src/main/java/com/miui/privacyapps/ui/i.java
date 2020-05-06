package com.miui.privacyapps.ui;

import b.b.k.c;
import java.text.Collator;
import java.util.Comparator;

class i implements Comparator<c> {

    /* renamed from: a  reason: collision with root package name */
    private Collator f7404a = Collator.getInstance();

    i() {
    }

    /* renamed from: a */
    public int compare(c cVar, c cVar2) {
        if (!cVar.a() && cVar2.a()) {
            return 1;
        }
        if (!cVar.a() || cVar2.a()) {
            return this.f7404a.compare(cVar.b(), cVar2.b());
        }
        return -1;
    }
}
