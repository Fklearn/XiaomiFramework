package com.google.android.exoplayer2.offline;

public final class TrackKey {
    public final int groupIndex;
    public final int periodIndex;
    public final int trackIndex;

    public TrackKey(int i, int i2, int i3) {
        this.periodIndex = i;
        this.groupIndex = i2;
        this.trackIndex = i3;
    }
}
