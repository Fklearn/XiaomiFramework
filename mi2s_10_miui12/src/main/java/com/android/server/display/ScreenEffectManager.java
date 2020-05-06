package com.android.server.display;

import android.os.Looper;

public abstract class ScreenEffectManager {
    public abstract long getDimDurationExtraTime(long j);

    public abstract float getGrayScale();

    public abstract int getNightLightBrightness();

    public abstract void initDisplayPowerController(DisplayPowerController displayPowerController, Looper looper);

    public abstract void setNightLight(int i);

    public abstract void updateDozeBrightness(int i);

    public abstract void updateLocalScreenEffect(String str);

    public abstract void updateScreenEffect(int i);
}
