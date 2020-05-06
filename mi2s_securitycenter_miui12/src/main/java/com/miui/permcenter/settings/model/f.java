package com.miui.permcenter.settings.model;

import android.util.Log;
import com.miui.permcenter.a.a;
import com.miui.permcenter.privacymanager.a.d;
import com.miui.permcenter.settings.model.e;
import java.util.ArrayList;
import java.util.HashMap;

class f implements e.a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ boolean f6547a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ PermissionUseTotalPreference f6548b;

    f(PermissionUseTotalPreference permissionUseTotalPreference, boolean z) {
        this.f6548b = permissionUseTotalPreference;
        this.f6547a = z;
    }

    public void a(HashMap<Long, ArrayList<d>> hashMap) {
        Log.i("PermissionUseTotal", "loadComplete");
        HashMap unused = this.f6548b.n = hashMap;
        if (this.f6547a) {
            this.f6548b.f6537a.a(false);
            this.f6548b.b();
            if (this.f6548b.p == -1) {
                this.f6548b.a(32);
                this.f6548b.f6538b.setSelected(true);
                a.e("use_permission_location");
                return;
            }
            PermissionUseTotalPreference permissionUseTotalPreference = this.f6548b;
            permissionUseTotalPreference.a(permissionUseTotalPreference.p);
        }
    }
}
