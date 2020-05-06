package com.miui.appmanager.c;

import android.view.View;
import android.widget.PopupWindow;
import com.miui.appmanager.AppManagerMainActivity;

class e implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ PopupWindow f3631a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ AppManagerMainActivity f3632b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ i f3633c;

    e(i iVar, PopupWindow popupWindow, AppManagerMainActivity appManagerMainActivity) {
        this.f3633c = iVar;
        this.f3631a = popupWindow;
        this.f3632b = appManagerMainActivity;
    }

    public void onClick(View view) {
        this.f3631a.dismiss();
        this.f3633c.a(this.f3632b);
    }
}
