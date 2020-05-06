package com.miui.antivirus.result;

import android.view.View;
import android.widget.PopupWindow;
import com.miui.antivirus.activity.MainActivity;

/* renamed from: com.miui.antivirus.result.c  reason: case insensitive filesystem */
class C0240c implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ PopupWindow f2826a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ MainActivity f2827b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ C0243f f2828c;

    C0240c(C0243f fVar, PopupWindow popupWindow, MainActivity mainActivity) {
        this.f2828c = fVar;
        this.f2826a = popupWindow;
        this.f2827b = mainActivity;
    }

    public void onClick(View view) {
        this.f2826a.dismiss();
        this.f2827b.a((C0238a) this.f2828c);
    }
}
