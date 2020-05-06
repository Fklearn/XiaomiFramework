package com.google.android.exoplayer2.text;

import java.util.List;

public interface TextOutput {
    void onCues(List<Cue> list);
}
