package com.miui.applicationlock;

import com.miui.applicationlock.c.C0257a;
import java.util.Comparator;

/* renamed from: com.miui.applicationlock.i  reason: case insensitive filesystem */
class C0279i implements Comparator<C0257a> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C0312y f3353a;

    C0279i(C0312y yVar) {
        this.f3353a = yVar;
    }

    /* renamed from: a */
    public int compare(C0257a aVar, C0257a aVar2) {
        if (aVar.c() && !aVar2.c()) {
            return -1;
        }
        if (aVar.c() || !aVar2.c()) {
            return aVar.a().compareTo(aVar2.a());
        }
        return 1;
    }
}
