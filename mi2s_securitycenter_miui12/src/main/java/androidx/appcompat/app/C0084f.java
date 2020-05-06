package androidx.appcompat.app;

import android.view.View;

/* renamed from: androidx.appcompat.app.f  reason: case insensitive filesystem */
class C0084f implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ View f300a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ View f301b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ AlertController f302c;

    C0084f(AlertController alertController, View view, View view2) {
        this.f302c = alertController;
        this.f300a = view;
        this.f301b = view2;
    }

    public void run() {
        AlertController.a(this.f302c.g, this.f300a, this.f301b);
    }
}
