package com.miui.gamebooster.widget;

import android.database.DataSetObserver;

class c extends DataSetObserver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ HorizontalListView f5391a;

    c(HorizontalListView horizontalListView) {
        this.f5391a = horizontalListView;
    }

    public void onChanged() {
        boolean unused = this.f5391a.g = true;
        boolean unused2 = this.f5391a.u = false;
        this.f5391a.h();
        this.f5391a.invalidate();
        this.f5391a.requestLayout();
    }

    public void onInvalidated() {
        boolean unused = this.f5391a.u = false;
        this.f5391a.h();
        this.f5391a.g();
        this.f5391a.invalidate();
        this.f5391a.requestLayout();
    }
}
