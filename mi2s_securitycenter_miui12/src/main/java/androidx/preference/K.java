package androidx.preference;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.SeekBar;

class K implements View.OnKeyListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ SeekBarPreference f1016a;

    K(SeekBarPreference seekBarPreference) {
        this.f1016a = seekBarPreference;
    }

    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (keyEvent.getAction() != 0) {
            return false;
        }
        if ((!this.f1016a.h && (i == 21 || i == 22)) || i == 23 || i == 66) {
            return false;
        }
        SeekBar seekBar = this.f1016a.f;
        if (seekBar != null) {
            return seekBar.onKeyDown(i, keyEvent);
        }
        Log.e("SeekBarPreference", "SeekBar view is null and hence cannot be adjusted.");
        return false;
    }
}
