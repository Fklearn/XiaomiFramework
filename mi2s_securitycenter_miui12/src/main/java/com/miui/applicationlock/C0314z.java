package com.miui.applicationlock;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.text.TextUtils;
import b.b.c.i.a;
import b.b.c.j.B;
import b.b.c.j.x;
import com.miui.applicationlock.C0312y;
import com.miui.applicationlock.c.C0257a;
import com.miui.applicationlock.c.F;
import com.miui.applicationlock.c.G;
import com.miui.applicationlock.c.o;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* renamed from: com.miui.applicationlock.z  reason: case insensitive filesystem */
class C0314z extends a<ArrayList<F>> {

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ C0312y f3476b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ C0312y.c f3477c;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    C0314z(C0312y.c cVar, Context context, C0312y yVar) {
        super(context);
        this.f3477c = cVar;
        this.f3476b = yVar;
    }

    public ArrayList<F> loadInBackground() {
        List<ApplicationInfo> c2 = o.c();
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        F f = new F();
        F f2 = new F();
        ArrayList<F> arrayList4 = new ArrayList<>();
        ArrayList arrayList5 = new ArrayList();
        for (ApplicationInfo next : c2) {
            String str = next.packageName;
            C0257a aVar = new C0257a(x.j(this.f3476b.z, str).toString(), Integer.valueOf(next.flags & 1), str, B.c(next.uid));
            aVar.a(this.f3476b.g.getApplicationAccessControlEnabledAsUser(str, B.c(next.uid)));
            aVar.b(this.f3476b.g.getApplicationMaskNotificationEnabledAsUser(str, B.c(next.uid)));
            aVar.c(false);
            if (aVar.f()) {
                arrayList.add(aVar);
            } else {
                if (C0312y.f3468b.contains(aVar.e())) {
                    if (this.f3476b.m.equals("zh")) {
                        aVar.c(true);
                    }
                    arrayList2.add(aVar);
                }
                arrayList3.add(aVar);
            }
            arrayList5.add(aVar);
        }
        ArrayList unused = this.f3476b.k = arrayList5;
        if (arrayList.size() > 0) {
            if (!TextUtils.isEmpty(this.f3476b.u) && arrayList.size() > 1 && this.f3476b.m.equals("zh")) {
                Collections.sort(arrayList, this.f3476b.K);
            }
            f.a((List<C0257a>) arrayList);
            f.a(G.ENABLED);
            f.a(String.format(this.f3476b.getResources().getQuantityString(R.plurals.number_locked, arrayList.size()), new Object[]{Integer.valueOf(arrayList.size())}));
            arrayList4.add(f);
        } else if (o.s() && arrayList2.size() > 0) {
            F f3 = new F();
            f3.a(G.RECOMMEND);
            f3.a(this.f3476b.getResources().getString(R.string.applock_app_recommend_lock_title));
            f3.a((List<C0257a>) arrayList2);
            arrayList4.add(f3);
            o.d(false);
            if (arrayList3.size() > 0) {
                for (int i = 0; i < arrayList2.size(); i++) {
                    if (arrayList3.contains(arrayList2.get(i))) {
                        arrayList3.remove(arrayList2.get(i));
                    }
                }
            }
        }
        if (arrayList3.size() > 0) {
            if (arrayList3.size() > 1 && this.f3476b.m.equals("zh")) {
                Collections.sort(arrayList3, this.f3476b.J);
            }
            f2.a((List<C0257a>) arrayList3);
            f2.a(G.DISABLED);
            f2.a(String.format(this.f3476b.getResources().getQuantityString(R.plurals.number_to_lock, arrayList3.size()), new Object[]{Integer.valueOf(arrayList3.size())}));
            arrayList4.add(f2);
        }
        return arrayList4;
    }
}
