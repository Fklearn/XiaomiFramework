package androidx.appcompat.app;

import android.view.View;
import android.widget.AbsListView;

/* renamed from: androidx.appcompat.app.e  reason: case insensitive filesystem */
class C0083e implements AbsListView.OnScrollListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ View f297a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ View f298b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ AlertController f299c;

    C0083e(AlertController alertController, View view, View view2) {
        this.f299c = alertController;
        this.f297a = view;
        this.f298b = view2;
    }

    public void onScroll(AbsListView absListView, int i, int i2, int i3) {
        AlertController.a(absListView, this.f297a, this.f298b);
    }

    public void onScrollStateChanged(AbsListView absListView, int i) {
    }
}
