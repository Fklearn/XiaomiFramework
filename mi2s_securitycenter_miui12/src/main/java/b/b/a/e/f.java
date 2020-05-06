package b.b.a.e;

import android.content.ContentResolver;
import b.b.a.d.a.h;
import b.b.a.d.a.l;
import java.util.ArrayList;

class f implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ l f1430a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ ContentResolver f1431b;

    f(l lVar, ContentResolver contentResolver) {
        this.f1430a = lVar;
        this.f1431b = contentResolver;
    }

    public void run() {
        ArrayList arrayList = new ArrayList();
        int itemCount = this.f1430a.getItemCount();
        for (int i = 0; i < itemCount; i++) {
            h.a aVar = (h.a) this.f1430a.a(i);
            String str = aVar.f1352b;
            if (str == null) {
                str = aVar.f1351a;
            }
            arrayList.add(str);
        }
        g.b(this.f1431b, arrayList);
    }
}
