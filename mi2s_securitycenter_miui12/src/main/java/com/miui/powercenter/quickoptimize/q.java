package com.miui.powercenter.quickoptimize;

import android.view.View;
import android.widget.CheckBox;
import com.miui.powercenter.quickoptimize.r;

class q implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ r f7242a;

    q(r rVar) {
        this.f7242a = rVar;
    }

    public void onClick(View view) {
        if (this.f7242a.f && (view.getTag() instanceof r.c)) {
            CheckBox checkBox = ((r.c) view.getTag()).f7254c;
            checkBox.setChecked(!checkBox.isChecked());
        }
    }
}
