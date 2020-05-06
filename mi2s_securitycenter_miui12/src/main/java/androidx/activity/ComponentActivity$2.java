package androidx.activity;

import android.view.View;
import android.view.Window;
import androidx.annotation.NonNull;
import androidx.lifecycle.f;
import androidx.lifecycle.g;
import androidx.lifecycle.i;

class ComponentActivity$2 implements g {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ c f197a;

    ComponentActivity$2(c cVar) {
        this.f197a = cVar;
    }

    public void a(@NonNull i iVar, @NonNull f.a aVar) {
        if (aVar == f.a.ON_STOP) {
            Window window = this.f197a.getWindow();
            View peekDecorView = window != null ? window.peekDecorView() : null;
            if (peekDecorView != null) {
                peekDecorView.cancelPendingInputEvents();
            }
        }
    }
}
