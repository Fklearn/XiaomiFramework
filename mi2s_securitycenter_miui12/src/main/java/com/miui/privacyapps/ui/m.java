package com.miui.privacyapps.ui;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import b.b.k.d;
import com.miui.common.stickydecoration.b.c;
import com.miui.securitycenter.R;
import java.util.Map;

class m implements c {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Map f7408a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ n f7409b;

    m(n nVar, Map map) {
        this.f7409b = nVar;
        this.f7408a = map;
    }

    public String getGroupName(int i) {
        return ((d) this.f7408a.get(Integer.valueOf(i))).a();
    }

    public View getGroupView(int i) {
        View inflate = this.f7409b.q.getLayoutInflater().inflate(R.layout.item_group_recyclerview_decoration, (ViewGroup) null, false);
        ((TextView) inflate.findViewById(R.id.header_title)).setText(((d) this.f7408a.get(Integer.valueOf(i))).a());
        return inflate;
    }
}
