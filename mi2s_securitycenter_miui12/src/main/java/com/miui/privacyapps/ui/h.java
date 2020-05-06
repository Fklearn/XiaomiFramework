package com.miui.privacyapps.ui;

import b.b.k.c;
import java.text.Collator;
import java.util.Comparator;

class h implements Comparator<c> {

    /* renamed from: a  reason: collision with root package name */
    private Collator f7403a = Collator.getInstance();

    h() {
    }

    /* renamed from: a */
    public int compare(c cVar, c cVar2) {
        return this.f7403a.compare(cVar.b(), cVar2.b());
    }
}
