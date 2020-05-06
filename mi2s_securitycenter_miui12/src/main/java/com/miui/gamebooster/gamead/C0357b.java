package com.miui.gamebooster.gamead;

import android.util.Log;
import com.miui.applicationlock.c.y;
import com.miui.gamebooster.ui.GameBoosterRealMainActivity;
import com.xiaomi.ad.feedback.IAdFeedbackListener;

/* renamed from: com.miui.gamebooster.gamead.b  reason: case insensitive filesystem */
class C0357b extends IAdFeedbackListener.Stub {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ GameBoosterRealMainActivity f4294a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ d f4295b;

    C0357b(d dVar, GameBoosterRealMainActivity gameBoosterRealMainActivity) {
        this.f4295b = dVar;
        this.f4294a = gameBoosterRealMainActivity;
    }

    public void onFinished(int i) {
        Log.i("GameAdAdapter", "closeAD");
        y.b().b(this.f4294a.getApplicationContext());
        if (i != -1) {
            this.f4295b.d(this.f4294a);
        }
    }
}
