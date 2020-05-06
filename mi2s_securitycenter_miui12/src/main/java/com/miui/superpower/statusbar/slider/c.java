package com.miui.superpower.statusbar.slider;

import android.provider.Settings;
import android.widget.SeekBar;

class c implements SeekBar.OnSeekBarChangeListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ToggleSliderView f8229a;

    c(ToggleSliderView toggleSliderView) {
        this.f8229a = toggleSliderView;
    }

    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        if (z) {
            Settings.System.putInt(this.f8229a.getContext().getContentResolver(), "screen_brightness", seekBar.getProgress() + 1);
        }
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        if (this.f8229a.f8220b == 1) {
            boolean unused = this.f8229a.f8221c = true;
        }
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        if (this.f8229a.f8221c) {
            boolean unused = this.f8229a.f8221c = false;
            return;
        }
        Settings.System.putInt(this.f8229a.getContext().getContentResolver(), "screen_brightness", seekBar.getProgress() + 1);
    }
}
