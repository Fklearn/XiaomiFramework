package com.miui.applicationlock.widget;

import android.view.View;
import com.miui.applicationlock.widget.MiuiNumericInputView;

class f implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MiuiNumericInputView f3434a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ MiuiNumericInputView.a f3435b;

    f(MiuiNumericInputView.a aVar, MiuiNumericInputView miuiNumericInputView) {
        this.f3435b = aVar;
        this.f3434a = miuiNumericInputView;
    }

    public void onClick(View view) {
        if (MiuiNumericInputView.this.e != null && this.f3435b.f3413a != -1) {
            MiuiNumericInputView.this.e.a(this.f3435b.f3413a);
        }
    }
}
