package com.miui.gamebooster.view;

class f implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ DragGridView f5274a;

    f(DragGridView dragGridView) {
        this.f5274a = dragGridView;
    }

    public void run() {
        if (this.f5274a.G != this.f5274a.H) {
            DragGridView dragGridView = this.f5274a;
            dragGridView.smoothScrollBy(dragGridView.G * 4, 2);
            DragGridView dragGridView2 = this.f5274a;
            dragGridView2.postDelayed(dragGridView2.K, 2);
        }
    }
}
