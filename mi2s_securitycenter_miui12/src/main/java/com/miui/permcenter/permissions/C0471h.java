package com.miui.permcenter.permissions;

import android.content.Context;
import b.b.c.i.a;
import java.util.ArrayList;

/* renamed from: com.miui.permcenter.permissions.h  reason: case insensitive filesystem */
class C0471h extends a<r> {

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ AppPermissionsUseActivity f6269b;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    C0471h(AppPermissionsUseActivity appPermissionsUseActivity, Context context) {
        super(context);
        this.f6269b = appPermissionsUseActivity;
    }

    public r loadInBackground() {
        r rVar = new r();
        rVar.f6290a = new ArrayList();
        rVar.f6291b = new ArrayList();
        for (String str : this.f6269b.f6206d) {
            s sVar = new s();
            sVar.f6292a = str;
            sVar.f6293b = this.f6269b.g.get(str);
            rVar.f6290a.add(sVar);
        }
        for (String str2 : this.f6269b.e) {
            s sVar2 = new s();
            sVar2.f6292a = str2;
            sVar2.f6293b = this.f6269b.g.get(str2);
            rVar.f6291b.add(sVar2);
        }
        return rVar;
    }
}
