package com.miui.appmanager;

import com.miui.appmanager.c.j;
import com.miui.appmanager.c.k;
import java.text.Collator;
import java.util.Comparator;

class l implements Comparator<k> {

    /* renamed from: a  reason: collision with root package name */
    private Collator f3680a = Collator.getInstance();

    l() {
    }

    /* renamed from: a */
    public int compare(k kVar, k kVar2) {
        j jVar = (j) kVar;
        j jVar2 = (j) kVar2;
        if (jVar.h() < jVar2.h()) {
            return 1;
        }
        if (jVar.h() > jVar2.h()) {
            return -1;
        }
        return this.f3680a.compare(jVar.d(), jVar2.d());
    }
}
