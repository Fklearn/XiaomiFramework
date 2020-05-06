package com.miui.applicationlock;

import android.media.MediaPlayer;

/* renamed from: com.miui.applicationlock.ua  reason: case insensitive filesystem */
class C0303ua implements MediaPlayer.OnInfoListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C0305va f3385a;

    C0303ua(C0305va vaVar) {
        this.f3385a = vaVar;
    }

    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i2) {
        if (i != 3) {
            return true;
        }
        this.f3385a.f3387a.f3171b.setBackgroundColor(0);
        return true;
    }
}
