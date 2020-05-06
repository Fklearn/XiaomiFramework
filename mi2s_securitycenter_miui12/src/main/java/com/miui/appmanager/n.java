package com.miui.appmanager;

import com.miui.appmanager.c.j;
import com.miui.appmanager.c.k;
import java.text.Collator;
import java.util.Comparator;

class n implements Comparator<k> {

    /* renamed from: a  reason: collision with root package name */
    private Collator f3682a = Collator.getInstance();

    n() {
    }

    /* renamed from: a */
    public int compare(k kVar, k kVar2) {
        j jVar = (j) kVar;
        j jVar2 = (j) kVar2;
        if (jVar.c() < jVar2.c()) {
            return 1;
        }
        if (jVar.c() > jVar2.c()) {
            return -1;
        }
        return this.f3682a.compare(jVar.d(), jVar2.d());
    }
}
