package com.miui.gamebooster.ui;

import android.media.AudioManager;

/* renamed from: com.miui.gamebooster.ui.ta  reason: case insensitive filesystem */
class C0451ta implements AudioManager.OnAudioFocusChangeListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ GameVideoPlayActivity f5109a;

    C0451ta(GameVideoPlayActivity gameVideoPlayActivity) {
        this.f5109a = gameVideoPlayActivity;
    }

    public void onAudioFocusChange(int i) {
        if (this.f5109a.p != null && this.f5109a.p.get() != null) {
            ((AudioManager.OnAudioFocusChangeListener) this.f5109a.p.get()).onAudioFocusChange(i);
        }
    }
}
