package com.miui.appmanager;

import com.miui.appmanager.c.k;
import java.text.Collator;
import java.util.Comparator;

class j implements Comparator<k> {

    /* renamed from: a  reason: collision with root package name */
    private Collator f3678a = Collator.getInstance();

    j() {
    }

    /* renamed from: a */
    public int compare(k kVar, k kVar2) {
        return this.f3678a.compare(((com.miui.appmanager.c.j) kVar).d(), ((com.miui.appmanager.c.j) kVar2).d());
    }
}
