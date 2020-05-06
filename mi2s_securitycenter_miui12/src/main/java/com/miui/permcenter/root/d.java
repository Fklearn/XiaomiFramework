package com.miui.permcenter.root;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.miui.common.stickydecoration.b.c;
import com.miui.securitycenter.R;

class d implements c {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ SparseArray f6500a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ RootManagementActivity f6501b;

    d(RootManagementActivity rootManagementActivity, SparseArray sparseArray) {
        this.f6501b = rootManagementActivity;
        this.f6500a = sparseArray;
    }

    public String getGroupName(int i) {
        return (String) this.f6500a.get(i);
    }

    public View getGroupView(int i) {
        View inflate = this.f6501b.getLayoutInflater().inflate(R.layout.item_group_recyclerview_decoration, (ViewGroup) null, false);
        ((TextView) inflate.findViewById(R.id.header_title)).setText((String) this.f6500a.get(i));
        return inflate;
    }
}
