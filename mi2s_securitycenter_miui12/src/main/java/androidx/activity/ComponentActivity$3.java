package androidx.activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.f;
import androidx.lifecycle.g;
import androidx.lifecycle.i;

class ComponentActivity$3 implements g {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ c f198a;

    ComponentActivity$3(c cVar) {
        this.f198a = cVar;
    }

    public void a(@NonNull i iVar, @NonNull f.a aVar) {
        if (aVar == f.a.ON_DESTROY && !this.f198a.isChangingConfigurations()) {
            this.f198a.d().a();
        }
    }
}
