package com.google.android.exoplayer2.text.cea;

import android.support.annotation.NonNull;
import android.text.Layout;
import com.google.android.exoplayer2.text.Cue;

final class Cea708Cue extends Cue implements Comparable<Cea708Cue> {
    public static final int PRIORITY_UNSET = -1;
    public final int priority;

    public Cea708Cue(CharSequence charSequence, Layout.Alignment alignment, float f, int i, int i2, float f2, int i3, float f3, boolean z, int i4, int i5) {
        super(charSequence, alignment, f, i, i2, f2, i3, f3, z, i4);
        this.priority = i5;
    }

    public int compareTo(@NonNull Cea708Cue cea708Cue) {
        int i = cea708Cue.priority;
        int i2 = this.priority;
        if (i < i2) {
            return -1;
        }
        return i > i2 ? 1 : 0;
    }
}
