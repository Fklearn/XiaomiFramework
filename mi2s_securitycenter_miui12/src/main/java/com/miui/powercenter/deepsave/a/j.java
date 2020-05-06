package com.miui.powercenter.deepsave.a;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import com.miui.powercenter.a.a;

class j implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ k f7021a;

    j(k kVar) {
        this.f7021a = kVar;
    }

    public void onClick(View view) {
        try {
            view.getContext().startActivity(new Intent("miui.intent.action.EXTREME_POWER_ENTRY_ACTIVITY"));
            a.e();
            a.d("extreme_save_mode");
        } catch (Exception e) {
            Log.e("ExtremePowerSave", "extremePowerSaveMode updateUI failed", e);
        }
    }
}
