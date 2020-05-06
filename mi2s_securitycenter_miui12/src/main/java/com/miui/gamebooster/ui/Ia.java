package com.miui.gamebooster.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.miui.applicationlock.c.F;
import com.miui.common.stickydecoration.b.c;
import com.miui.securitycenter.R;
import java.util.Map;

class Ia implements c {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Map f4916a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ SelectGameActivity f4917b;

    Ia(SelectGameActivity selectGameActivity, Map map) {
        this.f4917b = selectGameActivity;
        this.f4916a = map;
    }

    public String getGroupName(int i) {
        return ((F) this.f4916a.get(Integer.valueOf(i))).b();
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [android.content.Context, com.miui.gamebooster.ui.SelectGameActivity] */
    public View getGroupView(int i) {
        View inflate = LayoutInflater.from(this.f4917b).inflate(R.layout.game_select_list_header_view, (ViewGroup) null);
        ((TextView) inflate.findViewById(R.id.header_title)).setText(((F) this.f4916a.get(Integer.valueOf(i))).b());
        return inflate;
    }
}
