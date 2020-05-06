package androidx.appcompat.app;

import android.view.View;
import androidx.core.widget.NestedScrollView;

/* renamed from: androidx.appcompat.app.c  reason: case insensitive filesystem */
class C0081c implements NestedScrollView.b {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ View f291a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ View f292b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ AlertController f293c;

    C0081c(AlertController alertController, View view, View view2) {
        this.f293c = alertController;
        this.f291a = view;
        this.f292b = view2;
    }

    public void a(NestedScrollView nestedScrollView, int i, int i2, int i3, int i4) {
        AlertController.a(nestedScrollView, this.f291a, this.f292b);
    }
}
