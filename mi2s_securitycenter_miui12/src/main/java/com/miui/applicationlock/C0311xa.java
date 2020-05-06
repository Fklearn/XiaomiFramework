package com.miui.applicationlock;

import android.media.MediaPlayer;

/* renamed from: com.miui.applicationlock.xa  reason: case insensitive filesystem */
class C0311xa implements MediaPlayer.OnPreparedListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ FirstUseAppLockActivity f3466a;

    C0311xa(FirstUseAppLockActivity firstUseAppLockActivity) {
        this.f3466a = firstUseAppLockActivity;
    }

    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.setOnInfoListener(new C0307wa(this));
        mediaPlayer.start();
        mediaPlayer.setLooping(true);
    }
}
