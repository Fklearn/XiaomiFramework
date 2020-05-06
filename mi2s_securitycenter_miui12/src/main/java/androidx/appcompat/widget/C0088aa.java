package androidx.appcompat.widget;

import android.view.View;

/* renamed from: androidx.appcompat.widget.aa  reason: case insensitive filesystem */
class C0088aa implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ View f583a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ C0090ba f584b;

    C0088aa(C0090ba baVar, View view) {
        this.f584b = baVar;
        this.f583a = view;
    }

    public void run() {
        this.f584b.smoothScrollTo(this.f583a.getLeft() - ((this.f584b.getWidth() - this.f583a.getWidth()) / 2), 0);
        this.f584b.f587b = null;
    }
}
