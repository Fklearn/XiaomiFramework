package com.miui.gamebooster.a.b.a;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import b.b.c.j.B;
import b.b.c.j.r;
import com.miui.gamebooster.customview.b.c;
import com.miui.gamebooster.customview.b.d;
import com.miui.gamebooster.customview.b.g;
import com.miui.gamebooster.model.C0398d;
import com.miui.gamebooster.widget.SwitchButton;
import com.miui.securitycenter.R;
import miui.widget.SlidingButton;

public class b implements d<C0398d> {

    /* renamed from: a  reason: collision with root package name */
    private final boolean f4033a;

    /* renamed from: b  reason: collision with root package name */
    private final CompoundButton.OnCheckedChangeListener f4034b;

    public b(boolean z, CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        this.f4033a = z;
        this.f4034b = onCheckedChangeListener;
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
        SlidingButton b2 = gVar.b(R.id.sliding_button);
        b2.setTag(dVar);
        if (b2 instanceof SlidingButton) {
            SlidingButton slidingButton = b2;
            slidingButton.setOnPerformCheckedChangeListener((CompoundButton.OnCheckedChangeListener) null);
            slidingButton.setChecked(dVar.e());
            slidingButton.setOnPerformCheckedChangeListener(this.f4034b);
        } else if (b2 instanceof SwitchButton) {
            SwitchButton switchButton = (SwitchButton) b2;
            switchButton.setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener) null);
            switchButton.setCheckedImmediatelyNoEvent(dVar.e());
            switchButton.setOnCheckedChangeListener(this.f4034b);
        }
    }

    public boolean a() {
        return true;
    }

    public boolean a(C0398d dVar, int i) {
        return dVar.b() != null;
    }

    public int b() {
        return this.f4033a ? R.layout.game_select_list_item_view_land : R.layout.game_select_list_item_view;
    }

    public /* synthetic */ View c() {
        return c.a(this);
    }
}
