package com.miui.securityscan;

import android.view.View;
import android.view.ViewStub;
import com.miui.common.customview.AutoPasteListView;
import com.miui.securitycenter.R;
import miui.os.Build;

/* renamed from: com.miui.securityscan.f  reason: case insensitive filesystem */
class C0546f implements ViewStub.OnInflateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ L f7694a;

    C0546f(L l) {
        this.f7694a = l;
    }

    public void onInflate(ViewStub viewStub, View view) {
        View unused = this.f7694a.A = view;
        AutoPasteListView unused2 = this.f7694a.z = (AutoPasteListView) view.findViewById(R.id.sec_result);
        if (Build.IS_INTERNATIONAL_BUILD) {
            this.f7694a.z.setOverScrollMode(2);
        } else {
            this.f7694a.z.setOverScrollMode(0);
        }
        this.f7694a.z.setTopDraggable(true);
        this.f7694a.z.setAlignItem(0);
        this.f7694a.z.setOnScrollPercentChangeListener(new C0545e(this));
    }
}
