package com.miui.privacyapps.ui;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.miui.common.stickydecoration.b.c;
import com.miui.securitycenter.R;
import java.util.Map;

class d implements c {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Map f7397a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ n f7398b;

    d(n nVar, Map map) {
        this.f7398b = nVar;
        this.f7397a = map;
    }

    public String getGroupName(int i) {
        return ((b.b.k.d) this.f7397a.get(Integer.valueOf(i))).a();
    }

    public View getGroupView(int i) {
        View inflate = this.f7398b.q.getLayoutInflater().inflate(R.layout.item_group_recyclerview_decoration, (ViewGroup) null, false);
        ((TextView) inflate.findViewById(R.id.header_title)).setText(((b.b.k.d) this.f7397a.get(Integer.valueOf(i))).a());
        return inflate;
    }
}
