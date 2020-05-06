package com.miui.permcenter.install;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.miui.common.stickydecoration.b.c;
import com.miui.securitycenter.R;

class m implements c {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ SparseArray f6162a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ PackageManagerActivity f6163b;

    m(PackageManagerActivity packageManagerActivity, SparseArray sparseArray) {
        this.f6163b = packageManagerActivity;
        this.f6162a = sparseArray;
    }

    public String getGroupName(int i) {
        return (String) this.f6162a.get(i);
    }

    public View getGroupView(int i) {
        View inflate = this.f6163b.getLayoutInflater().inflate(R.layout.item_group_recyclerview_decoration, (ViewGroup) null, false);
        ((TextView) inflate.findViewById(R.id.header_title)).setText((String) this.f6162a.get(i));
        return inflate;
    }
}
