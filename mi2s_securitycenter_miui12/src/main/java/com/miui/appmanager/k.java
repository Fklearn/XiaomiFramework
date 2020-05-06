package com.miui.appmanager;

import com.miui.appmanager.c.j;
import java.text.Collator;
import java.util.Comparator;

class k implements Comparator<com.miui.appmanager.c.k> {

    /* renamed from: a  reason: collision with root package name */
    private Collator f3679a = Collator.getInstance();

    k() {
    }

    /* renamed from: a */
    public int compare(com.miui.appmanager.c.k kVar, com.miui.appmanager.c.k kVar2) {
        j jVar = (j) kVar;
        j jVar2 = (j) kVar2;
        int i = (jVar.j() > jVar2.j() ? 1 : (jVar.j() == jVar2.j() ? 0 : -1));
        if (i < 0) {
            return 1;
        }
        if (i > 0) {
            return -1;
        }
        return this.f3679a.compare(jVar.d(), jVar2.d());
    }
}
