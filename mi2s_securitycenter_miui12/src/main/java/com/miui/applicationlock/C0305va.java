package com.miui.applicationlock;

import android.media.MediaPlayer;

/* renamed from: com.miui.applicationlock.va  reason: case insensitive filesystem */
class C0305va implements MediaPlayer.OnPreparedListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ FirstUseAppLockActivity f3387a;

    C0305va(FirstUseAppLockActivity firstUseAppLockActivity) {
        this.f3387a = firstUseAppLockActivity;
    }

    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.setOnInfoListener(new C0303ua(this));
        mediaPlayer.start();
        mediaPlayer.setLooping(true);
    }
}
