package com.miui.gamebooster.view;

import android.view.ViewTreeObserver;

class g implements ViewTreeObserver.OnPreDrawListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ViewTreeObserver f5275a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ int f5276b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ DragGridView f5277c;

    g(DragGridView dragGridView, ViewTreeObserver viewTreeObserver, int i) {
        this.f5277c = dragGridView;
        this.f5275a = viewTreeObserver;
        this.f5276b = i;
    }

    public boolean onPreDraw() {
        this.f5275a.removeOnPreDrawListener(this);
        DragGridView dragGridView = this.f5277c;
        dragGridView.a(dragGridView.g, this.f5276b);
        int unused = this.f5277c.g = this.f5276b;
        return true;
    }
}
