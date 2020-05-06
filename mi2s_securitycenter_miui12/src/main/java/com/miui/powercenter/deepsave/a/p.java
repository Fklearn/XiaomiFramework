package com.miui.powercenter.deepsave.a;

import android.content.Intent;
import android.view.View;
import com.miui.powercenter.a.a;

class p implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ q f7036a;

    p(q qVar) {
        this.f7036a = qVar;
    }

    public void onClick(View view) {
        if (System.currentTimeMillis() - this.f7036a.f7037d >= 500) {
            long unused = this.f7036a.f7037d = System.currentTimeMillis();
            view.getContext().startActivity(new Intent("android.intent.action.POWER_USAGE_SUMMARY"));
            a.d("expend_top");
        }
    }
}
