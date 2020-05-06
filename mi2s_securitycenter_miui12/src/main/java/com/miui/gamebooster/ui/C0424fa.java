package com.miui.gamebooster.ui;

import android.util.Log;
import androidx.preference.CheckBoxPreference;
import com.miui.gamebooster.c.a;
import com.miui.gamebooster.globalgame.util.Utils;
import com.miui.powerkeeper.feedbackcontrol.IFeedbackControl;

/* renamed from: com.miui.gamebooster.ui.fa  reason: case insensitive filesystem */
class C0424fa implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C0426ga f5063a;

    C0424fa(C0426ga gaVar) {
        this.f5063a = gaVar;
    }

    public void run() {
        CheckBoxPreference C;
        boolean G;
        try {
            if (!Utils.a(this.f5063a.f5066a.O)) {
                IFeedbackControl unused = this.f5063a.f5066a.K = this.f5063a.f5066a.c();
                if (this.f5063a.f5066a.K != null) {
                    int b2 = this.f5063a.f5066a.f4892d;
                    if (b2 == 1) {
                        GameBoosterSettingFragment gameBoosterSettingFragment = this.f5063a.f5066a;
                        a unused2 = this.f5063a.f5066a.I;
                        boolean unused3 = gameBoosterSettingFragment.G = a.q(false);
                        C = this.f5063a.f5066a.p;
                        G = this.f5063a.f5066a.G;
                    } else if (b2 == 2) {
                        boolean unused4 = this.f5063a.f5066a.G = this.f5063a.f5066a.K.p();
                        C = this.f5063a.f5066a.p;
                        G = this.f5063a.f5066a.G;
                    } else {
                        return;
                    }
                    C.setChecked(G);
                }
            }
        } catch (Exception e) {
            Log.i(GameBoosterSettingFragment.f4889a, e.toString());
        }
    }
}
