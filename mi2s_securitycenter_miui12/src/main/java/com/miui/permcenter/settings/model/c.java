package com.miui.permcenter.settings.model;

import android.content.Intent;
import android.view.View;
import com.miui.permcenter.a.a;
import com.miui.permcenter.permissions.AppPermissionItemActivity;
import com.miui.permcenter.permissions.AppPermissionsTabActivity;
import com.miui.permission.PermissionManager;
import com.miui.securitycenter.R;

class c implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ DangerPermissionPreference f6544a;

    c(DangerPermissionPreference dangerPermissionPreference) {
        this.f6544a = dangerPermissionPreference;
    }

    public void onClick(View view) {
        String str;
        Intent intent = new Intent();
        String str2 = "-2";
        switch (view.getId()) {
            case R.id.container_call /*2131296650*/:
                intent.setClass(this.f6544a.mContext, AppPermissionItemActivity.class);
                if (com.miui.permcenter.privacymanager.b.c.a(this.f6544a.mContext)) {
                    str2 = String.valueOf(16);
                }
                intent.putExtra("permissionID", str2);
                str = "permission_state_call";
                break;
            case R.id.container_contacts /*2131296653*/:
                intent.setClass(this.f6544a.mContext, AppPermissionItemActivity.class);
                if (com.miui.permcenter.privacymanager.b.c.a(this.f6544a.mContext)) {
                    str2 = String.valueOf(8);
                }
                intent.putExtra("permissionID", str2);
                str = "permission_state_contacts";
                break;
            case R.id.container_location /*2131296660*/:
                intent.setClass(this.f6544a.mContext, AppPermissionItemActivity.class);
                intent.putExtra("permissionID", String.valueOf(32));
                str = "permission_state_location";
                break;
            case R.id.container_record /*2131296662*/:
                intent.setClass(this.f6544a.mContext, AppPermissionItemActivity.class);
                intent.putExtra("permissionID", String.valueOf(PermissionManager.PERM_ID_AUDIO_RECORDER));
                str = "permission_state_record";
                break;
            case R.id.container_storage /*2131296665*/:
                intent.setClass(this.f6544a.mContext, AppPermissionItemActivity.class);
                intent.putExtra("permissionID", String.valueOf(PermissionManager.PERM_ID_EXTERNAL_STORAGE));
                str = "permission_state_storage";
                break;
            case R.id.look_all /*2131297283*/:
                intent.setClass(this.f6544a.mContext, AppPermissionsTabActivity.class);
                intent.putExtra("select_navi_item", 1);
                str = "look_all_permission_state";
                break;
            default:
                this.f6544a.mContext.startActivity(intent);
        }
        a.e(str);
        this.f6544a.mContext.startActivity(intent);
    }
}
