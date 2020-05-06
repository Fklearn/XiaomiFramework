package com.miui.gamebooster.ui;

import android.content.DialogInterface;
import android.os.RemoteException;
import android.util.Log;
import com.miui.gamebooster.c.a;
import com.miui.powerkeeper.feedbackcontrol.IFeedbackControl;

/* renamed from: com.miui.gamebooster.ui.ja  reason: case insensitive filesystem */
class C0432ja implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ GameBoosterSettingFragment f5078a;

    C0432ja(GameBoosterSettingFragment gameBoosterSettingFragment) {
        this.f5078a = gameBoosterSettingFragment;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        try {
            IFeedbackControl unused = this.f5078a.K = this.f5078a.c();
            if (this.f5078a.K != null) {
                this.f5078a.K.b(true);
            }
        } catch (RemoteException e) {
            Log.i(GameBoosterSettingFragment.f4889a, e.toString());
        }
        a unused2 = this.f5078a.I;
        a.W(true);
        this.f5078a.p.setChecked(true);
    }
}
