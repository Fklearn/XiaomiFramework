package androidx.recyclerview.widget;

import androidx.recyclerview.widget.C0178t;
import java.util.Comparator;

/* renamed from: androidx.recyclerview.widget.s  reason: case insensitive filesystem */
class C0177s implements Comparator<C0178t.b> {
    C0177s() {
    }

    /* renamed from: a */
    public int compare(C0178t.b bVar, C0178t.b bVar2) {
        if ((bVar.f1265d == null) != (bVar2.f1265d == null)) {
            return bVar.f1265d == null ? 1 : -1;
        }
        boolean z = bVar.f1262a;
        if (z != bVar2.f1262a) {
            return z ? -1 : 1;
        }
        int i = bVar2.f1263b - bVar.f1263b;
        if (i != 0) {
            return i;
        }
        int i2 = bVar.f1264c - bVar2.f1264c;
        if (i2 != 0) {
            return i2;
        }
        return 0;
    }
}
