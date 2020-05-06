package com.miui.gamebooster.ui;

import com.miui.gamebooster.e;
import java.text.Collator;
import java.util.Comparator;

class Ea implements Comparator<e> {

    /* renamed from: a  reason: collision with root package name */
    private Collator f4874a = Collator.getInstance();

    Ea() {
    }

    /* renamed from: a */
    public int compare(e eVar, e eVar2) {
        if (eVar.a() && !eVar2.a()) {
            return -1;
        }
        if (eVar.a() || !eVar2.a()) {
            return this.f4874a.compare(eVar.c(), eVar2.c());
        }
        return 1;
    }
}
