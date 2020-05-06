package androidx.appcompat.widget;

/* renamed from: androidx.appcompat.widget.e  reason: case insensitive filesystem */
class C0095e implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ActionBarOverlayLayout f601a;

    C0095e(ActionBarOverlayLayout actionBarOverlayLayout) {
        this.f601a = actionBarOverlayLayout;
    }

    public void run() {
        this.f601a.h();
        ActionBarOverlayLayout actionBarOverlayLayout = this.f601a;
        actionBarOverlayLayout.x = actionBarOverlayLayout.e.animate().translationY(0.0f).setListener(this.f601a.y);
    }
}
