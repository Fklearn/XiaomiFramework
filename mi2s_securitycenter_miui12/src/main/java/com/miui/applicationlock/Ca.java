package com.miui.applicationlock;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import b.b.c.i.a;
import b.b.c.j.B;
import b.b.c.j.x;
import com.miui.applicationlock.MaskNotificationActivity;
import com.miui.applicationlock.c.C0257a;
import com.miui.applicationlock.c.F;
import com.miui.applicationlock.c.G;
import com.miui.applicationlock.c.o;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Ca extends a<ArrayList<F>> {

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ MaskNotificationActivity f3113b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ MaskNotificationActivity.a f3114c;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    Ca(MaskNotificationActivity.a aVar, Context context, MaskNotificationActivity maskNotificationActivity) {
        super(context);
        this.f3114c = aVar;
        this.f3113b = maskNotificationActivity;
    }

    /* JADX WARNING: type inference failed for: r13v0, types: [android.content.Context, com.miui.applicationlock.MaskNotificationActivity] */
    public ArrayList<F> loadInBackground() {
        List<ApplicationInfo> a2 = o.a(this.f3113b.f3192b);
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        F f = new F();
        F f2 = new F();
        ArrayList<F> arrayList3 = new ArrayList<>();
        ArrayList arrayList4 = new ArrayList();
        for (ApplicationInfo next : a2) {
            String str = next.packageName;
            C0257a aVar = new C0257a(x.j(this.f3113b, str).toString(), Integer.valueOf(next.flags & 1), str, B.c(next.uid));
            aVar.a(this.f3113b.f3192b.getApplicationMaskNotificationEnabledAsUser(str, B.c(next.uid)));
            aVar.c(false);
            if (aVar.f()) {
                arrayList.add(aVar);
            } else {
                if (MaskNotificationActivity.f3191a.contains(str) && this.f3113b.f3193c.equals("zh")) {
                    aVar.c(true);
                }
                arrayList2.add(aVar);
            }
            arrayList4.add(aVar);
        }
        if (arrayList.size() > 0) {
            if (arrayList.size() > 1 && this.f3113b.f3193c.equals("zh")) {
                Collections.sort(arrayList, this.f3113b.i);
            }
            f.a((List<C0257a>) arrayList);
            f.a(G.ENABLED);
            f.a(String.format(this.f3113b.getResources().getString(R.string.privacyapp_number_masked_text), new Object[0]));
            arrayList3.add(f);
        }
        if (arrayList2.size() > 0) {
            if (arrayList2.size() > 1 && this.f3113b.f3193c.equals("zh")) {
                Collections.sort(arrayList2, this.f3113b.i);
            }
            f2.a((List<C0257a>) arrayList2);
            f2.a(G.DISABLED);
            f2.a(String.format(this.f3113b.getResources().getString(R.string.privacyapp_number_unmasked_text), new Object[0]));
            arrayList3.add(f2);
        }
        return arrayList3;
    }
}
