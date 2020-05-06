package com.miui.applicationlock.widget;

import android.media.MediaPlayer;

class o implements MediaPlayer.OnErrorListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ r f3448a;

    o(r rVar) {
        this.f3448a = rVar;
    }

    public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
        int unused = this.f3448a.f3454d = -1;
        int unused2 = this.f3448a.e = -1;
        if (this.f3448a.m != null) {
            this.f3448a.m.hide();
        }
        if (this.f3448a.q == null) {
            return true;
        }
        this.f3448a.q.onError(this.f3448a.g, i, i2);
        return true;
    }
}
