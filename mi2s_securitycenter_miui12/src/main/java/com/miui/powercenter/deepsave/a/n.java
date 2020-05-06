package com.miui.powercenter.deepsave.a;

import android.content.Intent;
import android.view.View;
import com.miui.powercenter.a.a;
import com.miui.powercenter.savemode.PowerSaveActivity;

class n implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ o f7031a;

    n(o oVar) {
        this.f7031a = oVar;
    }

    public void onClick(View view) {
        view.getContext().startActivity(new Intent(view.getContext(), PowerSaveActivity.class));
        a.d("save_mode");
    }
}
