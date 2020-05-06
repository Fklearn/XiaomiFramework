package com.miui.privacyapps.ui;

import android.content.Context;
import android.os.UserHandle;
import android.os.UserManager;
import b.b.c.i.a;
import b.b.c.j.x;
import b.b.k.c;
import java.util.ArrayList;
import java.util.List;

class b extends a<List<c>> {

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ PrivacyAppsActivity f7395b;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    b(PrivacyAppsActivity privacyAppsActivity, Context context) {
        super(context);
        this.f7395b = privacyAppsActivity;
    }

    /* JADX WARNING: type inference failed for: r5v0, types: [android.content.Context, com.miui.privacyapps.ui.PrivacyAppsActivity] */
    public List<c> loadInBackground() {
        ArrayList arrayList = new ArrayList();
        for (UserHandle identifier : ((UserManager) this.f7395b.getSystemService("user")).getUserProfiles()) {
            int identifier2 = identifier.getIdentifier();
            for (String next : this.f7395b.h.getAllPrivacyApps(identifier2)) {
                if (x.a((Context) this.f7395b, next, identifier2)) {
                    c cVar = new c();
                    cVar.b(next);
                    cVar.b(identifier2);
                    cVar.a(false);
                    arrayList.add(cVar);
                }
            }
        }
        return arrayList;
    }
}
