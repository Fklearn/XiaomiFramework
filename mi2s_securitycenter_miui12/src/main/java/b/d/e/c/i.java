package b.d.e.c;

import b.d.e.b;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.List;

public class i extends g {

    /* renamed from: a  reason: collision with root package name */
    private int[][] f2205a = null;

    /* renamed from: b  reason: collision with root package name */
    private List<Character> f2206b = null;

    /* renamed from: c  reason: collision with root package name */
    int f2207c = 0;

    /* renamed from: d  reason: collision with root package name */
    int f2208d = 0;

    /* access modifiers changed from: protected */
    public int a() {
        return this.f2206b.size();
    }

    public void a(DataInputStream dataInputStream) {
        this.f2206b = new ArrayList();
        int readInt = dataInputStream.readInt();
        for (int i = 0; i < readInt; i++) {
            this.f2206b.add(Character.valueOf(dataInputStream.readChar()));
        }
    }

    public boolean a(b bVar, int[] iArr, int i) {
        String c2 = bVar.c();
        boolean z = false;
        for (int i2 = 0; i2 < this.f2206b.size(); i2++) {
            if (c2.indexOf(this.f2206b.get(i2).charValue()) != -1) {
                int i3 = i + i2;
                iArr[i3] = iArr[i3] + 1;
                z = true;
            }
        }
        return z;
    }
}
