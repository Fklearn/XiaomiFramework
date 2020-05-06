package com.miui.powercenter.deepsave.a;

import android.content.Intent;
import android.view.View;
import com.miui.powercenter.a.a;
import miui.util.Log;

class l implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ m f7026a;

    l(m mVar) {
        this.f7026a = mVar;
    }

    public void onClick(View view) {
        try {
            view.getContext().startActivity(new Intent("miui.intent.action.POWER_HIDE_MODE_APP_LIST"));
            a.d("app_smart_save");
        } catch (Exception e) {
            Log.d("hideModeStateModel", "can not find hide mode action", e);
        }
    }
}
