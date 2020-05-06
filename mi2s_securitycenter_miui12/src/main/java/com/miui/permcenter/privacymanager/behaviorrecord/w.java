package com.miui.permcenter.privacymanager.behaviorrecord;

import android.content.Context;
import com.miui.permcenter.privacymanager.behaviorrecord.PrivacyDetailActivity;
import com.miui.permcenter.s;
import com.miui.permission.PermissionManager;

class w implements PrivacyDetailActivity.i {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ PrivacyDetailActivity f6465a;

    w(PrivacyDetailActivity privacyDetailActivity) {
        this.f6465a = privacyDetailActivity;
    }

    /* JADX WARNING: type inference failed for: r1v2, types: [android.content.Context, com.miui.permcenter.privacymanager.behaviorrecord.PrivacyDetailActivity] */
    public void a(Long l, int i) {
        boolean z = true;
        if (i == 1 || i == 2) {
            this.f6465a.l();
        }
        if (l.longValue() == PermissionManager.PERM_ID_AUTOSTART) {
            ? r1 = this.f6465a;
            String a2 = r1.F;
            if (i != 3) {
                z = false;
            }
            s.a((Context) r1, a2, z);
        }
        this.f6465a.aa.put(l, Integer.valueOf(i));
    }
}
