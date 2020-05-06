package com.miui.gamebooster.videobox.adapter.a;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import b.b.c.j.r;
import com.miui.gamebooster.customview.b.c;
import com.miui.gamebooster.customview.b.d;
import com.miui.gamebooster.customview.b.g;
import com.miui.gamebooster.n.d.n;
import com.miui.securitycenter.R;

public class a implements d<n> {
    public void a(g gVar, n nVar, int i) {
        r.a(nVar.a(), (ImageView) gVar.b(R.id.app_icon), r.f, (int) R.drawable.card_icon_default);
        gVar.a((int) R.id.app_name, nVar.b());
        ((CheckBox) gVar.b(R.id.cb_switch)).setChecked(nVar.d());
    }

    public boolean a() {
        return true;
    }

    public boolean a(n nVar, int i) {
        return true;
    }

    public int b() {
        return R.layout.videobox_manager_app_list_item_layout;
    }

    public /* synthetic */ View c() {
        return c.a(this);
    }
}
