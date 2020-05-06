package com.miui.securityscan.scanner;

import android.content.Context;
import b.b.c.j.d;
import com.miui.securityscan.b.g;
import com.miui.securityscan.model.AbsModel;
import com.miui.securityscan.model.GroupModel;
import java.util.List;
import java.util.ListIterator;

/* renamed from: com.miui.securityscan.scanner.h  reason: case insensitive filesystem */
class C0561h {

    /* renamed from: a  reason: collision with root package name */
    private static C0561h f7896a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public Context f7897b;

    private C0561h(Context context) {
        this.f7897b = context.getApplicationContext();
    }

    public static synchronized C0561h a(Context context) {
        C0561h hVar;
        synchronized (C0561h.class) {
            if (f7896a == null) {
                f7896a = new C0561h(context.getApplicationContext());
            }
            hVar = f7896a;
        }
        return hVar;
    }

    /* access modifiers changed from: private */
    public void a(List<GroupModel> list, List<String> list2) {
        if (list != null && list2 != null) {
            if (list2.contains("MIUI_UPDATE")) {
                list2.remove("MIUI_UPDATE");
            }
            for (GroupModel modelList : list) {
                ListIterator<AbsModel> listIterator = modelList.getModelList().listIterator();
                while (listIterator.hasNext()) {
                    if (list2.contains(listIterator.next().getItemKey())) {
                        listIterator.remove();
                    }
                }
            }
        }
    }

    public void a(g gVar) {
        d.a(new C0560g(this, gVar));
    }
}
