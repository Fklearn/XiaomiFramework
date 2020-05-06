package com.miui.optimizecenter.storage.b;

import android.content.Context;
import android.content.DialogInterface;
import b.b.c.j.B;
import b.b.o.g.e;
import com.miui.optimizecenter.storage.d.a;
import com.miui.securitycenter.Application;

class d implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ com.miui.optimizecenter.storage.d.d f5719a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Context f5720b;

    d(com.miui.optimizecenter.storage.d.d dVar, Context context) {
        this.f5719a = dVar;
        this.f5720b = context;
    }

    private boolean a(String str) {
        try {
            Class<?> cls = Class.forName("com.android.settingslib.RestrictedLockUtils");
            Object a2 = e.a(cls, Class.forName("com.android.settingslib.RestrictedLockUtils$EnforcedAdmin"), "checkIfRestrictionEnforced", (Class<?>[]) new Class[]{Context.class, String.class, Integer.TYPE}, Application.d(), str, Integer.valueOf(B.j()));
            boolean booleanValue = ((Boolean) e.a(cls, Boolean.TYPE, "hasBaseUserRestriction", (Class<?>[]) new Class[]{Context.class, String.class, Integer.TYPE}, Application.d(), str, Integer.valueOf(B.j()))).booleanValue();
            if (a2 != null && !booleanValue) {
                e.a(cls, "sendShowAdminSupportDetailsIntent", (Class<?>[]) new Class[]{Context.class, String.class, Integer.TYPE}, Application.d(), str, Integer.valueOf(B.j()));
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (!a("no_physical_media")) {
            a a2 = this.f5719a.a();
            if (a2 == null || !a2.d() || !a("no_usb_file_transfer")) {
                new com.miui.optimizecenter.storage.c.a(this.f5720b, this.f5719a).execute(new Void[0]);
            }
        }
    }
}
