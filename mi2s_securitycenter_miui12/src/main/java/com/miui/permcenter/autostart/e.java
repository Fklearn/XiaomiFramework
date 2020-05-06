package com.miui.permcenter.autostart;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.miui.common.stickydecoration.b.c;
import com.miui.securitycenter.R;

class e implements c {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ SparseArray f6070a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ AutoStartManagementActivity f6071b;

    e(AutoStartManagementActivity autoStartManagementActivity, SparseArray sparseArray) {
        this.f6071b = autoStartManagementActivity;
        this.f6070a = sparseArray;
    }

    public String getGroupName(int i) {
        return (String) this.f6070a.get(i);
    }

    public View getGroupView(int i) {
        View inflate = this.f6071b.getLayoutInflater().inflate(R.layout.item_group_recyclerview_decoration, (ViewGroup) null, false);
        ((TextView) inflate.findViewById(R.id.header_title)).setText((String) this.f6070a.get(i));
        return inflate;
    }
}
