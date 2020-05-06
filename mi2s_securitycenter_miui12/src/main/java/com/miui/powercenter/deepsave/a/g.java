package com.miui.powercenter.deepsave.a;

import android.content.Intent;
import android.view.View;
import com.miui.powercenter.a.a;
import com.miui.powercenter.bootshutdown.PowerShutdownOnTime;

class g implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ h f7016a;

    g(h hVar) {
        this.f7016a = hVar;
    }

    public void onClick(View view) {
        view.getContext().startActivity(new Intent(view.getContext(), PowerShutdownOnTime.class));
        a.d("power_on_off_plan");
    }
}
