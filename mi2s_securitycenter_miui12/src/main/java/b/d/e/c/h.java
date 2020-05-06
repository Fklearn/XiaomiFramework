package b.d.e.c;

import b.d.e.b;
import java.io.DataInputStream;
import java.util.List;

public class h extends g {

    /* renamed from: a  reason: collision with root package name */
    List<int[]> f2201a = null;

    /* renamed from: b  reason: collision with root package name */
    int f2202b = 0;

    /* renamed from: c  reason: collision with root package name */
    int f2203c = 0;

    /* renamed from: d  reason: collision with root package name */
    int f2204d = 0;

    private int a(b bVar) {
        return bVar.c().length();
    }

    public void a(DataInputStream dataInputStream) {
        this.f2204d = dataInputStream.readInt();
    }

    public boolean a(b bVar, int[] iArr, int i) {
        if (a(bVar) < this.f2204d) {
            return false;
        }
        iArr[i] = iArr[i] + 1;
        return true;
    }
}
