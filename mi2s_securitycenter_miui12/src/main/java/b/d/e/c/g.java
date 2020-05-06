package b.d.e.c;

import b.d.e.b;
import java.io.DataInputStream;

public abstract class g extends d {
    public abstract void a(DataInputStream dataInputStream);

    /* access modifiers changed from: protected */
    public abstract boolean a(b bVar, int[] iArr, int i);

    public boolean b(b bVar, int[] iArr, int i) {
        if (bVar == null || iArr == null || i >= iArr.length) {
            return false;
        }
        return a(bVar, iArr, i);
    }
}
