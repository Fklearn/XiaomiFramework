package com.miui.gamebooster.xunyou;

import android.os.Message;
import b.b.c.i.b;
import com.miui.gamebooster.ui.GameBoosterRealMainActivity;
import java.lang.ref.WeakReference;

public class d extends b {

    /* renamed from: a  reason: collision with root package name */
    private final WeakReference<GameBoosterRealMainActivity> f5404a;

    public d(GameBoosterRealMainActivity gameBoosterRealMainActivity) {
        this.f5404a = new WeakReference<>(gameBoosterRealMainActivity);
    }

    public void handleMessage(Message message) {
        GameBoosterRealMainActivity gameBoosterRealMainActivity = (GameBoosterRealMainActivity) this.f5404a.get();
        if (gameBoosterRealMainActivity != null && !gameBoosterRealMainActivity.isFinishing()) {
            super.handleMessage(message);
            m mVar = gameBoosterRealMainActivity.f4887c;
            int i = message.what;
            if (i == 123) {
                gameBoosterRealMainActivity.o();
                if (mVar != null) {
                    mVar.a();
                }
            } else if (i == 124 && mVar != null) {
                mVar.b();
            }
        }
    }
}
