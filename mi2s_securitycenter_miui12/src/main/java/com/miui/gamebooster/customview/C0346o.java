package com.miui.gamebooster.customview;

import android.content.pm.ResolveInfo;
import android.view.View;
import android.widget.AdapterView;
import com.miui.gamebooster.f.c;
import com.miui.gamebooster.model.j;
import com.miui.gamebooster.p.r;

/* renamed from: com.miui.gamebooster.customview.o  reason: case insensitive filesystem */
class C0346o implements AdapterView.OnItemClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ r f4218a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ GameBoxView f4219b;

    C0346o(GameBoxView gameBoxView, r rVar) {
        this.f4219b = gameBoxView;
        this.f4218a = rVar;
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        j item = this.f4219b.s.getItem(i);
        if (item != null) {
            item.a(this.f4218a, view);
            this.f4218a.i();
            ResolveInfo d2 = item.d();
            if (d2 != null) {
                c.a().a(d2.activityInfo.applicationInfo.packageName);
            }
        }
    }
}
