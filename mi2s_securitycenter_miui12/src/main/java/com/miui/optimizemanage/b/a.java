package com.miui.optimizemanage.b;

import com.miui.optimizemanage.b.b;
import java.util.Comparator;

class a implements Comparator<b.a> {
    a() {
    }

    /* renamed from: a */
    public final int compare(b.a aVar, b.a aVar2) {
        int i = aVar.q + aVar.r;
        int i2 = aVar2.q + aVar2.r;
        if (i != i2) {
            return i > i2 ? -1 : 1;
        }
        boolean z = aVar.z;
        if (z != aVar2.z) {
            return z ? -1 : 1;
        }
        if (aVar.A != aVar2.A) {
            return z ? -1 : 1;
        }
        return 0;
    }
}
