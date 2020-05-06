package androidx.appcompat.app;

import android.view.View;

/* renamed from: androidx.appcompat.app.d  reason: case insensitive filesystem */
class C0082d implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ View f294a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ View f295b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ AlertController f296c;

    C0082d(AlertController alertController, View view, View view2) {
        this.f296c = alertController;
        this.f294a = view;
        this.f295b = view2;
    }

    public void run() {
        AlertController.a(this.f296c.A, this.f294a, this.f295b);
    }
}
