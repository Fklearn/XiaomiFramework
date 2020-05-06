package com.miui.permcenter.permissions;

import androidx.preference.Preference;
import com.miui.permcenter.n;
import com.miui.permission.PermissionInfo;
import java.lang.ref.WeakReference;

class B implements Preference.c {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ long f6211a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ String f6212b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ Integer f6213c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ PermissionInfo f6214d;
    final /* synthetic */ C e;

    B(C c2, long j, String str, Integer num, PermissionInfo permissionInfo) {
        this.e = c2;
        this.f6211a = j;
        this.f6212b = str;
        this.f6213c = num;
        this.f6214d = permissionInfo;
    }

    public boolean onPreferenceClick(Preference preference) {
        WeakReference weakReference = new WeakReference(this.e);
        C c2 = (C) weakReference.get();
        if (c2 == null || c2.getActivity().isFinishing() || c2.getActivity().isDestroyed()) {
            return true;
        }
        n.a(c2.getActivity(), this.e.f6216b, this.f6211a, this.f6212b, this.f6213c.intValue(), (n.c) weakReference.get(), (this.f6214d.getFlags() & 16) != 0, false, this.e.i, (String) this.e.h.get(Long.valueOf(this.f6211a)), (this.f6214d.getFlags() & 64) != 0);
        return true;
    }
}
