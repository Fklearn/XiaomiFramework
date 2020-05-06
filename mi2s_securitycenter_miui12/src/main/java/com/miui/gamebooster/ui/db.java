package com.miui.gamebooster.ui;

import android.content.Context;
import android.widget.AbsListView;
import b.b.c.j.i;
import com.miui.securitycenter.R;

class db implements AbsListView.OnScrollListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ib f5058a;

    db(ib ibVar) {
        this.f5058a = ibVar;
    }

    public void onScroll(AbsListView absListView, int i, int i2, int i3) {
        if (this.f5058a.e() > 40) {
            this.f5058a.f5075b.setPadding(this.f5058a.getResources().getDimensionPixelOffset(R.dimen.gb_wonderful_main_page_left_right_margin), i.a((Context) this.f5058a.i, 10.0f), this.f5058a.getResources().getDimensionPixelOffset(R.dimen.gb_wonderful_main_page_left_right_margin), 0);
        } else {
            this.f5058a.f5075b.setPadding(this.f5058a.getResources().getDimensionPixelOffset(R.dimen.gb_wonderful_main_page_left_right_margin), 0, this.f5058a.getResources().getDimensionPixelOffset(R.dimen.gb_wonderful_main_page_left_right_margin), 0);
        }
    }

    public void onScrollStateChanged(AbsListView absListView, int i) {
    }
}
