package com.miui.applicationlock.widget;

import android.media.MediaPlayer;

class k implements MediaPlayer.OnVideoSizeChangedListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ r f3444a;

    k(r rVar) {
        this.f3444a = rVar;
    }

    public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i2) {
        int unused = this.f3444a.i = mediaPlayer.getVideoWidth();
        int unused2 = this.f3444a.j = mediaPlayer.getVideoHeight();
        if (this.f3444a.i != 0 && this.f3444a.j != 0) {
            this.f3444a.getHolder().setFixedSize(this.f3444a.i, this.f3444a.j);
            this.f3444a.requestLayout();
        }
    }
}
