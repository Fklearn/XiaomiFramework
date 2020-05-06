package com.miui.gamebooster.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.miui.gamebooster.c.a;
import com.miui.gamebooster.widget.CheckBoxSettingItemView;
import com.miui.powerkeeper.feedbackcontrol.IFeedbackControl;

/* renamed from: com.miui.gamebooster.ui.xa  reason: case insensitive filesystem */
class C0459xa extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Ba f5127a;

    C0459xa(Ba ba) {
        this.f5127a = ba;
    }

    public void onReceive(Context context, Intent intent) {
        int intExtra;
        boolean q;
        CheckBoxSettingItemView d2;
        if ("gb_thermal_supported_action".equals(intent.getAction()) && (intExtra = intent.getIntExtra("gb_thermal_supported", 0)) > 0 && this.f5127a.h == 0) {
            int unused = this.f5127a.h = intExtra;
            this.f5127a.f4855b.setVisibility(0);
            try {
                SettingsActivity settingsActivity = (SettingsActivity) this.f5127a.getActivity();
                if (settingsActivity != null && !settingsActivity.isFinishing() && !settingsActivity.isDestroyed()) {
                    IFeedbackControl unused2 = this.f5127a.i = settingsActivity.l();
                    if (this.f5127a.i != null) {
                        if (intExtra == 1) {
                            q = a.q(false);
                            d2 = this.f5127a.f4855b;
                        } else if (intExtra == 2) {
                            q = this.f5127a.i.p();
                            d2 = this.f5127a.f4855b;
                        } else {
                            return;
                        }
                        d2.a(q, false, false);
                    }
                }
            } catch (Exception e) {
                Log.i("PerformanceSettingsFrag", e.toString());
            }
        }
    }
}
