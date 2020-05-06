package androidx.appcompat.widget;

import android.view.View;
import android.view.Window;
import androidx.appcompat.view.menu.C0086a;

class Aa implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final C0086a f421a = new C0086a(this.f422b.f466a.getContext(), 0, 16908332, 0, 0, this.f422b.i);

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Ca f422b;

    Aa(Ca ca) {
        this.f422b = ca;
    }

    public void onClick(View view) {
        Ca ca = this.f422b;
        Window.Callback callback = ca.l;
        if (callback != null && ca.m) {
            callback.onMenuItemSelected(0, this.f421a);
        }
    }
}
