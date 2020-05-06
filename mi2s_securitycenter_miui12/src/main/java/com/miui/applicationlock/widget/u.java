package com.miui.applicationlock.widget;

import android.view.KeyEvent;
import android.widget.TextView;

class u implements TextView.OnEditorActionListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ x f3456a;

    u(x xVar) {
        this.f3456a = xVar;
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 0 && i != 6 && i != 5) {
            return false;
        }
        this.f3456a.j();
        return true;
    }
}
