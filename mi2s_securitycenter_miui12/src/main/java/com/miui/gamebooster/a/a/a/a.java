package com.miui.gamebooster.a.a.a;

import android.view.View;
import android.widget.ImageView;
import b.b.c.j.B;
import b.b.c.j.r;
import com.miui.gamebooster.customview.b.c;
import com.miui.gamebooster.customview.b.d;
import com.miui.gamebooster.customview.b.g;
import com.miui.gamebooster.model.C0398d;
import com.miui.securitycenter.R;

public class a implements d<C0398d> {

    /* renamed from: a  reason: collision with root package name */
    private final boolean f4029a;

    public a(boolean z) {
        this.f4029a = z;
    }

    public void a(g gVar, C0398d dVar, int i) {
        String str;
        String str2;
        if (B.c(dVar.b().uid) == 999) {
            str = dVar.b().packageName;
            str2 = "pkg_icon_xspace://";
        } else {
            str = dVar.b().packageName;
            str2 = "pkg_icon://";
        }
        r.a(str2.concat(str), (ImageView) gVar.b(R.id.icon), r.f, gVar.b().getResources().getDrawable(R.drawable.gb_def_icon));
        gVar.a((int) R.id.title, (String) dVar.d());
    }

    public boolean a() {
        return true;
    }

    public boolean a(C0398d dVar, int i) {
        return dVar.b() != null;
    }

    public int b() {
        return this.f4029a ? R.layout.advance_settings_list_item_view_land : R.layout.advance_settings_list_item_view;
    }

    public /* synthetic */ View c() {
        return c.a(this);
    }
}
