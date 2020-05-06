package com.miui.appmanager.widget;

import miui.widget.DropDownPopupWindow;

class b extends DropDownPopupWindow.DefaultContainerController {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ d f3720a;

    b(d dVar) {
        this.f3720a = dVar;
    }

    public void onDismiss() {
        this.f3720a.c();
    }

    public void onShow() {
        if (this.f3720a.f3725d != null) {
            this.f3720a.f3725d.onShow();
        }
    }
}
