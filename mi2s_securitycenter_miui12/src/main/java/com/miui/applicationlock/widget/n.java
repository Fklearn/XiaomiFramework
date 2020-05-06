package com.miui.applicationlock.widget;

import android.media.MediaPlayer;

class n implements MediaPlayer.OnInfoListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ r f3447a;

    n(r rVar) {
        this.f3447a = rVar;
    }

    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i2) {
        if (this.f3447a.r == null) {
            return true;
        }
        this.f3447a.r.onInfo(mediaPlayer, i, i2);
        return true;
    }
}
