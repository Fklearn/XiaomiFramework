package com.miui.gamebooster.ui;

import android.content.DialogInterface;
import android.util.Log;
import com.miui.gamebooster.c.a;
import com.miui.powerkeeper.feedbackcontrol.IFeedbackControl;

class Aa implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Ba f4844a;

    Aa(Ba ba) {
        this.f4844a = ba;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        try {
            IFeedbackControl unused = this.f4844a.i = ((SettingsActivity) this.f4844a.mActivity).l();
            if (this.f4844a.i != null) {
                this.f4844a.i.b(true);
            }
        } catch (Exception e) {
            Log.i("PerformanceSettingsFrag", e.toString());
        }
        a.W(true);
    }
}
