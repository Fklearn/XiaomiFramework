package androidx.appcompat.widget;

import android.view.ViewTreeObserver;
import androidx.appcompat.widget.AppCompatSpinner;

class C implements ViewTreeObserver.OnGlobalLayoutListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AppCompatSpinner.c f465a;

    C(AppCompatSpinner.c cVar) {
        this.f465a = cVar;
    }

    public void onGlobalLayout() {
        AppCompatSpinner.c cVar = this.f465a;
        if (!cVar.b(AppCompatSpinner.this)) {
            this.f465a.dismiss();
            return;
        }
        this.f465a.h();
        C.super.b();
    }
}
