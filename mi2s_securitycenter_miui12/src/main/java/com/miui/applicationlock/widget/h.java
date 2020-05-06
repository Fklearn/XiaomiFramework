package com.miui.applicationlock.widget;

import android.text.Editable;
import android.text.TextUtils;
import miui.view.MiuiKeyBoardView;

class h implements MiuiKeyBoardView.OnKeyboardActionListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ j f3437a;

    h(j jVar) {
        this.f3437a = jVar;
    }

    public void onKeyBoardDelete() {
        Editable text = this.f3437a.f3442c.getText();
        if (!TextUtils.isEmpty(text.toString())) {
            text.delete(text.length() - 1, text.length());
        }
    }

    public void onKeyBoardOK() {
        this.f3437a.i();
    }

    public void onText(CharSequence charSequence) {
        this.f3437a.f3442c.append(charSequence);
    }
}
