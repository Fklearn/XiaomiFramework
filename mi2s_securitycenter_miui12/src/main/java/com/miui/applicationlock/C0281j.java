package com.miui.applicationlock;

import com.miui.applicationlock.c.C0257a;
import java.util.Comparator;

/* renamed from: com.miui.applicationlock.j  reason: case insensitive filesystem */
class C0281j implements Comparator<C0257a> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C0312y f3355a;

    C0281j(C0312y yVar) {
        this.f3355a = yVar;
    }

    /* renamed from: a */
    public int compare(C0257a aVar, C0257a aVar2) {
        if (!aVar.e().equals(this.f3355a.u) || aVar2.e().equals(this.f3355a.u)) {
            return (aVar.e().equals(this.f3355a.u) || !aVar2.e().equals(this.f3355a.u)) ? 0 : 1;
        }
        return -1;
    }
}
