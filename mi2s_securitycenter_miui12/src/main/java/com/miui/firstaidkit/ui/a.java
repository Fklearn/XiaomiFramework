package com.miui.firstaidkit.ui;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;

class a implements Player.EventListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ FirstAidVideoView f3990a;

    a(FirstAidVideoView firstAidVideoView) {
        this.f3990a = firstAidVideoView;
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
        if (b.f3991a[this.f3990a.e.ordinal()] == 1) {
            this.f3990a.f3982d.addMediaSource(this.f3990a.f3982d.getSize(), (MediaSource) this.f3990a.f3981c);
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
