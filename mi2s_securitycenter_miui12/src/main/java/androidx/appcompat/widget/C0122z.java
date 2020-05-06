package androidx.appcompat.widget;

import android.annotation.SuppressLint;
import android.view.View;
import androidx.appcompat.view.menu.v;
import androidx.appcompat.widget.AppCompatSpinner;

/* renamed from: androidx.appcompat.widget.z  reason: case insensitive filesystem */
class C0122z extends Q {
    final /* synthetic */ AppCompatSpinner.c j;
    final /* synthetic */ AppCompatSpinner k;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    C0122z(AppCompatSpinner appCompatSpinner, View view, AppCompatSpinner.c cVar) {
        super(view);
        this.k = appCompatSpinner;
        this.j = cVar;
    }

    public v a() {
        return this.j;
    }

    @SuppressLint({"SyntheticAccessor"})
    public boolean b() {
        if (this.k.getInternalPopup().isShowing()) {
            return true;
        }
        this.k.a();
        return true;
    }
}
