package com.miui.gamebooster.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.miui.applicationlock.c.F;
import com.miui.common.stickydecoration.b.c;
import com.miui.securitycenter.R;
import java.util.Map;

class Za implements c {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Map f5040a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ WhiteListFragment f5041b;

    Za(WhiteListFragment whiteListFragment, Map map) {
        this.f5041b = whiteListFragment;
        this.f5040a = map;
    }

    public String getGroupName(int i) {
        return ((F) this.f5040a.get(Integer.valueOf(i))).b();
    }

    public View getGroupView(int i) {
        View inflate = LayoutInflater.from(this.f5041b.getContext()).inflate(this.f5041b.h() ? R.layout.game_select_list_header_view_land : R.layout.game_select_list_header_view, (ViewGroup) null);
        ((TextView) inflate.findViewById(R.id.header_title)).setText(((F) this.f5040a.get(Integer.valueOf(i))).b());
        return inflate;
    }
}
