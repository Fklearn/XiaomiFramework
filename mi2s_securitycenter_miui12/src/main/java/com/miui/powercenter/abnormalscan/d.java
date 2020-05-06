package com.miui.powercenter.abnormalscan;

import android.view.View;
import android.widget.CheckBox;
import com.miui.powercenter.abnormalscan.e;

class d implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ e f6653a;

    d(e eVar) {
        this.f6653a = eVar;
    }

    public void onClick(View view) {
        if (this.f6653a.g && (view.getTag() instanceof e.c)) {
            CheckBox checkBox = ((e.c) view.getTag()).f6663d;
            checkBox.setChecked(!checkBox.isChecked());
        }
    }
}
