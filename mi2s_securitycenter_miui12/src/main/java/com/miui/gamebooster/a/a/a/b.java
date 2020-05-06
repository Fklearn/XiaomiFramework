package com.miui.gamebooster.a.a.a;

import android.view.View;
import android.widget.TextView;
import com.miui.gamebooster.customview.b.c;
import com.miui.gamebooster.customview.b.d;
import com.miui.gamebooster.customview.b.g;
import com.miui.gamebooster.model.C0398d;
import com.miui.securitycenter.R;

public class b implements d {
    public void a(g gVar, Object obj, int i) {
        ((TextView) gVar.itemView.findViewById(R.id.header_title)).setText(R.string.gs_advanced_setting_summary);
    }

    public boolean a() {
        return false;
    }

    public boolean a(Object obj, int i) {
        return (obj instanceof C0398d) && ((C0398d) obj).b() == null;
    }

    public int b() {
        return R.layout.game_select_list_header_view_land;
    }

    public /* synthetic */ View c() {
        return c.a(this);
    }
}
