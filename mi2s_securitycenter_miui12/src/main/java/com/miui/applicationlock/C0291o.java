package com.miui.applicationlock;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.miui.applicationlock.c.F;
import com.miui.common.stickydecoration.b.c;
import com.miui.securitycenter.R;
import java.util.Map;

/* renamed from: com.miui.applicationlock.o  reason: case insensitive filesystem */
class C0291o implements c {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Map f3367a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ C0312y f3368b;

    C0291o(C0312y yVar, Map map) {
        this.f3368b = yVar;
        this.f3367a = map;
    }

    public String getGroupName(int i) {
        return ((F) this.f3367a.get(Integer.valueOf(i))).b();
    }

    public View getGroupView(int i) {
        View inflate = this.f3368b.z.getLayoutInflater().inflate(R.layout.item_group_recyclerview_decoration, (ViewGroup) null, false);
        ((TextView) inflate.findViewById(R.id.header_title)).setText(((F) this.f3367a.get(Integer.valueOf(i))).b());
        return inflate;
    }
}
