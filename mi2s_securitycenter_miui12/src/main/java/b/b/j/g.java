package b.b.j;

import android.view.View;
import android.view.ViewStub;
import android.widget.LinearLayout;
import miui.os.Build;

class g implements ViewStub.OnInflateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ h f1819a;

    g(h hVar) {
        this.f1819a = hVar;
    }

    public void onInflate(ViewStub viewStub, View view) {
        this.f1819a.f1821b.a((LinearLayout) view);
        if (!Build.IS_INTERNATIONAL_BUILD) {
            this.f1819a.f1821b.setActivity(this.f1819a.getActivity());
        }
    }
}
