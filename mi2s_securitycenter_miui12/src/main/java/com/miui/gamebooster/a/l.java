package com.miui.gamebooster.a;

import android.widget.AbsListView;
import com.miui.securitycenter.R;

class l implements AbsListView.OnScrollListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ v f4050a;

    l(v vVar) {
        this.f4050a = vVar;
    }

    public void onScroll(AbsListView absListView, int i, int i2, int i3) {
    }

    public void onScrollStateChanged(AbsListView absListView, int i) {
        boolean z = false;
        if (!(this.f4050a.i.getChildAt(0) == null || this.f4050a.i.getChildAt(0).getTop() == 0)) {
            z = true;
        }
        this.f4050a.h.setImageResource(z ? R.drawable.gb_feed_scroll_to_top : R.drawable.gb_feed_collapse);
    }
}
