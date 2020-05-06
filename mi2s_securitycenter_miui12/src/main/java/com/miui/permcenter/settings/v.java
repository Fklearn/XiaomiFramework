package com.miui.permcenter.settings;

import android.media.MediaPlayer;

class v implements MediaPlayer.OnInfoListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ w f6577a;

    v(w wVar) {
        this.f6577a = wVar;
    }

    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i2) {
        if (i != 3) {
            return true;
        }
        this.f6577a.f6601a.f6508a.setBackgroundColor(0);
        return true;
    }
}
