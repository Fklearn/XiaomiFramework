package com.miui.gamebooster.customview;

import android.view.View;
import android.widget.AdapterView;
import com.miui.gamebooster.d.d;
import com.miui.gamebooster.model.j;
import com.miui.gamebooster.p.r;

/* renamed from: com.miui.gamebooster.customview.n  reason: case insensitive filesystem */
class C0345n implements AdapterView.OnItemClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ r f4216a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ GameBoxView f4217b;

    C0345n(GameBoxView gameBoxView, r rVar) {
        this.f4217b = gameBoxView;
        this.f4216a = rVar;
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        j item = this.f4217b.o.getItem(i);
        if (item != null) {
            item.a(this.f4216a, view);
            d c2 = item.b().c();
            if (!d.DND.equals(c2) && !d.WIFI.equals(c2) && !d.SIMCARD.equals(c2) && !d.IMMERSION.equals(c2) && !d.DISPLAY.equals(c2)) {
                this.f4216a.i();
            }
        }
    }
}
