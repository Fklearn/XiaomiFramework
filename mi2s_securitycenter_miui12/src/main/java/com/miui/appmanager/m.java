package com.miui.appmanager;

import com.miui.appmanager.c.j;
import com.miui.appmanager.c.k;
import java.text.Collator;
import java.util.Comparator;

class m implements Comparator<k> {

    /* renamed from: a  reason: collision with root package name */
    private Collator f3681a = Collator.getInstance();

    m() {
    }

    /* renamed from: a */
    public int compare(k kVar, k kVar2) {
        j jVar = (j) kVar;
        if (jVar.l() && !((j) kVar2).l()) {
            return -1;
        }
        if (jVar.l() || !((j) kVar2).l()) {
            return this.f3681a.compare(jVar.d(), ((j) kVar2).d());
        }
        return 1;
    }
}
