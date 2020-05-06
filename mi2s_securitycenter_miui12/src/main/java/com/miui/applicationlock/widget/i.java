package com.miui.applicationlock.widget;

import android.text.Editable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.ImageView;
import com.miui.securitycenter.R;

class i implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ImageView f3438a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ j f3439b;

    i(j jVar, ImageView imageView) {
        this.f3439b = jVar;
        this.f3438a = imageView;
    }

    public void onClick(View view) {
        boolean unused = this.f3439b.i = true;
        this.f3439b.f3442c.setTransformationMethod(!this.f3439b.f ? HideReturnsTransformationMethod.getInstance() : PasswordTransformationMethod.getInstance());
        Editable text = this.f3439b.f3442c.getText();
        if (text != null) {
            this.f3439b.f3442c.setSelection(text.length());
        }
        this.f3438a.setImageResource(this.f3439b.f ? R.drawable.show_password_img_hide : R.drawable.show_password_img_show);
        j jVar = this.f3439b;
        boolean unused2 = jVar.f = true ^ jVar.f;
        boolean unused3 = this.f3439b.i = false;
    }
}
