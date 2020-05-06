package com.miui.gamebooster.ui;

import android.support.annotation.UiThread;
import android.text.TextUtils;
import com.miui.gamebooster.globalgame.util.Utils;
import com.miui.gamebooster.i.a.b;
import com.miui.gamebooster.model.C0397c;
import com.miui.gamebooster.model.C0398d;
import com.miui.gamebooster.xunyou.h;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class H extends h.a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ N f4913a;

    H(N n) {
        this.f4913a = n;
    }

    /* access modifiers changed from: private */
    @UiThread
    public void b(List<String> list) {
        ArrayList arrayList = new ArrayList();
        Iterator it = this.f4913a.y.iterator();
        while (it.hasNext()) {
            C0398d dVar = (C0398d) it.next();
            if (dVar != null && !TextUtils.isEmpty(dVar.d())) {
                C0397c cVar = new C0397c(dVar);
                cVar.b(list.contains((String) dVar.d()));
                arrayList.add(cVar);
            }
        }
        if (Utils.b(arrayList)) {
            b.a(this.f4913a.mAppContext, arrayList.size());
        }
        this.f4913a.B();
        this.f4913a.l.b((List<C0397c>) arrayList);
        int i = 0;
        String d2 = Utils.d();
        if (!TextUtils.isEmpty(d2)) {
            i = this.f4913a.l.a(d2);
        }
        this.f4913a.f.setCurrentItem(i, true);
        this.f4913a.g.onPageSelected(i);
    }

    public void a(List<String> list) {
        N n = this.f4913a;
        n.postOnUiThread(new G(this, n.mActivity, list));
    }
}
