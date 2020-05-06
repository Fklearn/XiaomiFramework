package com.miui.applicationlock;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.miui.applicationlock.c.F;
import com.miui.applicationlock.c.G;
import com.miui.common.stickydecoration.b.c;
import com.miui.securitycenter.R;
import java.util.Map;

/* renamed from: com.miui.applicationlock.m  reason: case insensitive filesystem */
class C0287m implements c {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Map f3362a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ C0312y f3363b;

    C0287m(C0312y yVar, Map map) {
        this.f3363b = yVar;
        this.f3362a = map;
    }

    public String getGroupName(int i) {
        return ((F) this.f3362a.get(Integer.valueOf(i))).b();
    }

    public View getGroupView(int i) {
        View inflate = this.f3363b.z.getLayoutInflater().inflate(R.layout.item_group_recyclerview_decoration, (ViewGroup) null, false);
        ((TextView) inflate.findViewById(R.id.header_title)).setText(((F) this.f3362a.get(Integer.valueOf(i))).b());
        if (((F) this.f3362a.get(Integer.valueOf(i))).c() == G.RECOMMEND) {
            TextView textView = (TextView) inflate.findViewById(R.id.header_operate);
            textView.setVisibility(0);
            textView.setText(this.f3363b.f.b() ? R.string.applock_unlock_all : R.string.applock_lock_all);
        } else {
            ((TextView) inflate.findViewById(R.id.header_operate)).setVisibility(8);
        }
        return inflate;
    }
}
