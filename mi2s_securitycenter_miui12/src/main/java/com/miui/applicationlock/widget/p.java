package com.miui.applicationlock.widget;

import android.media.MediaPlayer;

class p implements MediaPlayer.OnBufferingUpdateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ r f3449a;

    p(r rVar) {
        this.f3449a = rVar;
    }

    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
        int unused = this.f3449a.p = i;
    }
}
