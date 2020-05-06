package com.miui.permcenter.settings.model;

import android.content.Context;
import android.view.View;
import com.miui.permcenter.a.a;
import com.miui.permcenter.privacymanager.behaviorrecord.AppBehaviorRecordActivity;
import com.miui.permission.PermissionManager;
import com.miui.securitycenter.R;

class h implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ PermissionUseTotalPreference f6550a;

    h(PermissionUseTotalPreference permissionUseTotalPreference) {
        this.f6550a = permissionUseTotalPreference;
    }

    public void onClick(View view) {
        String str;
        switch (view.getId()) {
            case R.id.icon_call /*2131296982*/:
                this.f6550a.a();
                this.f6550a.f6539c.setSelected(true);
                this.f6550a.a(16);
                long unused = this.f6550a.p = 16;
                str = "use_permission_call";
                break;
            case R.id.icon_contacts /*2131296983*/:
                this.f6550a.a();
                this.f6550a.f6540d.setSelected(true);
                this.f6550a.a(8);
                long unused2 = this.f6550a.p = 8;
                str = "use_permission_contacts";
                break;
            case R.id.icon_location /*2131296994*/:
                this.f6550a.a();
                this.f6550a.f6538b.setSelected(true);
                this.f6550a.a(32);
                long unused3 = this.f6550a.p = 32;
                str = "use_permission_location";
                break;
            case R.id.icon_record /*2131296995*/:
                this.f6550a.a();
                this.f6550a.e.setSelected(true);
                this.f6550a.a((long) PermissionManager.PERM_ID_AUDIO_RECORDER);
                long unused4 = this.f6550a.p = PermissionManager.PERM_ID_AUDIO_RECORDER;
                str = "use_permission_record";
                break;
            case R.id.icon_storage /*2131296998*/:
                this.f6550a.a();
                this.f6550a.f.setSelected(true);
                this.f6550a.a((long) PermissionManager.PERM_ID_EXTERNAL_STORAGE);
                long unused5 = this.f6550a.p = PermissionManager.PERM_ID_EXTERNAL_STORAGE;
                str = "use_permission_storage";
                break;
            case R.id.look_all /*2131297283*/:
                this.f6550a.mContext.startActivity(AppBehaviorRecordActivity.b("statics"));
                str = "look_all_use_permission";
                break;
            case R.id.settings /*2131297651*/:
                if (this.f6550a.o.get() != null) {
                    PermissionUseTotalPreference permissionUseTotalPreference = this.f6550a;
                    permissionUseTotalPreference.a((Context) permissionUseTotalPreference.o.get());
                    return;
                }
                return;
            default:
                return;
        }
        a.e(str);
    }
}
