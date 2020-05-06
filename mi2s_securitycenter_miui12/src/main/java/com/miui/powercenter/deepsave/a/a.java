package com.miui.powercenter.deepsave.a;

import android.content.Intent;
import android.view.View;
import com.miui.powercenter.autotask.AutoTaskManageActivity;

class a implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ b f7001a;

    a(b bVar) {
        this.f7001a = bVar;
    }

    public void onClick(View view) {
        view.getContext().startActivity(new Intent(view.getContext(), AutoTaskManageActivity.class));
        com.miui.powercenter.a.a.d("auto_task");
    }
}
