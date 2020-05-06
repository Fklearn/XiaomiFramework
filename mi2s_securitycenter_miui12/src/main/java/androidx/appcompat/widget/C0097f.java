package androidx.appcompat.widget;

/* renamed from: androidx.appcompat.widget.f  reason: case insensitive filesystem */
class C0097f implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ActionBarOverlayLayout f603a;

    C0097f(ActionBarOverlayLayout actionBarOverlayLayout) {
        this.f603a = actionBarOverlayLayout;
    }

    public void run() {
        this.f603a.h();
        ActionBarOverlayLayout actionBarOverlayLayout = this.f603a;
        actionBarOverlayLayout.x = actionBarOverlayLayout.e.animate().translationY((float) (-this.f603a.e.getHeight())).setListener(this.f603a.y);
    }
}
