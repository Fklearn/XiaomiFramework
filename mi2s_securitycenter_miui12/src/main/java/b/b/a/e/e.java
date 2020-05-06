package b.b.a.e;

import android.content.ContentResolver;
import android.util.SparseBooleanArray;
import b.b.a.d.a.h;
import com.miui.antispam.ui.view.RecyclerViewExt;
import java.util.ArrayList;

class e implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ SparseBooleanArray f1427a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ RecyclerViewExt.c f1428b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ ContentResolver f1429c;

    e(SparseBooleanArray sparseBooleanArray, RecyclerViewExt.c cVar, ContentResolver contentResolver) {
        this.f1427a = sparseBooleanArray;
        this.f1428b = cVar;
        this.f1429c = contentResolver;
    }

    public void run() {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < this.f1427a.size(); i++) {
            if (this.f1427a.valueAt(i)) {
                h.a aVar = (h.a) this.f1428b.a(this.f1427a.keyAt(i));
                String str = aVar.f1352b;
                if (str == null) {
                    str = aVar.f1351a;
                }
                arrayList.add(str);
            }
        }
        g.b(this.f1429c, arrayList);
    }
}
