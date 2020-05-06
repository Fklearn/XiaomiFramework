package androidx.preference;

import android.widget.SeekBar;

class J implements SeekBar.OnSeekBarChangeListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ SeekBarPreference f1015a;

    J(SeekBarPreference seekBarPreference) {
        this.f1015a = seekBarPreference;
    }

    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        if (z) {
            SeekBarPreference seekBarPreference = this.f1015a;
            if (seekBarPreference.j || !seekBarPreference.e) {
                this.f1015a.a(seekBar);
                return;
            }
        }
        SeekBarPreference seekBarPreference2 = this.f1015a;
        seekBarPreference2.d(i + seekBarPreference2.f1024b);
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        this.f1015a.e = true;
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        this.f1015a.e = false;
        int progress = seekBar.getProgress();
        SeekBarPreference seekBarPreference = this.f1015a;
        if (progress + seekBarPreference.f1024b != seekBarPreference.f1023a) {
            seekBarPreference.a(seekBar);
        }
    }
}
