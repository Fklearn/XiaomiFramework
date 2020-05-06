package com.miui.antivirus.activity;

import android.view.View;
import android.widget.CheckBox;
import com.miui.antivirus.activity.SignExceptionActivity;

class I implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ SignExceptionActivity.a f2664a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ SignExceptionActivity.b f2665b;

    I(SignExceptionActivity.b bVar, SignExceptionActivity.a aVar) {
        this.f2665b = bVar;
        this.f2664a = aVar;
    }

    public void onClick(View view) {
        CheckBox checkBox = this.f2664a.f2702d;
        checkBox.setChecked(!checkBox.isChecked());
    }
}
