package com.miui.gamebooster.a.b.a;

import android.view.View;
import com.miui.gamebooster.customview.b.c;
import com.miui.gamebooster.customview.b.d;
import com.miui.gamebooster.customview.b.g;
import com.miui.gamebooster.model.C0398d;
import com.miui.securitycenter.R;

public class a implements d<C0398d> {

    /* renamed from: a  reason: collision with root package name */
    private final boolean f4032a;

    public a(boolean z) {
        this.f4032a = z;
    }

    public void a(g gVar, C0398d dVar, int i) {
        gVar.a((int) R.id.header_title, (String) dVar.d());
    }

    public boolean a() {
        return false;
    }

    public boolean a(C0398d dVar, int i) {
        return dVar.b() == null;
    }

    public int b() {
        return this.f4032a ? R.layout.game_select_list_header_view_land : R.layout.game_select_list_header_view;
    }

    public /* synthetic */ View c() {
        return c.a(this);
    }
}
