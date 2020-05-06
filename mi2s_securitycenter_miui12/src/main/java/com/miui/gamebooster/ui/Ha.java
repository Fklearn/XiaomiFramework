package com.miui.gamebooster.ui;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;
import b.b.c.d;
import b.b.c.i.a;
import b.b.c.j.B;
import b.b.c.j.e;
import b.b.c.j.x;
import com.miui.gamebooster.model.C0398d;
import com.miui.gamebooster.model.k;
import com.miui.gamebooster.model.l;
import com.miui.gamebooster.videobox.settings.f;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class Ha extends a<ArrayList<k>> {

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ SelectGameActivity f4914b;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    Ha(SelectGameActivity selectGameActivity, Context context) {
        super(context);
        this.f4914b = selectGameActivity;
    }

    /* JADX WARNING: type inference failed for: r9v4, types: [android.content.Context, com.miui.gamebooster.ui.SelectGameActivity] */
    public ArrayList<k> loadInBackground() {
        List<ApplicationInfo> a2;
        ArrayList f = this.f4914b.r();
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        List<ApplicationInfo> list = null;
        try {
            list = d.a(0, B.c());
            if (B.j() == 0 && (a2 = d.a(0, 999)) != null) {
                list.addAll(a2);
            }
        } catch (Exception e) {
            Log.i(SelectGameActivity.f4983a, e.toString());
        }
        ArrayList arrayList3 = new ArrayList();
        ArrayList arrayList4 = new ArrayList();
        Iterator it = f.iterator();
        while (it.hasNext()) {
            ApplicationInfo applicationInfo = (ApplicationInfo) it.next();
            arrayList3.add(Integer.valueOf(applicationInfo.uid));
            arrayList4.add(applicationInfo.packageName);
        }
        ArrayList<String> a3 = f.a((ArrayList<String>) new ArrayList());
        for (ApplicationInfo next : list) {
            if (!a3.contains(next.packageName) && x.a(next) && !e.a((Context) this.f4914b, next.packageName, 0) && this.f4914b.h.getLaunchIntentForPackage(next.packageName) != null) {
                if (!arrayList3.contains(Integer.valueOf(next.uid)) || !arrayList4.contains(next.packageName)) {
                    arrayList.add(new C0398d(next, false, next.loadLabel(this.f4914b.h), next.loadIcon(this.f4914b.h)));
                } else {
                    arrayList2.add(new C0398d(next, true, next.loadLabel(this.f4914b.h), next.loadIcon(this.f4914b.h)));
                }
            }
        }
        this.f4914b.l.clear();
        this.f4914b.l.addAll(arrayList4);
        ArrayList<k> arrayList5 = new ArrayList<>();
        if (!arrayList2.isEmpty()) {
            k kVar = new k();
            kVar.a(l.ENABLED);
            kVar.a(this.f4914b.getResources().getQuantityString(R.plurals.install_game_count_title, arrayList2.size(), new Object[]{Integer.valueOf(arrayList2.size())}));
            kVar.a(arrayList2.size());
            kVar.a((ArrayList<C0398d>) arrayList2);
            arrayList5.add(kVar);
        }
        if (!arrayList.isEmpty()) {
            k kVar2 = new k();
            kVar2.a(l.DISABLED);
            kVar2.a(this.f4914b.getResources().getQuantityString(R.plurals.uninstall_game_count_title, arrayList.size(), new Object[]{Integer.valueOf(arrayList.size())}));
            kVar2.a(arrayList.size());
            kVar2.a((ArrayList<C0398d>) arrayList);
            arrayList5.add(kVar2);
        }
        return arrayList5;
    }
}
