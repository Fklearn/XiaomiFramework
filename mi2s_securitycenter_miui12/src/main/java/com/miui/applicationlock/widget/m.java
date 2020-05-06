package com.miui.applicationlock.widget;

import android.media.MediaPlayer;

class m implements MediaPlayer.OnCompletionListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ r f3446a;

    m(r rVar) {
        this.f3446a = rVar;
    }

    public void onCompletion(MediaPlayer mediaPlayer) {
        int unused = this.f3446a.f3454d = 5;
        int unused2 = this.f3446a.e = 5;
        if (this.f3446a.m != null) {
            this.f3446a.m.hide();
        }
        if (this.f3446a.n != null) {
            this.f3446a.n.onCompletion(this.f3446a.g);
        }
    }
}
