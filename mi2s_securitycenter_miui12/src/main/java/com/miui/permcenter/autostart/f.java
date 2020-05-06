package com.miui.permcenter.autostart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.miui.permcenter.a;
import com.miui.permcenter.autostart.l;
import com.miui.permission.PermissionManager;

class f implements l.a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AutoStartManagementActivity f6072a;

    f(AutoStartManagementActivity autoStartManagementActivity) {
        this.f6072a = autoStartManagementActivity;
    }

    /* JADX WARNING: type inference failed for: r5v2, types: [android.content.Context, com.miui.permcenter.autostart.AutoStartManagementActivity] */
    public void a(int i, View view, a aVar) {
        if (i != this.f6072a.f6048b) {
            int unused = this.f6072a.f6048b = i;
            View unused2 = this.f6072a.f6049c = view;
            if (aVar != null) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("pkg_label", aVar.d());
                bundle.putString("pkg_name", aVar.e());
                bundle.putInt("action", aVar.f().get(Long.valueOf(PermissionManager.PERM_ID_AUTOSTART)).intValue());
                bundle.putInt("pkg_position", this.f6072a.f6048b);
                bundle.putBoolean("white_list", (this.f6072a.f6050d == null || this.f6072a.f6050d.size() == 0) ? false : this.f6072a.f6050d.contains(aVar.e()));
                intent.putExtras(bundle);
                intent.setClass(this.f6072a, AutoStartDetailManagementActivity.class);
                this.f6072a.startActivityForResult(intent, 1);
            }
        }
    }
}
