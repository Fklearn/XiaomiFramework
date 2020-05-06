package com.miui.antivirus.ui;

import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Surface;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import java.io.IOException;

class k implements AnalyticsListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MainContentFrame f2971a;

    k(MainContentFrame mainContentFrame) {
        this.f2971a = mainContentFrame;
    }

    public void onAudioSessionId(AnalyticsListener.EventTime eventTime, int i) {
    }

    public void onAudioUnderrun(AnalyticsListener.EventTime eventTime, int i, long j, long j2) {
    }

    public void onBandwidthEstimate(AnalyticsListener.EventTime eventTime, int i, long j, long j2) {
    }

    public void onDecoderDisabled(AnalyticsListener.EventTime eventTime, int i, DecoderCounters decoderCounters) {
    }

    public void onDecoderEnabled(AnalyticsListener.EventTime eventTime, int i, DecoderCounters decoderCounters) {
    }

    public void onDecoderInitialized(AnalyticsListener.EventTime eventTime, int i, String str, long j) {
    }

    public void onDecoderInputFormatChanged(AnalyticsListener.EventTime eventTime, int i, Format format) {
    }

    public void onDownstreamFormatChanged(AnalyticsListener.EventTime eventTime, MediaSourceEventListener.MediaLoadData mediaLoadData) {
    }

    public void onDrmKeysLoaded(AnalyticsListener.EventTime eventTime) {
    }

    public void onDrmKeysRemoved(AnalyticsListener.EventTime eventTime) {
    }

    public void onDrmKeysRestored(AnalyticsListener.EventTime eventTime) {
    }

    public void onDrmSessionManagerError(AnalyticsListener.EventTime eventTime, Exception exc) {
    }

    public void onDroppedVideoFrames(AnalyticsListener.EventTime eventTime, int i, long j) {
    }

    public void onLoadCanceled(AnalyticsListener.EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
    }

    public void onLoadCompleted(AnalyticsListener.EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
    }

    public void onLoadError(AnalyticsListener.EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData, IOException iOException, boolean z) {
    }

    public void onLoadStarted(AnalyticsListener.EventTime eventTime, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
    }

    public void onLoadingChanged(AnalyticsListener.EventTime eventTime, boolean z) {
    }

    public void onMediaPeriodCreated(AnalyticsListener.EventTime eventTime) {
    }

    public void onMediaPeriodReleased(AnalyticsListener.EventTime eventTime) {
    }

    public void onMetadata(AnalyticsListener.EventTime eventTime, Metadata metadata) {
    }

    public void onNetworkTypeChanged(AnalyticsListener.EventTime eventTime, @Nullable NetworkInfo networkInfo) {
    }

    public void onPlaybackParametersChanged(AnalyticsListener.EventTime eventTime, PlaybackParameters playbackParameters) {
    }

    public void onPlayerError(AnalyticsListener.EventTime eventTime, ExoPlaybackException exoPlaybackException) {
    }

    public void onPlayerStateChanged(AnalyticsListener.EventTime eventTime, boolean z, int i) {
    }

    public void onPositionDiscontinuity(AnalyticsListener.EventTime eventTime, int i) {
        if (!this.f2971a.n) {
            this.f2971a.l.addMediaSource(this.f2971a.l.getSize(), (MediaSource) this.f2971a.m);
            Log.d("TAGTAG", "onPositionDiscontinuity add media source: size: " + this.f2971a.l.getSize());
        }
    }

    public void onReadingStarted(AnalyticsListener.EventTime eventTime) {
    }

    public void onRenderedFirstFrame(AnalyticsListener.EventTime eventTime, Surface surface) {
        Log.d("TAGTAG", "第一帧设置为透明");
    }

    public void onRepeatModeChanged(AnalyticsListener.EventTime eventTime, int i) {
    }

    public void onSeekProcessed(AnalyticsListener.EventTime eventTime) {
    }

    public void onSeekStarted(AnalyticsListener.EventTime eventTime) {
    }

    public void onShuffleModeChanged(AnalyticsListener.EventTime eventTime, boolean z) {
    }

    public void onTimelineChanged(AnalyticsListener.EventTime eventTime, int i) {
    }

    public void onTracksChanged(AnalyticsListener.EventTime eventTime, TrackGroupArray trackGroupArray, TrackSelectionArray trackSelectionArray) {
    }

    public void onUpstreamDiscarded(AnalyticsListener.EventTime eventTime, MediaSourceEventListener.MediaLoadData mediaLoadData) {
    }

    public void onVideoSizeChanged(AnalyticsListener.EventTime eventTime, int i, int i2, int i3, float f) {
    }

    public void onViewportSizeChange(AnalyticsListener.EventTime eventTime, int i, int i2) {
    }
}
