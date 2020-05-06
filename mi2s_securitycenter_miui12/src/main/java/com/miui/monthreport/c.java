package com.miui.monthreport;

import com.miui.monthreport.d;
import java.util.Comparator;

class c implements Comparator<d.a> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ d f5624a;

    c(d dVar) {
        this.f5624a = dVar;
    }

    /* renamed from: a */
    public int compare(d.a aVar, d.a aVar2) {
        if (aVar == null && aVar2 == null) {
            return 0;
        }
        return aVar2.g - aVar.g;
    }
}
