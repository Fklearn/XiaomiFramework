package com.miui.permcenter.permissions;

import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ListView;
import com.miui.powercenter.view.ScrollListView;

class H implements ViewTreeObserver.OnGlobalLayoutListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ScrollListView f6224a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ SystemAppPermissionDialogActivity f6225b;

    H(SystemAppPermissionDialogActivity systemAppPermissionDialogActivity, ScrollListView scrollListView) {
        this.f6225b = systemAppPermissionDialogActivity;
        this.f6224a = scrollListView;
    }

    public void onGlobalLayout() {
        this.f6224a.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        ViewGroup.LayoutParams layoutParams = this.f6224a.getLayoutParams();
        layoutParams.height = this.f6225b.a((ListView) this.f6224a);
        this.f6224a.setLayoutParams(layoutParams);
    }
}
