package com.miui.securityscan;

import android.view.View;
import android.view.ViewStub;
import android.widget.LinearLayout;
import miui.os.Build;

class A implements ViewStub.OnInflateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ L f7543a;

    A(L l) {
        this.f7543a = l;
    }

    public void onInflate(ViewStub viewStub, View view) {
        LinearLayout unused = this.f7543a.pa = (LinearLayout) view;
        this.f7543a.oa.a(this.f7543a.pa);
        if (!Build.IS_INTERNATIONAL_BUILD) {
            this.f7543a.oa.setActivity(this.f7543a.getActivity());
        }
    }
}
