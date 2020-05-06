package com.miui.gamebooster.widget;

class d implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ HorizontalListView f5392a;

    d(HorizontalListView horizontalListView) {
        this.f5392a = horizontalListView;
    }

    public void run() {
        this.f5392a.requestLayout();
    }
}
