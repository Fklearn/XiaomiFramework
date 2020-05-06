package com.google.android.exoplayer2.ui;

import com.google.android.exoplayer2.Format;

public interface TrackNameProvider {
    String getTrackName(Format format);
}
