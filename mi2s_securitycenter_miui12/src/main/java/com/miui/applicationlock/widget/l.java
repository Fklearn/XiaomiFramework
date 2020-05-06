package com.miui.applicationlock.widget;

import android.media.MediaPlayer;

class l implements MediaPlayer.OnPreparedListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ r f3445a;

    l(r rVar) {
        this.f3445a = rVar;
    }

    public void onPrepared(MediaPlayer mediaPlayer) {
        int unused = this.f3445a.f3454d = 2;
        r rVar = this.f3445a;
        boolean unused2 = rVar.v = true;
        boolean unused3 = rVar.u = true;
        boolean unused4 = rVar.t = true;
        if (this.f3445a.o != null) {
            this.f3445a.o.onPrepared(this.f3445a.g);
        }
        if (this.f3445a.m != null) {
            this.f3445a.m.setEnabled(true);
        }
        int unused5 = this.f3445a.i = mediaPlayer.getVideoWidth();
        int unused6 = this.f3445a.j = mediaPlayer.getVideoHeight();
        int m = this.f3445a.s;
        if (m != 0) {
            this.f3445a.seekTo(m);
        }
        if (this.f3445a.i != 0 && this.f3445a.j != 0) {
            this.f3445a.getHolder().setFixedSize(this.f3445a.i, this.f3445a.j);
            if (this.f3445a.k != this.f3445a.i || this.f3445a.l != this.f3445a.j) {
                return;
            }
            if (this.f3445a.e == 3) {
                this.f3445a.start();
                if (this.f3445a.m != null) {
                    this.f3445a.m.show();
                }
            } else if (this.f3445a.isPlaying()) {
            } else {
                if ((m != 0 || this.f3445a.getCurrentPosition() > 0) && this.f3445a.m != null) {
                    this.f3445a.m.show(0);
                }
            }
        } else if (this.f3445a.e == 3) {
            this.f3445a.start();
        }
    }
}
