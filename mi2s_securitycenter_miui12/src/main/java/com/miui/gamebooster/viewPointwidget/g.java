package com.miui.gamebooster.viewPointwidget;

import android.media.AudioManager;

class g implements AudioManager.OnAudioFocusChangeListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ViewPointListVideoItem f5351a;

    g(ViewPointListVideoItem viewPointListVideoItem) {
        this.f5351a = viewPointListVideoItem;
    }

    public void onAudioFocusChange(int i) {
        if (this.f5351a.F != null && this.f5351a.F.get() != null) {
            ((AudioManager.OnAudioFocusChangeListener) this.f5351a.F.get()).onAudioFocusChange(i);
        }
    }
}
