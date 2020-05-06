package com.miui.optimizemanage.view;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;

class a implements Player.EventListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ OptimizeMainView f6026a;

    a(OptimizeMainView optimizeMainView) {
        this.f6026a = optimizeMainView;
    }

    public void onLoadingChanged(boolean z) {
    }

    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
    }

    public void onPlayerError(ExoPlaybackException exoPlaybackException) {
    }

    public void onPlayerStateChanged(boolean z, int i) {
    }

    public void onPositionDiscontinuity(int i) {
        if (c.f6028a[this.f6026a.f.ordinal()] == 1) {
            this.f6026a.f6019d.addMediaSource(this.f6026a.f6019d.getSize(), (MediaSource) this.f6026a.f6018c);
        }
    }

    public void onRepeatModeChanged(int i) {
    }

    public void onSeekProcessed() {
    }

    public void onShuffleModeEnabledChanged(boolean z) {
    }

    public void onTimelineChanged(Timeline timeline, Object obj, int i) {
    }

    public void onTracksChanged(TrackGroupArray trackGroupArray, TrackSelectionArray trackSelectionArray) {
    }
}
