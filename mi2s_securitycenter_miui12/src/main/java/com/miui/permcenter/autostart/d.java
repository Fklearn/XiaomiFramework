package com.miui.permcenter.autostart;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.miui.common.stickydecoration.b.c;
import com.miui.securitycenter.R;

class d implements c {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AutoStartDetailManagementActivity f6069a;

    d(AutoStartDetailManagementActivity autoStartDetailManagementActivity) {
        this.f6069a = autoStartDetailManagementActivity;
    }

    public String getGroupName(int i) {
        AutoStartDetailManagementActivity autoStartDetailManagementActivity;
        int i2;
        if (i == 0) {
            autoStartDetailManagementActivity = this.f6069a;
            i2 = R.string.pm_auto_start_by_system;
        } else if (i != 1) {
            return "";
        } else {
            autoStartDetailManagementActivity = this.f6069a;
            i2 = R.string.pm_auto_start_by_other;
        }
        return autoStartDetailManagementActivity.getString(i2);
    }

    public View getGroupView(int i) {
        View inflate = this.f6069a.getLayoutInflater().inflate(R.layout.pm_auto_start_list_header_view, (ViewGroup) null, false);
        ((TextView) inflate.findViewById(R.id.header_title)).setText(getGroupName(i));
        return inflate;
    }
}
