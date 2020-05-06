package com.miui.permcenter.root;

import android.content.Context;
import b.b.c.i.a;
import com.miui.permcenter.autostart.i;
import com.miui.permcenter.autostart.j;
import com.miui.permcenter.n;
import com.miui.securitycenter.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

class b extends a<ArrayList<i>> {

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ RootManagementActivity f6497b;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    b(RootManagementActivity rootManagementActivity, Context context) {
        super(context);
        this.f6497b = rootManagementActivity;
    }

    public ArrayList<i> loadInBackground() {
        ArrayList<com.miui.permcenter.a> a2 = n.a(this.f6497b.getApplicationContext(), 512);
        Collections.sort(a2, new com.miui.permcenter.b());
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        Iterator<com.miui.permcenter.a> it = a2.iterator();
        while (it.hasNext()) {
            com.miui.permcenter.a next = it.next();
            if (next.f().get(512L).intValue() == 3) {
                arrayList.add(next);
            } else {
                arrayList2.add(next);
            }
        }
        ArrayList<i> arrayList3 = new ArrayList<>();
        if (!arrayList.isEmpty()) {
            i iVar = new i();
            iVar.a(j.ENABLED);
            iVar.a(this.f6497b.getResources().getQuantityString(R.plurals.hints_get_root_enable_title, arrayList.size(), new Object[]{Integer.valueOf(arrayList.size())}));
            iVar.a((ArrayList<com.miui.permcenter.a>) arrayList);
            arrayList3.add(iVar);
        }
        if (!arrayList2.isEmpty()) {
            i iVar2 = new i();
            iVar2.a(j.DISABLED);
            iVar2.a(this.f6497b.getResources().getQuantityString(R.plurals.hints_get_root_disable_title, arrayList2.size(), new Object[]{Integer.valueOf(arrayList2.size())}));
            iVar2.a((ArrayList<com.miui.permcenter.a>) arrayList2);
            arrayList3.add(iVar2);
        }
        return arrayList3;
    }
}
