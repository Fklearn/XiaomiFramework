package b.d.e.c;

import b.d.e.b;
import java.util.ArrayList;
import java.util.List;

public abstract class f extends d {

    /* renamed from: a  reason: collision with root package name */
    protected boolean f2200a = false;

    /* access modifiers changed from: protected */
    public abstract List<String> a(String str);

    public List<String> a(List<String> list) {
        ArrayList arrayList = new ArrayList();
        for (String a2 : list) {
            arrayList.addAll(a(a2));
        }
        return arrayList;
    }

    public abstract boolean a(b bVar, int[] iArr, int i);

    /* access modifiers changed from: protected */
    public void b() {
        this.f2200a = false;
    }

    public boolean b(b bVar, int[] iArr, int i) {
        if (iArr == null || i >= iArr.length) {
            return false;
        }
        return a(bVar, iArr, i);
    }
}
