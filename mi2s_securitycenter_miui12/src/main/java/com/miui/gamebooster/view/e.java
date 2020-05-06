package com.miui.gamebooster.view;

class e implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ DragGridView f5273a;

    e(DragGridView dragGridView) {
        this.f5273a = dragGridView;
    }

    public void run() {
        boolean unused = this.f5273a.f5251b = true;
        this.f5273a.l.vibrate(50);
        this.f5273a.i.setVisibility(4);
        DragGridView dragGridView = this.f5273a;
        dragGridView.a(dragGridView.o, this.f5273a.f5252c, this.f5273a.f5253d);
        this.f5273a.getParent().requestDisallowInterceptTouchEvent(true);
        this.f5273a.d();
    }
}
