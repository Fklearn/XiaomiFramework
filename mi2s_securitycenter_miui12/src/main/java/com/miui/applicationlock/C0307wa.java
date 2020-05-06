package com.miui.applicationlock;

import android.media.MediaPlayer;

/* renamed from: com.miui.applicationlock.wa  reason: case insensitive filesystem */
class C0307wa implements MediaPlayer.OnInfoListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C0311xa f3389a;

    C0307wa(C0311xa xaVar) {
        this.f3389a = xaVar;
    }

    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i2) {
        if (i != 3) {
            return true;
        }
        this.f3389a.f3466a.f3172c.setBackgroundColor(0);
        return true;
    }
}
