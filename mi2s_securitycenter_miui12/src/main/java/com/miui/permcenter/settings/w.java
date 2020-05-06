package com.miui.permcenter.settings;

import android.media.MediaPlayer;

class w implements MediaPlayer.OnPreparedListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ PrivacyProvisionActivity f6601a;

    w(PrivacyProvisionActivity privacyProvisionActivity) {
        this.f6601a = privacyProvisionActivity;
    }

    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.setOnInfoListener(new v(this));
    }
}
