package com.google.android.exoplayer2;

public final class IllegalSeekPositionException extends IllegalStateException {
    public final long positionMs;
    public final Timeline timeline;
    public final int windowIndex;

    public IllegalSeekPositionException(Timeline timeline2, int i, long j) {
        this.timeline = timeline2;
        this.windowIndex = i;
        this.positionMs = j;
    }
}
