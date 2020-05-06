package com.google.android.exoplayer2;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;
import com.google.android.exoplayer2.DefaultMediaClock;
import com.google.android.exoplayer2.PlayerMessage;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.SampleStream;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectorResult;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Clock;
import com.google.android.exoplayer2.util.HandlerWrapper;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

final class ExoPlayerImplInternal implements Handler.Callback, MediaPeriod.Callback, TrackSelector.InvalidationListener, MediaSource.SourceInfoRefreshListener, DefaultMediaClock.PlaybackParameterListener, PlayerMessage.Sender {
    private static final int IDLE_INTERVAL_MS = 1000;
    private static final int MSG_DO_SOME_WORK = 2;
    public static final int MSG_ERROR = 2;
    private static final int MSG_PERIOD_PREPARED = 9;
    public static final int MSG_PLAYBACK_INFO_CHANGED = 0;
    public static final int MSG_PLAYBACK_PARAMETERS_CHANGED = 1;
    private static final int MSG_PREPARE = 0;
    private static final int MSG_REFRESH_SOURCE_INFO = 8;
    private static final int MSG_RELEASE = 7;
    private static final int MSG_SEEK_TO = 3;
    private static final int MSG_SEND_MESSAGE = 14;
    private static final int MSG_SEND_MESSAGE_TO_TARGET_THREAD = 15;
    private static final int MSG_SET_PLAYBACK_PARAMETERS = 4;
    private static final int MSG_SET_PLAY_WHEN_READY = 1;
    private static final int MSG_SET_REPEAT_MODE = 12;
    private static final int MSG_SET_SEEK_PARAMETERS = 5;
    private static final int MSG_SET_SHUFFLE_ENABLED = 13;
    private static final int MSG_SOURCE_CONTINUE_LOADING_REQUESTED = 10;
    private static final int MSG_STOP = 6;
    private static final int MSG_TRACK_SELECTION_INVALIDATED = 11;
    private static final int PREPARING_SOURCE_INTERVAL_MS = 10;
    private static final int RENDERING_INTERVAL_MS = 10;
    private static final String TAG = "ExoPlayerImplInternal";
    private final long backBufferDurationUs;
    private final Clock clock;
    private final TrackSelectorResult emptyTrackSelectorResult;
    private Renderer[] enabledRenderers;
    private final Handler eventHandler;
    private final HandlerWrapper handler;
    private final HandlerThread internalPlaybackThread;
    private final LoadControl loadControl;
    private final DefaultMediaClock mediaClock;
    private MediaSource mediaSource;
    private int nextPendingMessageIndex;
    private SeekPosition pendingInitialSeekPosition;
    private final ArrayList<PendingMessageInfo> pendingMessages;
    private int pendingPrepareCount;
    private final Timeline.Period period;
    private boolean playWhenReady;
    private PlaybackInfo playbackInfo;
    private final PlaybackInfoUpdate playbackInfoUpdate;
    private final ExoPlayer player;
    private final MediaPeriodQueue queue = new MediaPeriodQueue();
    private boolean rebuffering;
    private boolean released;
    private final RendererCapabilities[] rendererCapabilities;
    private long rendererPositionUs;
    private final Renderer[] renderers;
    private int repeatMode;
    private final boolean retainBackBufferFromKeyframe;
    private SeekParameters seekParameters;
    private boolean shuffleModeEnabled;
    private final TrackSelector trackSelector;
    private final Timeline.Window window;

    private static final class MediaSourceRefreshInfo {
        public final Object manifest;
        public final MediaSource source;
        public final Timeline timeline;

        public MediaSourceRefreshInfo(MediaSource mediaSource, Timeline timeline2, Object obj) {
            this.source = mediaSource;
            this.timeline = timeline2;
            this.manifest = obj;
        }
    }

    private static final class PendingMessageInfo implements Comparable<PendingMessageInfo> {
        public final PlayerMessage message;
        public int resolvedPeriodIndex;
        public long resolvedPeriodTimeUs;
        @Nullable
        public Object resolvedPeriodUid;

        public PendingMessageInfo(PlayerMessage playerMessage) {
            this.message = playerMessage;
        }

        public int compareTo(@NonNull PendingMessageInfo pendingMessageInfo) {
            if ((this.resolvedPeriodUid == null) != (pendingMessageInfo.resolvedPeriodUid == null)) {
                return this.resolvedPeriodUid != null ? -1 : 1;
            }
            if (this.resolvedPeriodUid == null) {
                return 0;
            }
            int i = this.resolvedPeriodIndex - pendingMessageInfo.resolvedPeriodIndex;
            return i != 0 ? i : Util.compareLong(this.resolvedPeriodTimeUs, pendingMessageInfo.resolvedPeriodTimeUs);
        }

        public void setResolvedPosition(int i, long j, Object obj) {
            this.resolvedPeriodIndex = i;
            this.resolvedPeriodTimeUs = j;
            this.resolvedPeriodUid = obj;
        }
    }

    private static final class PlaybackInfoUpdate {
        /* access modifiers changed from: private */
        public int discontinuityReason;
        private PlaybackInfo lastPlaybackInfo;
        /* access modifiers changed from: private */
        public int operationAcks;
        /* access modifiers changed from: private */
        public boolean positionDiscontinuity;

        private PlaybackInfoUpdate() {
        }

        public boolean hasPendingUpdate(PlaybackInfo playbackInfo) {
            return playbackInfo != this.lastPlaybackInfo || this.operationAcks > 0 || this.positionDiscontinuity;
        }

        public void incrementPendingOperationAcks(int i) {
            this.operationAcks += i;
        }

        public void reset(PlaybackInfo playbackInfo) {
            this.lastPlaybackInfo = playbackInfo;
            this.operationAcks = 0;
            this.positionDiscontinuity = false;
        }

        public void setPositionDiscontinuity(int i) {
            boolean z = true;
            if (!this.positionDiscontinuity || this.discontinuityReason == 4) {
                this.positionDiscontinuity = true;
                this.discontinuityReason = i;
                return;
            }
            if (i != 4) {
                z = false;
            }
            Assertions.checkArgument(z);
        }
    }

    private static final class SeekPosition {
        public final Timeline timeline;
        public final int windowIndex;
        public final long windowPositionUs;

        public SeekPosition(Timeline timeline2, int i, long j) {
            this.timeline = timeline2;
            this.windowIndex = i;
            this.windowPositionUs = j;
        }
    }

    public ExoPlayerImplInternal(Renderer[] rendererArr, TrackSelector trackSelector2, TrackSelectorResult trackSelectorResult, LoadControl loadControl2, boolean z, int i, boolean z2, Handler handler2, ExoPlayer exoPlayer, Clock clock2) {
        Renderer[] rendererArr2 = rendererArr;
        Clock clock3 = clock2;
        this.renderers = rendererArr2;
        this.trackSelector = trackSelector2;
        this.emptyTrackSelectorResult = trackSelectorResult;
        this.loadControl = loadControl2;
        this.playWhenReady = z;
        this.repeatMode = i;
        this.shuffleModeEnabled = z2;
        this.eventHandler = handler2;
        this.player = exoPlayer;
        this.clock = clock3;
        this.backBufferDurationUs = loadControl2.getBackBufferDurationUs();
        this.retainBackBufferFromKeyframe = loadControl2.retainBackBufferFromKeyframe();
        this.seekParameters = SeekParameters.DEFAULT;
        this.playbackInfo = new PlaybackInfo(Timeline.EMPTY, C.TIME_UNSET, TrackGroupArray.EMPTY, trackSelectorResult);
        this.playbackInfoUpdate = new PlaybackInfoUpdate();
        this.rendererCapabilities = new RendererCapabilities[rendererArr2.length];
        for (int i2 = 0; i2 < rendererArr2.length; i2++) {
            rendererArr2[i2].setIndex(i2);
            this.rendererCapabilities[i2] = rendererArr2[i2].getCapabilities();
        }
        this.mediaClock = new DefaultMediaClock(this, clock3);
        this.pendingMessages = new ArrayList<>();
        this.enabledRenderers = new Renderer[0];
        this.window = new Timeline.Window();
        this.period = new Timeline.Period();
        trackSelector2.init(this);
        this.internalPlaybackThread = new HandlerThread("ExoPlayerImplInternal:Handler", -16);
        this.internalPlaybackThread.start();
        this.handler = clock3.createHandler(this.internalPlaybackThread.getLooper(), this);
    }

    /* access modifiers changed from: private */
    public void deliverMessage(PlayerMessage playerMessage) {
        if (!playerMessage.isCanceled()) {
            try {
                playerMessage.getTarget().handleMessage(playerMessage.getType(), playerMessage.getPayload());
            } finally {
                playerMessage.markAsProcessed(true);
            }
        }
    }

    private void disableRenderer(Renderer renderer) {
        this.mediaClock.onRendererDisabled(renderer);
        ensureStopped(renderer);
        renderer.disable();
    }

    /* JADX WARNING: Removed duplicated region for block: B:56:0x00e9  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x0107  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x010b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void doSomeWork() {
        /*
            r17 = this;
            r0 = r17
            com.google.android.exoplayer2.util.Clock r1 = r0.clock
            long r1 = r1.uptimeMillis()
            r17.updatePeriods()
            com.google.android.exoplayer2.MediaPeriodQueue r3 = r0.queue
            boolean r3 = r3.hasPlayingPeriod()
            r4 = 10
            if (r3 != 0) goto L_0x001c
            r17.maybeThrowPeriodPrepareError()
            r0.scheduleNextWork(r1, r4)
            return
        L_0x001c:
            com.google.android.exoplayer2.MediaPeriodQueue r3 = r0.queue
            com.google.android.exoplayer2.MediaPeriodHolder r3 = r3.getPlayingPeriod()
            java.lang.String r6 = "doSomeWork"
            com.google.android.exoplayer2.util.TraceUtil.beginSection(r6)
            r17.updatePlaybackPositions()
            long r6 = android.os.SystemClock.elapsedRealtime()
            r8 = 1000(0x3e8, double:4.94E-321)
            long r6 = r6 * r8
            com.google.android.exoplayer2.source.MediaPeriod r10 = r3.mediaPeriod
            com.google.android.exoplayer2.PlaybackInfo r11 = r0.playbackInfo
            long r11 = r11.positionUs
            long r13 = r0.backBufferDurationUs
            long r11 = r11 - r13
            boolean r13 = r0.retainBackBufferFromKeyframe
            r10.discardBuffer(r11, r13)
            com.google.android.exoplayer2.Renderer[] r10 = r0.enabledRenderers
            int r11 = r10.length
            r13 = 1
            r15 = r13
            r16 = r15
            r14 = 0
        L_0x0047:
            if (r14 >= r11) goto L_0x0084
            r12 = r10[r14]
            long r8 = r0.rendererPositionUs
            r12.render(r8, r6)
            if (r16 == 0) goto L_0x005b
            boolean r8 = r12.isEnded()
            if (r8 == 0) goto L_0x005b
            r16 = r13
            goto L_0x005d
        L_0x005b:
            r16 = 0
        L_0x005d:
            boolean r8 = r12.isReady()
            if (r8 != 0) goto L_0x0072
            boolean r8 = r12.isEnded()
            if (r8 != 0) goto L_0x0072
            boolean r8 = r0.rendererWaitingForNextStream(r12)
            if (r8 == 0) goto L_0x0070
            goto L_0x0072
        L_0x0070:
            r8 = 0
            goto L_0x0073
        L_0x0072:
            r8 = r13
        L_0x0073:
            if (r8 != 0) goto L_0x0078
            r12.maybeThrowStreamError()
        L_0x0078:
            if (r15 == 0) goto L_0x007e
            if (r8 == 0) goto L_0x007e
            r15 = r13
            goto L_0x007f
        L_0x007e:
            r15 = 0
        L_0x007f:
            int r14 = r14 + 1
            r8 = 1000(0x3e8, double:4.94E-321)
            goto L_0x0047
        L_0x0084:
            if (r15 != 0) goto L_0x0089
            r17.maybeThrowPeriodPrepareError()
        L_0x0089:
            com.google.android.exoplayer2.MediaPeriodInfo r6 = r3.info
            long r6 = r6.durationUs
            r8 = 4
            r9 = 3
            r10 = 2
            if (r16 == 0) goto L_0x00b0
            r11 = -9223372036854775807(0x8000000000000001, double:-4.9E-324)
            int r11 = (r6 > r11 ? 1 : (r6 == r11 ? 0 : -1))
            if (r11 == 0) goto L_0x00a3
            com.google.android.exoplayer2.PlaybackInfo r11 = r0.playbackInfo
            long r11 = r11.positionUs
            int r6 = (r6 > r11 ? 1 : (r6 == r11 ? 0 : -1))
            if (r6 > 0) goto L_0x00b0
        L_0x00a3:
            com.google.android.exoplayer2.MediaPeriodInfo r3 = r3.info
            boolean r3 = r3.isFinal
            if (r3 == 0) goto L_0x00b0
            r0.setState(r8)
        L_0x00ac:
            r17.stopRenderers()
            goto L_0x00e3
        L_0x00b0:
            com.google.android.exoplayer2.PlaybackInfo r3 = r0.playbackInfo
            int r3 = r3.playbackState
            if (r3 != r10) goto L_0x00c7
            boolean r3 = r0.shouldTransitionToReadyState(r15)
            if (r3 == 0) goto L_0x00c7
            r0.setState(r9)
            boolean r3 = r0.playWhenReady
            if (r3 == 0) goto L_0x00e3
            r17.startRenderers()
            goto L_0x00e3
        L_0x00c7:
            com.google.android.exoplayer2.PlaybackInfo r3 = r0.playbackInfo
            int r3 = r3.playbackState
            if (r3 != r9) goto L_0x00e3
            com.google.android.exoplayer2.Renderer[] r3 = r0.enabledRenderers
            int r3 = r3.length
            if (r3 != 0) goto L_0x00d9
            boolean r3 = r17.isTimelineReady()
            if (r3 == 0) goto L_0x00db
            goto L_0x00e3
        L_0x00d9:
            if (r15 != 0) goto L_0x00e3
        L_0x00db:
            boolean r3 = r0.playWhenReady
            r0.rebuffering = r3
            r0.setState(r10)
            goto L_0x00ac
        L_0x00e3:
            com.google.android.exoplayer2.PlaybackInfo r3 = r0.playbackInfo
            int r3 = r3.playbackState
            if (r3 != r10) goto L_0x00f7
            com.google.android.exoplayer2.Renderer[] r3 = r0.enabledRenderers
            int r6 = r3.length
            r7 = 0
        L_0x00ed:
            if (r7 >= r6) goto L_0x00f7
            r11 = r3[r7]
            r11.maybeThrowStreamError()
            int r7 = r7 + 1
            goto L_0x00ed
        L_0x00f7:
            boolean r3 = r0.playWhenReady
            if (r3 == 0) goto L_0x0101
            com.google.android.exoplayer2.PlaybackInfo r3 = r0.playbackInfo
            int r3 = r3.playbackState
            if (r3 == r9) goto L_0x0107
        L_0x0101:
            com.google.android.exoplayer2.PlaybackInfo r3 = r0.playbackInfo
            int r3 = r3.playbackState
            if (r3 != r10) goto L_0x010b
        L_0x0107:
            r0.scheduleNextWork(r1, r4)
            goto L_0x011d
        L_0x010b:
            com.google.android.exoplayer2.Renderer[] r4 = r0.enabledRenderers
            int r4 = r4.length
            if (r4 == 0) goto L_0x0118
            if (r3 == r8) goto L_0x0118
            r3 = 1000(0x3e8, double:4.94E-321)
            r0.scheduleNextWork(r1, r3)
            goto L_0x011d
        L_0x0118:
            com.google.android.exoplayer2.util.HandlerWrapper r1 = r0.handler
            r1.removeMessages(r10)
        L_0x011d:
            com.google.android.exoplayer2.util.TraceUtil.endSection()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.ExoPlayerImplInternal.doSomeWork():void");
    }

    private void enableRenderer(int i, boolean z, int i2) {
        MediaPeriodHolder playingPeriod = this.queue.getPlayingPeriod();
        Renderer renderer = this.renderers[i];
        this.enabledRenderers[i2] = renderer;
        if (renderer.getState() == 0) {
            TrackSelectorResult trackSelectorResult = playingPeriod.trackSelectorResult;
            RendererConfiguration rendererConfiguration = trackSelectorResult.rendererConfigurations[i];
            Format[] formats = getFormats(trackSelectorResult.selections.get(i));
            boolean z2 = this.playWhenReady && this.playbackInfo.playbackState == 3;
            renderer.enable(rendererConfiguration, formats, playingPeriod.sampleStreams[i], this.rendererPositionUs, !z && z2, playingPeriod.getRendererOffset());
            this.mediaClock.onRendererEnabled(renderer);
            if (z2) {
                renderer.start();
            }
        }
    }

    private void enableRenderers(boolean[] zArr, int i) {
        this.enabledRenderers = new Renderer[i];
        MediaPeriodHolder playingPeriod = this.queue.getPlayingPeriod();
        int i2 = 0;
        for (int i3 = 0; i3 < this.renderers.length; i3++) {
            if (playingPeriod.trackSelectorResult.isRendererEnabled(i3)) {
                enableRenderer(i3, zArr[i3], i2);
                i2++;
            }
        }
    }

    private void ensureStopped(Renderer renderer) {
        if (renderer.getState() == 2) {
            renderer.stop();
        }
    }

    private int getFirstPeriodIndex() {
        Timeline timeline = this.playbackInfo.timeline;
        if (timeline.isEmpty()) {
            return 0;
        }
        return timeline.getWindow(timeline.getFirstWindowIndex(this.shuffleModeEnabled), this.window).firstPeriodIndex;
    }

    @NonNull
    private static Format[] getFormats(TrackSelection trackSelection) {
        int length = trackSelection != null ? trackSelection.length() : 0;
        Format[] formatArr = new Format[length];
        for (int i = 0; i < length; i++) {
            formatArr[i] = trackSelection.getFormat(i);
        }
        return formatArr;
    }

    private Pair<Integer, Long> getPeriodPosition(Timeline timeline, int i, long j) {
        return timeline.getPeriodPosition(this.window, this.period, i, j);
    }

    private void handleContinueLoadingRequested(MediaPeriod mediaPeriod) {
        if (this.queue.isLoading(mediaPeriod)) {
            this.queue.reevaluateBuffer(this.rendererPositionUs);
            maybeContinueLoading();
        }
    }

    private void handlePeriodPrepared(MediaPeriod mediaPeriod) {
        if (this.queue.isLoading(mediaPeriod)) {
            MediaPeriodHolder loadingPeriod = this.queue.getLoadingPeriod();
            loadingPeriod.handlePrepared(this.mediaClock.getPlaybackParameters().speed);
            updateLoadControlTrackSelection(loadingPeriod.trackGroups, loadingPeriod.trackSelectorResult);
            if (!this.queue.hasPlayingPeriod()) {
                resetRendererPosition(this.queue.advancePlayingPeriod().info.startPositionUs);
                updatePlayingPeriodRenderers((MediaPeriodHolder) null);
            }
            maybeContinueLoading();
        }
    }

    private void handleSourceInfoRefreshEndedPlayback() {
        setState(4);
        resetInternal(false, true, false);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0061, code lost:
        if (r11.isAd() != false) goto L_0x009e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x009c, code lost:
        if (r11.isAd() != false) goto L_0x009e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x009e, code lost:
        r12 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x00a0, code lost:
        r12 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x00a1, code lost:
        r0.playbackInfo = r10.fromNewPosition(r11, r12, r14);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void handleSourceInfoRefreshed(com.google.android.exoplayer2.ExoPlayerImplInternal.MediaSourceRefreshInfo r20) {
        /*
            r19 = this;
            r0 = r19
            r1 = r20
            com.google.android.exoplayer2.source.MediaSource r2 = r1.source
            com.google.android.exoplayer2.source.MediaSource r3 = r0.mediaSource
            if (r2 == r3) goto L_0x000b
            return
        L_0x000b:
            com.google.android.exoplayer2.PlaybackInfo r2 = r0.playbackInfo
            com.google.android.exoplayer2.Timeline r2 = r2.timeline
            com.google.android.exoplayer2.Timeline r3 = r1.timeline
            java.lang.Object r1 = r1.manifest
            com.google.android.exoplayer2.MediaPeriodQueue r4 = r0.queue
            r4.setTimeline(r3)
            com.google.android.exoplayer2.PlaybackInfo r4 = r0.playbackInfo
            com.google.android.exoplayer2.PlaybackInfo r1 = r4.copyWithTimeline(r3, r1)
            r0.playbackInfo = r1
            r19.resolvePendingMessagePositions()
            int r1 = r0.pendingPrepareCount
            r4 = 0
            r5 = 1
            r6 = -9223372036854775807(0x8000000000000001, double:-4.9E-324)
            r8 = 0
            if (r1 <= 0) goto L_0x00a8
            com.google.android.exoplayer2.ExoPlayerImplInternal$PlaybackInfoUpdate r2 = r0.playbackInfoUpdate
            r2.incrementPendingOperationAcks(r1)
            r0.pendingPrepareCount = r4
            com.google.android.exoplayer2.ExoPlayerImplInternal$SeekPosition r1 = r0.pendingInitialSeekPosition
            if (r1 == 0) goto L_0x0064
            android.util.Pair r1 = r0.resolveSeekPosition(r1, r5)
            r2 = 0
            r0.pendingInitialSeekPosition = r2
            if (r1 != 0) goto L_0x0045
            goto L_0x0072
        L_0x0045:
            java.lang.Object r2 = r1.first
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r2 = r2.intValue()
            java.lang.Object r1 = r1.second
            java.lang.Long r1 = (java.lang.Long) r1
            long r14 = r1.longValue()
            com.google.android.exoplayer2.MediaPeriodQueue r1 = r0.queue
            com.google.android.exoplayer2.source.MediaSource$MediaPeriodId r11 = r1.resolveMediaPeriodIdForAds(r2, r14)
            com.google.android.exoplayer2.PlaybackInfo r10 = r0.playbackInfo
            boolean r1 = r11.isAd()
            if (r1 == 0) goto L_0x00a0
            goto L_0x009e
        L_0x0064:
            com.google.android.exoplayer2.PlaybackInfo r1 = r0.playbackInfo
            long r1 = r1.startPositionUs
            int r1 = (r1 > r6 ? 1 : (r1 == r6 ? 0 : -1))
            if (r1 != 0) goto L_0x00a7
            boolean r1 = r3.isEmpty()
            if (r1 == 0) goto L_0x0076
        L_0x0072:
            r19.handleSourceInfoRefreshEndedPlayback()
            goto L_0x00a7
        L_0x0076:
            boolean r1 = r0.shuffleModeEnabled
            int r1 = r3.getFirstWindowIndex(r1)
            android.util.Pair r1 = r0.getPeriodPosition(r3, r1, r6)
            java.lang.Object r2 = r1.first
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r2 = r2.intValue()
            java.lang.Object r1 = r1.second
            java.lang.Long r1 = (java.lang.Long) r1
            long r14 = r1.longValue()
            com.google.android.exoplayer2.MediaPeriodQueue r1 = r0.queue
            com.google.android.exoplayer2.source.MediaSource$MediaPeriodId r11 = r1.resolveMediaPeriodIdForAds(r2, r14)
            com.google.android.exoplayer2.PlaybackInfo r10 = r0.playbackInfo
            boolean r1 = r11.isAd()
            if (r1 == 0) goto L_0x00a0
        L_0x009e:
            r12 = r8
            goto L_0x00a1
        L_0x00a0:
            r12 = r14
        L_0x00a1:
            com.google.android.exoplayer2.PlaybackInfo r1 = r10.fromNewPosition(r11, r12, r14)
            r0.playbackInfo = r1
        L_0x00a7:
            return
        L_0x00a8:
            com.google.android.exoplayer2.PlaybackInfo r1 = r0.playbackInfo
            com.google.android.exoplayer2.source.MediaSource$MediaPeriodId r10 = r1.periodId
            int r10 = r10.periodIndex
            long r13 = r1.contentPositionUs
            boolean r1 = r2.isEmpty()
            if (r1 == 0) goto L_0x00d6
            boolean r1 = r3.isEmpty()
            if (r1 != 0) goto L_0x00d5
            com.google.android.exoplayer2.MediaPeriodQueue r1 = r0.queue
            com.google.android.exoplayer2.source.MediaSource$MediaPeriodId r12 = r1.resolveMediaPeriodIdForAds(r10, r13)
            com.google.android.exoplayer2.PlaybackInfo r11 = r0.playbackInfo
            boolean r1 = r12.isAd()
            if (r1 == 0) goto L_0x00cb
            goto L_0x00cc
        L_0x00cb:
            r8 = r13
        L_0x00cc:
            r1 = r13
            r13 = r8
            r15 = r1
            com.google.android.exoplayer2.PlaybackInfo r1 = r11.fromNewPosition(r12, r13, r15)
            r0.playbackInfo = r1
        L_0x00d5:
            return
        L_0x00d6:
            com.google.android.exoplayer2.MediaPeriodQueue r1 = r0.queue
            com.google.android.exoplayer2.MediaPeriodHolder r1 = r1.getFrontPeriod()
            if (r1 != 0) goto L_0x00e7
            com.google.android.exoplayer2.Timeline$Period r11 = r0.period
            com.google.android.exoplayer2.Timeline$Period r11 = r2.getPeriod(r10, r11, r5)
            java.lang.Object r11 = r11.uid
            goto L_0x00e9
        L_0x00e7:
            java.lang.Object r11 = r1.uid
        L_0x00e9:
            int r11 = r3.getIndexOfPeriod(r11)
            r12 = -1
            if (r11 != r12) goto L_0x015b
            int r2 = r0.resolveSubsequentPeriod(r10, r2, r3)
            if (r2 != r12) goto L_0x00fa
            r19.handleSourceInfoRefreshEndedPlayback()
            return
        L_0x00fa:
            com.google.android.exoplayer2.Timeline$Period r4 = r0.period
            com.google.android.exoplayer2.Timeline$Period r2 = r3.getPeriod(r2, r4)
            int r2 = r2.windowIndex
            android.util.Pair r2 = r0.getPeriodPosition(r3, r2, r6)
            java.lang.Object r4 = r2.first
            java.lang.Integer r4 = (java.lang.Integer) r4
            int r4 = r4.intValue()
            java.lang.Object r2 = r2.second
            java.lang.Long r2 = (java.lang.Long) r2
            long r6 = r2.longValue()
            com.google.android.exoplayer2.MediaPeriodQueue r2 = r0.queue
            com.google.android.exoplayer2.source.MediaSource$MediaPeriodId r14 = r2.resolveMediaPeriodIdForAds(r4, r6)
            com.google.android.exoplayer2.Timeline$Period r2 = r0.period
            r3.getPeriod(r4, r2, r5)
            if (r1 == 0) goto L_0x0144
            com.google.android.exoplayer2.Timeline$Period r2 = r0.period
            java.lang.Object r2 = r2.uid
        L_0x0127:
            com.google.android.exoplayer2.MediaPeriodInfo r3 = r1.info
            com.google.android.exoplayer2.MediaPeriodInfo r3 = r3.copyWithPeriodIndex(r12)
        L_0x012d:
            r1.info = r3
            com.google.android.exoplayer2.MediaPeriodHolder r1 = r1.next
            if (r1 == 0) goto L_0x0144
            java.lang.Object r3 = r1.uid
            boolean r3 = r3.equals(r2)
            if (r3 == 0) goto L_0x0127
            com.google.android.exoplayer2.MediaPeriodQueue r3 = r0.queue
            com.google.android.exoplayer2.MediaPeriodInfo r5 = r1.info
            com.google.android.exoplayer2.MediaPeriodInfo r3 = r3.getUpdatedMediaPeriodInfo((com.google.android.exoplayer2.MediaPeriodInfo) r5, (int) r4)
            goto L_0x012d
        L_0x0144:
            boolean r1 = r14.isAd()
            if (r1 == 0) goto L_0x014b
            goto L_0x014c
        L_0x014b:
            r8 = r6
        L_0x014c:
            long r15 = r0.seekToPeriodPosition(r14, r8)
            com.google.android.exoplayer2.PlaybackInfo r13 = r0.playbackInfo
            r17 = r6
            com.google.android.exoplayer2.PlaybackInfo r1 = r13.fromNewPosition(r14, r15, r17)
        L_0x0158:
            r0.playbackInfo = r1
            return
        L_0x015b:
            if (r11 == r10) goto L_0x0165
            com.google.android.exoplayer2.PlaybackInfo r1 = r0.playbackInfo
            com.google.android.exoplayer2.PlaybackInfo r1 = r1.copyWithPeriodIndex(r11)
            r0.playbackInfo = r1
        L_0x0165:
            com.google.android.exoplayer2.PlaybackInfo r1 = r0.playbackInfo
            com.google.android.exoplayer2.source.MediaSource$MediaPeriodId r1 = r1.periodId
            boolean r2 = r1.isAd()
            if (r2 == 0) goto L_0x0191
            com.google.android.exoplayer2.MediaPeriodQueue r2 = r0.queue
            com.google.android.exoplayer2.source.MediaSource$MediaPeriodId r12 = r2.resolveMediaPeriodIdForAds(r11, r13)
            boolean r2 = r12.equals(r1)
            if (r2 != 0) goto L_0x0191
            boolean r1 = r12.isAd()
            if (r1 == 0) goto L_0x0182
            goto L_0x0183
        L_0x0182:
            r8 = r13
        L_0x0183:
            long r1 = r0.seekToPeriodPosition(r12, r8)
            com.google.android.exoplayer2.PlaybackInfo r11 = r0.playbackInfo
            r3 = r13
            r13 = r1
            r15 = r3
            com.google.android.exoplayer2.PlaybackInfo r1 = r11.fromNewPosition(r12, r13, r15)
            goto L_0x0158
        L_0x0191:
            com.google.android.exoplayer2.MediaPeriodQueue r2 = r0.queue
            long r5 = r0.rendererPositionUs
            boolean r1 = r2.updateQueuedPeriods(r1, r5)
            if (r1 != 0) goto L_0x019e
            r0.seekToCurrentPosition(r4)
        L_0x019e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.ExoPlayerImplInternal.handleSourceInfoRefreshed(com.google.android.exoplayer2.ExoPlayerImplInternal$MediaSourceRefreshInfo):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x001b, code lost:
        r0 = r0.next;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean isTimelineReady() {
        /*
            r5 = this;
            com.google.android.exoplayer2.MediaPeriodQueue r0 = r5.queue
            com.google.android.exoplayer2.MediaPeriodHolder r0 = r0.getPlayingPeriod()
            com.google.android.exoplayer2.MediaPeriodInfo r1 = r0.info
            long r1 = r1.durationUs
            r3 = -9223372036854775807(0x8000000000000001, double:-4.9E-324)
            int r3 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r3 == 0) goto L_0x0030
            com.google.android.exoplayer2.PlaybackInfo r3 = r5.playbackInfo
            long r3 = r3.positionUs
            int r1 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r1 < 0) goto L_0x0030
            com.google.android.exoplayer2.MediaPeriodHolder r0 = r0.next
            if (r0 == 0) goto L_0x002e
            boolean r1 = r0.prepared
            if (r1 != 0) goto L_0x0030
            com.google.android.exoplayer2.MediaPeriodInfo r0 = r0.info
            com.google.android.exoplayer2.source.MediaSource$MediaPeriodId r0 = r0.id
            boolean r0 = r0.isAd()
            if (r0 == 0) goto L_0x002e
            goto L_0x0030
        L_0x002e:
            r0 = 0
            goto L_0x0031
        L_0x0030:
            r0 = 1
        L_0x0031:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.ExoPlayerImplInternal.isTimelineReady():boolean");
    }

    private void maybeContinueLoading() {
        MediaPeriodHolder loadingPeriod = this.queue.getLoadingPeriod();
        long nextLoadPositionUs = loadingPeriod.getNextLoadPositionUs();
        if (nextLoadPositionUs == Long.MIN_VALUE) {
            setIsLoading(false);
            return;
        }
        boolean shouldContinueLoading = this.loadControl.shouldContinueLoading(nextLoadPositionUs - loadingPeriod.toPeriodTime(this.rendererPositionUs), this.mediaClock.getPlaybackParameters().speed);
        setIsLoading(shouldContinueLoading);
        if (shouldContinueLoading) {
            loadingPeriod.continueLoading(this.rendererPositionUs);
        }
    }

    private void maybeNotifyPlaybackInfoChanged() {
        if (this.playbackInfoUpdate.hasPendingUpdate(this.playbackInfo)) {
            this.eventHandler.obtainMessage(0, this.playbackInfoUpdate.operationAcks, this.playbackInfoUpdate.positionDiscontinuity ? this.playbackInfoUpdate.discontinuityReason : -1, this.playbackInfo).sendToTarget();
            this.playbackInfoUpdate.reset(this.playbackInfo);
        }
    }

    private void maybeThrowPeriodPrepareError() {
        MediaPeriodHolder loadingPeriod = this.queue.getLoadingPeriod();
        MediaPeriodHolder readingPeriod = this.queue.getReadingPeriod();
        if (loadingPeriod != null && !loadingPeriod.prepared) {
            if (readingPeriod == null || readingPeriod.next == loadingPeriod) {
                Renderer[] rendererArr = this.enabledRenderers;
                int length = rendererArr.length;
                int i = 0;
                while (i < length) {
                    if (rendererArr[i].hasReadStreamToEnd()) {
                        i++;
                    } else {
                        return;
                    }
                }
                loadingPeriod.mediaPeriod.maybeThrowPrepareError();
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0059, code lost:
        if (r6.nextPendingMessageIndex < r6.pendingMessages.size()) goto L_0x005b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x005b, code lost:
        r1 = r6.pendingMessages.get(r6.nextPendingMessageIndex);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0066, code lost:
        r1 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0067, code lost:
        if (r1 == null) goto L_0x008a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x006b, code lost:
        if (r1.resolvedPeriodUid == null) goto L_0x008a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x006d, code lost:
        r3 = r1.resolvedPeriodIndex;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x006f, code lost:
        if (r3 < r0) goto L_0x0079;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0071, code lost:
        if (r3 != r0) goto L_0x008a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0077, code lost:
        if (r1.resolvedPeriodTimeUs > r7) goto L_0x008a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0079, code lost:
        r6.nextPendingMessageIndex++;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0087, code lost:
        if (r6.nextPendingMessageIndex >= r6.pendingMessages.size()) goto L_0x0066;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x008a, code lost:
        if (r1 == null) goto L_0x00d9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x008e, code lost:
        if (r1.resolvedPeriodUid == null) goto L_0x00d9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0092, code lost:
        if (r1.resolvedPeriodIndex != r0) goto L_0x00d9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x0094, code lost:
        r3 = r1.resolvedPeriodTimeUs;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x0098, code lost:
        if (r3 <= r7) goto L_0x00d9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x009c, code lost:
        if (r3 > r9) goto L_0x00d9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x009e, code lost:
        sendMessageToTarget(r1.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x00a9, code lost:
        if (r1.message.getDeleteAfterDelivery() != false) goto L_0x00bb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x00b1, code lost:
        if (r1.message.isCanceled() == false) goto L_0x00b4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x00b4, code lost:
        r6.nextPendingMessageIndex++;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x00bb, code lost:
        r6.pendingMessages.remove(r6.nextPendingMessageIndex);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x00ca, code lost:
        if (r6.nextPendingMessageIndex >= r6.pendingMessages.size()) goto L_0x00d7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x00cc, code lost:
        r1 = r6.pendingMessages.get(r6.nextPendingMessageIndex);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x00d7, code lost:
        r1 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:?, code lost:
        return;
     */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x0038  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void maybeTriggerPendingMessages(long r7, long r9) {
        /*
            r6 = this;
            java.util.ArrayList<com.google.android.exoplayer2.ExoPlayerImplInternal$PendingMessageInfo> r0 = r6.pendingMessages
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x00d9
            com.google.android.exoplayer2.PlaybackInfo r0 = r6.playbackInfo
            com.google.android.exoplayer2.source.MediaSource$MediaPeriodId r0 = r0.periodId
            boolean r0 = r0.isAd()
            if (r0 == 0) goto L_0x0014
            goto L_0x00d9
        L_0x0014:
            com.google.android.exoplayer2.PlaybackInfo r0 = r6.playbackInfo
            long r0 = r0.startPositionUs
            int r0 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            if (r0 != 0) goto L_0x001f
            r0 = 1
            long r7 = r7 - r0
        L_0x001f:
            com.google.android.exoplayer2.PlaybackInfo r0 = r6.playbackInfo
            com.google.android.exoplayer2.source.MediaSource$MediaPeriodId r0 = r0.periodId
            int r0 = r0.periodIndex
            int r1 = r6.nextPendingMessageIndex
            r2 = 0
            if (r1 <= 0) goto L_0x0035
            java.util.ArrayList<com.google.android.exoplayer2.ExoPlayerImplInternal$PendingMessageInfo> r3 = r6.pendingMessages
        L_0x002c:
            int r1 = r1 + -1
            java.lang.Object r1 = r3.get(r1)
            com.google.android.exoplayer2.ExoPlayerImplInternal$PendingMessageInfo r1 = (com.google.android.exoplayer2.ExoPlayerImplInternal.PendingMessageInfo) r1
            goto L_0x0036
        L_0x0035:
            r1 = r2
        L_0x0036:
            if (r1 == 0) goto L_0x0051
            int r3 = r1.resolvedPeriodIndex
            if (r3 > r0) goto L_0x0044
            if (r3 != r0) goto L_0x0051
            long r3 = r1.resolvedPeriodTimeUs
            int r1 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r1 <= 0) goto L_0x0051
        L_0x0044:
            int r1 = r6.nextPendingMessageIndex
            int r1 = r1 + -1
            r6.nextPendingMessageIndex = r1
            int r1 = r6.nextPendingMessageIndex
            if (r1 <= 0) goto L_0x0035
            java.util.ArrayList<com.google.android.exoplayer2.ExoPlayerImplInternal$PendingMessageInfo> r3 = r6.pendingMessages
            goto L_0x002c
        L_0x0051:
            int r1 = r6.nextPendingMessageIndex
            java.util.ArrayList<com.google.android.exoplayer2.ExoPlayerImplInternal$PendingMessageInfo> r3 = r6.pendingMessages
            int r3 = r3.size()
            if (r1 >= r3) goto L_0x0066
        L_0x005b:
            java.util.ArrayList<com.google.android.exoplayer2.ExoPlayerImplInternal$PendingMessageInfo> r1 = r6.pendingMessages
            int r3 = r6.nextPendingMessageIndex
            java.lang.Object r1 = r1.get(r3)
            com.google.android.exoplayer2.ExoPlayerImplInternal$PendingMessageInfo r1 = (com.google.android.exoplayer2.ExoPlayerImplInternal.PendingMessageInfo) r1
            goto L_0x0067
        L_0x0066:
            r1 = r2
        L_0x0067:
            if (r1 == 0) goto L_0x008a
            java.lang.Object r3 = r1.resolvedPeriodUid
            if (r3 == 0) goto L_0x008a
            int r3 = r1.resolvedPeriodIndex
            if (r3 < r0) goto L_0x0079
            if (r3 != r0) goto L_0x008a
            long r3 = r1.resolvedPeriodTimeUs
            int r3 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r3 > 0) goto L_0x008a
        L_0x0079:
            int r1 = r6.nextPendingMessageIndex
            int r1 = r1 + 1
            r6.nextPendingMessageIndex = r1
            int r1 = r6.nextPendingMessageIndex
            java.util.ArrayList<com.google.android.exoplayer2.ExoPlayerImplInternal$PendingMessageInfo> r3 = r6.pendingMessages
            int r3 = r3.size()
            if (r1 >= r3) goto L_0x0066
            goto L_0x005b
        L_0x008a:
            if (r1 == 0) goto L_0x00d9
            java.lang.Object r3 = r1.resolvedPeriodUid
            if (r3 == 0) goto L_0x00d9
            int r3 = r1.resolvedPeriodIndex
            if (r3 != r0) goto L_0x00d9
            long r3 = r1.resolvedPeriodTimeUs
            int r5 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r5 <= 0) goto L_0x00d9
            int r3 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1))
            if (r3 > 0) goto L_0x00d9
            com.google.android.exoplayer2.PlayerMessage r3 = r1.message
            r6.sendMessageToTarget(r3)
            com.google.android.exoplayer2.PlayerMessage r3 = r1.message
            boolean r3 = r3.getDeleteAfterDelivery()
            if (r3 != 0) goto L_0x00bb
            com.google.android.exoplayer2.PlayerMessage r1 = r1.message
            boolean r1 = r1.isCanceled()
            if (r1 == 0) goto L_0x00b4
            goto L_0x00bb
        L_0x00b4:
            int r1 = r6.nextPendingMessageIndex
            int r1 = r1 + 1
            r6.nextPendingMessageIndex = r1
            goto L_0x00c2
        L_0x00bb:
            java.util.ArrayList<com.google.android.exoplayer2.ExoPlayerImplInternal$PendingMessageInfo> r1 = r6.pendingMessages
            int r3 = r6.nextPendingMessageIndex
            r1.remove(r3)
        L_0x00c2:
            int r1 = r6.nextPendingMessageIndex
            java.util.ArrayList<com.google.android.exoplayer2.ExoPlayerImplInternal$PendingMessageInfo> r3 = r6.pendingMessages
            int r3 = r3.size()
            if (r1 >= r3) goto L_0x00d7
            java.util.ArrayList<com.google.android.exoplayer2.ExoPlayerImplInternal$PendingMessageInfo> r1 = r6.pendingMessages
            int r3 = r6.nextPendingMessageIndex
            java.lang.Object r1 = r1.get(r3)
            com.google.android.exoplayer2.ExoPlayerImplInternal$PendingMessageInfo r1 = (com.google.android.exoplayer2.ExoPlayerImplInternal.PendingMessageInfo) r1
            goto L_0x008a
        L_0x00d7:
            r1 = r2
            goto L_0x008a
        L_0x00d9:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.ExoPlayerImplInternal.maybeTriggerPendingMessages(long, long):void");
    }

    private void maybeUpdateLoadingPeriod() {
        this.queue.reevaluateBuffer(this.rendererPositionUs);
        if (this.queue.shouldLoadNextMediaPeriod()) {
            MediaPeriodInfo nextMediaPeriodInfo = this.queue.getNextMediaPeriodInfo(this.rendererPositionUs, this.playbackInfo);
            if (nextMediaPeriodInfo == null) {
                this.mediaSource.maybeThrowSourceInfoRefreshError();
                return;
            }
            this.queue.enqueueNextMediaPeriod(this.rendererCapabilities, this.trackSelector, this.loadControl.getAllocator(), this.mediaSource, this.playbackInfo.timeline.getPeriod(nextMediaPeriodInfo.id.periodIndex, this.period, true).uid, nextMediaPeriodInfo).prepare(this, nextMediaPeriodInfo.startPositionUs);
            setIsLoading(true);
        }
    }

    private void prepareInternal(MediaSource mediaSource2, boolean z, boolean z2) {
        this.pendingPrepareCount++;
        resetInternal(true, z, z2);
        this.loadControl.onPrepared();
        this.mediaSource = mediaSource2;
        setState(2);
        mediaSource2.prepareSource(this.player, true, this);
        this.handler.sendEmptyMessage(2);
    }

    private void releaseInternal() {
        resetInternal(true, true, true);
        this.loadControl.onReleased();
        setState(1);
        this.internalPlaybackThread.quit();
        synchronized (this) {
            this.released = true;
            notifyAll();
        }
    }

    private boolean rendererWaitingForNextStream(Renderer renderer) {
        MediaPeriodHolder mediaPeriodHolder = this.queue.getReadingPeriod().next;
        return mediaPeriodHolder != null && mediaPeriodHolder.prepared && renderer.hasReadStreamToEnd();
    }

    private void reselectTracksInternal() {
        if (this.queue.hasPlayingPeriod()) {
            float f = this.mediaClock.getPlaybackParameters().speed;
            MediaPeriodHolder playingPeriod = this.queue.getPlayingPeriod();
            MediaPeriodHolder readingPeriod = this.queue.getReadingPeriod();
            boolean z = true;
            while (playingPeriod != null && playingPeriod.prepared) {
                if (playingPeriod.selectTracks(f)) {
                    if (z) {
                        MediaPeriodHolder playingPeriod2 = this.queue.getPlayingPeriod();
                        boolean removeAfter = this.queue.removeAfter(playingPeriod2);
                        boolean[] zArr = new boolean[this.renderers.length];
                        long applyTrackSelection = playingPeriod2.applyTrackSelection(this.playbackInfo.positionUs, removeAfter, zArr);
                        updateLoadControlTrackSelection(playingPeriod2.trackGroups, playingPeriod2.trackSelectorResult);
                        PlaybackInfo playbackInfo2 = this.playbackInfo;
                        if (!(playbackInfo2.playbackState == 4 || applyTrackSelection == playbackInfo2.positionUs)) {
                            PlaybackInfo playbackInfo3 = this.playbackInfo;
                            this.playbackInfo = playbackInfo3.fromNewPosition(playbackInfo3.periodId, applyTrackSelection, playbackInfo3.contentPositionUs);
                            this.playbackInfoUpdate.setPositionDiscontinuity(4);
                            resetRendererPosition(applyTrackSelection);
                        }
                        boolean[] zArr2 = new boolean[this.renderers.length];
                        int i = 0;
                        int i2 = 0;
                        while (true) {
                            Renderer[] rendererArr = this.renderers;
                            if (i >= rendererArr.length) {
                                break;
                            }
                            Renderer renderer = rendererArr[i];
                            zArr2[i] = renderer.getState() != 0;
                            SampleStream sampleStream = playingPeriod2.sampleStreams[i];
                            if (sampleStream != null) {
                                i2++;
                            }
                            if (zArr2[i]) {
                                if (sampleStream != renderer.getStream()) {
                                    disableRenderer(renderer);
                                } else if (zArr[i]) {
                                    renderer.resetPosition(this.rendererPositionUs);
                                }
                            }
                            i++;
                        }
                        this.playbackInfo = this.playbackInfo.copyWithTrackInfo(playingPeriod2.trackGroups, playingPeriod2.trackSelectorResult);
                        enableRenderers(zArr2, i2);
                    } else {
                        this.queue.removeAfter(playingPeriod);
                        if (playingPeriod.prepared) {
                            playingPeriod.applyTrackSelection(Math.max(playingPeriod.info.startPositionUs, playingPeriod.toPeriodTime(this.rendererPositionUs)), false);
                            updateLoadControlTrackSelection(playingPeriod.trackGroups, playingPeriod.trackSelectorResult);
                        }
                    }
                    if (this.playbackInfo.playbackState != 4) {
                        maybeContinueLoading();
                        updatePlaybackPositions();
                        this.handler.sendEmptyMessage(2);
                        return;
                    }
                    return;
                }
                if (playingPeriod == readingPeriod) {
                    z = false;
                }
                playingPeriod = playingPeriod.next;
            }
        }
    }

    private void resetInternal(boolean z, boolean z2, boolean z3) {
        MediaSource mediaSource2;
        this.handler.removeMessages(2);
        this.rebuffering = false;
        this.mediaClock.stop();
        this.rendererPositionUs = 0;
        for (Renderer disableRenderer : this.enabledRenderers) {
            try {
                disableRenderer(disableRenderer);
            } catch (ExoPlaybackException | RuntimeException e) {
                Log.e(TAG, "Stop failed.", e);
            }
        }
        this.enabledRenderers = new Renderer[0];
        this.queue.clear(!z2);
        setIsLoading(false);
        if (z2) {
            this.pendingInitialSeekPosition = null;
        }
        if (z3) {
            this.queue.setTimeline(Timeline.EMPTY);
            Iterator<PendingMessageInfo> it = this.pendingMessages.iterator();
            while (it.hasNext()) {
                it.next().message.markAsProcessed(false);
            }
            this.pendingMessages.clear();
            this.nextPendingMessageIndex = 0;
        }
        Timeline timeline = z3 ? Timeline.EMPTY : this.playbackInfo.timeline;
        Object obj = z3 ? null : this.playbackInfo.manifest;
        MediaSource.MediaPeriodId mediaPeriodId = z2 ? new MediaSource.MediaPeriodId(getFirstPeriodIndex()) : this.playbackInfo.periodId;
        long j = C.TIME_UNSET;
        long j2 = z2 ? -9223372036854775807L : this.playbackInfo.positionUs;
        if (!z2) {
            j = this.playbackInfo.contentPositionUs;
        }
        long j3 = j;
        PlaybackInfo playbackInfo2 = this.playbackInfo;
        this.playbackInfo = new PlaybackInfo(timeline, obj, mediaPeriodId, j2, j3, playbackInfo2.playbackState, false, z3 ? TrackGroupArray.EMPTY : playbackInfo2.trackGroups, z3 ? this.emptyTrackSelectorResult : this.playbackInfo.trackSelectorResult);
        if (z && (mediaSource2 = this.mediaSource) != null) {
            mediaSource2.releaseSource(this);
            this.mediaSource = null;
        }
    }

    private void resetRendererPosition(long j) {
        if (this.queue.hasPlayingPeriod()) {
            j = this.queue.getPlayingPeriod().toRendererTime(j);
        }
        this.rendererPositionUs = j;
        this.mediaClock.resetPosition(this.rendererPositionUs);
        for (Renderer resetPosition : this.enabledRenderers) {
            resetPosition.resetPosition(this.rendererPositionUs);
        }
    }

    private boolean resolvePendingMessagePosition(PendingMessageInfo pendingMessageInfo) {
        Object obj = pendingMessageInfo.resolvedPeriodUid;
        if (obj == null) {
            Pair<Integer, Long> resolveSeekPosition = resolveSeekPosition(new SeekPosition(pendingMessageInfo.message.getTimeline(), pendingMessageInfo.message.getWindowIndex(), C.msToUs(pendingMessageInfo.message.getPositionMs())), false);
            if (resolveSeekPosition == null) {
                return false;
            }
            pendingMessageInfo.setResolvedPosition(((Integer) resolveSeekPosition.first).intValue(), ((Long) resolveSeekPosition.second).longValue(), this.playbackInfo.timeline.getPeriod(((Integer) resolveSeekPosition.first).intValue(), this.period, true).uid);
        } else {
            int indexOfPeriod = this.playbackInfo.timeline.getIndexOfPeriod(obj);
            if (indexOfPeriod == -1) {
                return false;
            }
            pendingMessageInfo.resolvedPeriodIndex = indexOfPeriod;
        }
        return true;
    }

    private void resolvePendingMessagePositions() {
        for (int size = this.pendingMessages.size() - 1; size >= 0; size--) {
            if (!resolvePendingMessagePosition(this.pendingMessages.get(size))) {
                this.pendingMessages.get(size).message.markAsProcessed(false);
                this.pendingMessages.remove(size);
            }
        }
        Collections.sort(this.pendingMessages);
    }

    private Pair<Integer, Long> resolveSeekPosition(SeekPosition seekPosition, boolean z) {
        int resolveSubsequentPeriod;
        Timeline timeline = this.playbackInfo.timeline;
        Timeline timeline2 = seekPosition.timeline;
        if (timeline.isEmpty()) {
            return null;
        }
        if (timeline2.isEmpty()) {
            timeline2 = timeline;
        }
        try {
            Pair<Integer, Long> periodPosition = timeline2.getPeriodPosition(this.window, this.period, seekPosition.windowIndex, seekPosition.windowPositionUs);
            if (timeline == timeline2) {
                return periodPosition;
            }
            int indexOfPeriod = timeline.getIndexOfPeriod(timeline2.getPeriod(((Integer) periodPosition.first).intValue(), this.period, true).uid);
            if (indexOfPeriod != -1) {
                return Pair.create(Integer.valueOf(indexOfPeriod), periodPosition.second);
            }
            if (!z || (resolveSubsequentPeriod = resolveSubsequentPeriod(((Integer) periodPosition.first).intValue(), timeline2, timeline)) == -1) {
                return null;
            }
            return getPeriodPosition(timeline, timeline.getPeriod(resolveSubsequentPeriod, this.period).windowIndex, C.TIME_UNSET);
        } catch (IndexOutOfBoundsException unused) {
            throw new IllegalSeekPositionException(timeline, seekPosition.windowIndex, seekPosition.windowPositionUs);
        }
    }

    private int resolveSubsequentPeriod(int i, Timeline timeline, Timeline timeline2) {
        int periodCount = timeline.getPeriodCount();
        int i2 = i;
        int i3 = -1;
        for (int i4 = 0; i4 < periodCount && i3 == -1; i4++) {
            i2 = timeline.getNextPeriodIndex(i2, this.period, this.window, this.repeatMode, this.shuffleModeEnabled);
            if (i2 == -1) {
                break;
            }
            i3 = timeline2.getIndexOfPeriod(timeline.getPeriod(i2, this.period, true).uid);
        }
        return i3;
    }

    private void scheduleNextWork(long j, long j2) {
        this.handler.removeMessages(2);
        this.handler.sendEmptyMessageAtTime(2, j + j2);
    }

    private void seekToCurrentPosition(boolean z) {
        MediaSource.MediaPeriodId mediaPeriodId = this.queue.getPlayingPeriod().info.id;
        long seekToPeriodPosition = seekToPeriodPosition(mediaPeriodId, this.playbackInfo.positionUs, true);
        if (seekToPeriodPosition != this.playbackInfo.positionUs) {
            PlaybackInfo playbackInfo2 = this.playbackInfo;
            this.playbackInfo = playbackInfo2.fromNewPosition(mediaPeriodId, seekToPeriodPosition, playbackInfo2.contentPositionUs);
            if (z) {
                this.playbackInfoUpdate.setPositionDiscontinuity(4);
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:46:0x00d4  */
    /* JADX WARNING: Removed duplicated region for block: B:55:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void seekToInternal(com.google.android.exoplayer2.ExoPlayerImplInternal.SeekPosition r21) {
        /*
            r20 = this;
            r1 = r20
            r0 = r21
            com.google.android.exoplayer2.ExoPlayerImplInternal$PlaybackInfoUpdate r2 = r1.playbackInfoUpdate
            r3 = 1
            r2.incrementPendingOperationAcks(r3)
            android.util.Pair r2 = r1.resolveSeekPosition(r0, r3)
            r4 = 0
            r6 = 0
            r7 = -9223372036854775807(0x8000000000000001, double:-4.9E-324)
            if (r2 != 0) goto L_0x0027
            com.google.android.exoplayer2.source.MediaSource$MediaPeriodId r2 = new com.google.android.exoplayer2.source.MediaSource$MediaPeriodId
            int r9 = r20.getFirstPeriodIndex()
            r2.<init>(r9)
            r15 = r2
            r2 = r3
            r12 = r7
            r18 = r12
            goto L_0x005b
        L_0x0027:
            java.lang.Object r9 = r2.first
            java.lang.Integer r9 = (java.lang.Integer) r9
            int r9 = r9.intValue()
            java.lang.Object r10 = r2.second
            java.lang.Long r10 = (java.lang.Long) r10
            long r10 = r10.longValue()
            com.google.android.exoplayer2.MediaPeriodQueue r12 = r1.queue
            com.google.android.exoplayer2.source.MediaSource$MediaPeriodId r9 = r12.resolveMediaPeriodIdForAds(r9, r10)
            boolean r12 = r9.isAd()
            if (r12 == 0) goto L_0x0049
            r2 = r3
            r12 = r4
        L_0x0045:
            r15 = r9
            r18 = r10
            goto L_0x005b
        L_0x0049:
            java.lang.Object r2 = r2.second
            java.lang.Long r2 = (java.lang.Long) r2
            long r12 = r2.longValue()
            long r14 = r0.windowPositionUs
            int r2 = (r14 > r7 ? 1 : (r14 == r7 ? 0 : -1))
            if (r2 != 0) goto L_0x0059
            r2 = r3
            goto L_0x0045
        L_0x0059:
            r2 = r6
            goto L_0x0045
        L_0x005b:
            r9 = 2
            com.google.android.exoplayer2.source.MediaSource r10 = r1.mediaSource     // Catch:{ all -> 0x00da }
            if (r10 == 0) goto L_0x00c6
            int r10 = r1.pendingPrepareCount     // Catch:{ all -> 0x00da }
            if (r10 <= 0) goto L_0x0065
            goto L_0x00c6
        L_0x0065:
            int r0 = (r12 > r7 ? 1 : (r12 == r7 ? 0 : -1))
            if (r0 != 0) goto L_0x0071
            r0 = 4
            r1.setState(r0)     // Catch:{ all -> 0x00da }
            r1.resetInternal(r6, r3, r6)     // Catch:{ all -> 0x00da }
            goto L_0x00c8
        L_0x0071:
            com.google.android.exoplayer2.PlaybackInfo r0 = r1.playbackInfo     // Catch:{ all -> 0x00da }
            com.google.android.exoplayer2.source.MediaSource$MediaPeriodId r0 = r0.periodId     // Catch:{ all -> 0x00da }
            boolean r0 = r15.equals(r0)     // Catch:{ all -> 0x00da }
            if (r0 == 0) goto L_0x00b7
            com.google.android.exoplayer2.MediaPeriodQueue r0 = r1.queue     // Catch:{ all -> 0x00da }
            com.google.android.exoplayer2.MediaPeriodHolder r0 = r0.getPlayingPeriod()     // Catch:{ all -> 0x00da }
            if (r0 == 0) goto L_0x0090
            int r4 = (r12 > r4 ? 1 : (r12 == r4 ? 0 : -1))
            if (r4 == 0) goto L_0x0090
            com.google.android.exoplayer2.source.MediaPeriod r0 = r0.mediaPeriod     // Catch:{ all -> 0x00da }
            com.google.android.exoplayer2.SeekParameters r4 = r1.seekParameters     // Catch:{ all -> 0x00da }
            long r4 = r0.getAdjustedSeekPositionUs(r12, r4)     // Catch:{ all -> 0x00da }
            goto L_0x0091
        L_0x0090:
            r4 = r12
        L_0x0091:
            long r7 = com.google.android.exoplayer2.C.usToMs(r4)     // Catch:{ all -> 0x00da }
            com.google.android.exoplayer2.PlaybackInfo r0 = r1.playbackInfo     // Catch:{ all -> 0x00da }
            long r10 = r0.positionUs     // Catch:{ all -> 0x00da }
            long r10 = com.google.android.exoplayer2.C.usToMs(r10)     // Catch:{ all -> 0x00da }
            int r0 = (r7 > r10 ? 1 : (r7 == r10 ? 0 : -1))
            if (r0 != 0) goto L_0x00b8
            com.google.android.exoplayer2.PlaybackInfo r0 = r1.playbackInfo     // Catch:{ all -> 0x00da }
            long r3 = r0.positionUs     // Catch:{ all -> 0x00da }
            com.google.android.exoplayer2.PlaybackInfo r14 = r1.playbackInfo
            r16 = r3
            com.google.android.exoplayer2.PlaybackInfo r0 = r14.fromNewPosition(r15, r16, r18)
            r1.playbackInfo = r0
            if (r2 == 0) goto L_0x00b6
            com.google.android.exoplayer2.ExoPlayerImplInternal$PlaybackInfoUpdate r0 = r1.playbackInfoUpdate
            r0.setPositionDiscontinuity(r9)
        L_0x00b6:
            return
        L_0x00b7:
            r4 = r12
        L_0x00b8:
            long r4 = r1.seekToPeriodPosition(r15, r4)     // Catch:{ all -> 0x00da }
            int r0 = (r12 > r4 ? 1 : (r12 == r4 ? 0 : -1))
            if (r0 == 0) goto L_0x00c1
            goto L_0x00c2
        L_0x00c1:
            r3 = r6
        L_0x00c2:
            r2 = r2 | r3
            r16 = r4
            goto L_0x00ca
        L_0x00c6:
            r1.pendingInitialSeekPosition = r0     // Catch:{ all -> 0x00da }
        L_0x00c8:
            r16 = r12
        L_0x00ca:
            com.google.android.exoplayer2.PlaybackInfo r14 = r1.playbackInfo
            com.google.android.exoplayer2.PlaybackInfo r0 = r14.fromNewPosition(r15, r16, r18)
            r1.playbackInfo = r0
            if (r2 == 0) goto L_0x00d9
            com.google.android.exoplayer2.ExoPlayerImplInternal$PlaybackInfoUpdate r0 = r1.playbackInfoUpdate
            r0.setPositionDiscontinuity(r9)
        L_0x00d9:
            return
        L_0x00da:
            r0 = move-exception
            com.google.android.exoplayer2.PlaybackInfo r14 = r1.playbackInfo
            r16 = r12
            com.google.android.exoplayer2.PlaybackInfo r3 = r14.fromNewPosition(r15, r16, r18)
            r1.playbackInfo = r3
            if (r2 == 0) goto L_0x00ec
            com.google.android.exoplayer2.ExoPlayerImplInternal$PlaybackInfoUpdate r2 = r1.playbackInfoUpdate
            r2.setPositionDiscontinuity(r9)
        L_0x00ec:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.ExoPlayerImplInternal.seekToInternal(com.google.android.exoplayer2.ExoPlayerImplInternal$SeekPosition):void");
    }

    private long seekToPeriodPosition(MediaSource.MediaPeriodId mediaPeriodId, long j) {
        return seekToPeriodPosition(mediaPeriodId, j, this.queue.getPlayingPeriod() != this.queue.getReadingPeriod());
    }

    private long seekToPeriodPosition(MediaSource.MediaPeriodId mediaPeriodId, long j, boolean z) {
        stopRenderers();
        this.rebuffering = false;
        setState(2);
        MediaPeriodHolder playingPeriod = this.queue.getPlayingPeriod();
        MediaPeriodHolder mediaPeriodHolder = playingPeriod;
        while (true) {
            if (mediaPeriodHolder == null) {
                break;
            } else if (shouldKeepPeriodHolder(mediaPeriodId, j, mediaPeriodHolder)) {
                this.queue.removeAfter(mediaPeriodHolder);
                break;
            } else {
                mediaPeriodHolder = this.queue.advancePlayingPeriod();
            }
        }
        if (playingPeriod != mediaPeriodHolder || z) {
            for (Renderer disableRenderer : this.enabledRenderers) {
                disableRenderer(disableRenderer);
            }
            this.enabledRenderers = new Renderer[0];
            playingPeriod = null;
        }
        if (mediaPeriodHolder != null) {
            updatePlayingPeriodRenderers(playingPeriod);
            if (mediaPeriodHolder.hasEnabledTracks) {
                long seekToUs = mediaPeriodHolder.mediaPeriod.seekToUs(j);
                mediaPeriodHolder.mediaPeriod.discardBuffer(seekToUs - this.backBufferDurationUs, this.retainBackBufferFromKeyframe);
                j = seekToUs;
            }
            resetRendererPosition(j);
            maybeContinueLoading();
        } else {
            this.queue.clear(true);
            resetRendererPosition(j);
        }
        this.handler.sendEmptyMessage(2);
        return j;
    }

    private void sendMessageInternal(PlayerMessage playerMessage) {
        if (playerMessage.getPositionMs() == C.TIME_UNSET) {
            sendMessageToTarget(playerMessage);
        } else if (this.mediaSource == null || this.pendingPrepareCount > 0) {
            this.pendingMessages.add(new PendingMessageInfo(playerMessage));
        } else {
            PendingMessageInfo pendingMessageInfo = new PendingMessageInfo(playerMessage);
            if (resolvePendingMessagePosition(pendingMessageInfo)) {
                this.pendingMessages.add(pendingMessageInfo);
                Collections.sort(this.pendingMessages);
                return;
            }
            playerMessage.markAsProcessed(false);
        }
    }

    private void sendMessageToTarget(PlayerMessage playerMessage) {
        if (playerMessage.getHandler().getLooper() == this.handler.getLooper()) {
            deliverMessage(playerMessage);
            int i = this.playbackInfo.playbackState;
            if (i == 3 || i == 2) {
                this.handler.sendEmptyMessage(2);
                return;
            }
            return;
        }
        this.handler.obtainMessage(15, playerMessage).sendToTarget();
    }

    private void sendMessageToTargetThread(final PlayerMessage playerMessage) {
        playerMessage.getHandler().post(new Runnable() {
            public void run() {
                try {
                    ExoPlayerImplInternal.this.deliverMessage(playerMessage);
                } catch (ExoPlaybackException e) {
                    Log.e(ExoPlayerImplInternal.TAG, "Unexpected error delivering message on external thread.", e);
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void setIsLoading(boolean z) {
        PlaybackInfo playbackInfo2 = this.playbackInfo;
        if (playbackInfo2.isLoading != z) {
            this.playbackInfo = playbackInfo2.copyWithIsLoading(z);
        }
    }

    private void setPlayWhenReadyInternal(boolean z) {
        this.rebuffering = false;
        this.playWhenReady = z;
        if (!z) {
            stopRenderers();
            updatePlaybackPositions();
            return;
        }
        int i = this.playbackInfo.playbackState;
        if (i == 3) {
            startRenderers();
        } else if (i != 2) {
            return;
        }
        this.handler.sendEmptyMessage(2);
    }

    private void setPlaybackParametersInternal(PlaybackParameters playbackParameters) {
        this.mediaClock.setPlaybackParameters(playbackParameters);
    }

    private void setRepeatModeInternal(int i) {
        this.repeatMode = i;
        if (!this.queue.updateRepeatMode(i)) {
            seekToCurrentPosition(true);
        }
    }

    private void setSeekParametersInternal(SeekParameters seekParameters2) {
        this.seekParameters = seekParameters2;
    }

    private void setShuffleModeEnabledInternal(boolean z) {
        this.shuffleModeEnabled = z;
        if (!this.queue.updateShuffleModeEnabled(z)) {
            seekToCurrentPosition(true);
        }
    }

    private void setState(int i) {
        PlaybackInfo playbackInfo2 = this.playbackInfo;
        if (playbackInfo2.playbackState != i) {
            this.playbackInfo = playbackInfo2.copyWithPlaybackState(i);
        }
    }

    private boolean shouldKeepPeriodHolder(MediaSource.MediaPeriodId mediaPeriodId, long j, MediaPeriodHolder mediaPeriodHolder) {
        if (!mediaPeriodId.equals(mediaPeriodHolder.info.id) || !mediaPeriodHolder.prepared) {
            return false;
        }
        this.playbackInfo.timeline.getPeriod(mediaPeriodHolder.info.id.periodIndex, this.period);
        int adGroupIndexAfterPositionUs = this.period.getAdGroupIndexAfterPositionUs(j);
        return adGroupIndexAfterPositionUs == -1 || this.period.getAdGroupTimeUs(adGroupIndexAfterPositionUs) == mediaPeriodHolder.info.endPositionUs;
    }

    private boolean shouldTransitionToReadyState(boolean z) {
        if (this.enabledRenderers.length == 0) {
            return isTimelineReady();
        }
        if (!z) {
            return false;
        }
        if (!this.playbackInfo.isLoading) {
            return true;
        }
        MediaPeriodHolder loadingPeriod = this.queue.getLoadingPeriod();
        long bufferedPositionUs = loadingPeriod.getBufferedPositionUs(!loadingPeriod.info.isFinal);
        return bufferedPositionUs == Long.MIN_VALUE || this.loadControl.shouldStartPlayback(bufferedPositionUs - loadingPeriod.toPeriodTime(this.rendererPositionUs), this.mediaClock.getPlaybackParameters().speed, this.rebuffering);
    }

    private void startRenderers() {
        this.rebuffering = false;
        this.mediaClock.start();
        for (Renderer start : this.enabledRenderers) {
            start.start();
        }
    }

    private void stopInternal(boolean z, boolean z2) {
        resetInternal(true, z, z);
        this.playbackInfoUpdate.incrementPendingOperationAcks(this.pendingPrepareCount + (z2 ? 1 : 0));
        this.pendingPrepareCount = 0;
        this.loadControl.onStopped();
        setState(1);
    }

    private void stopRenderers() {
        this.mediaClock.stop();
        for (Renderer ensureStopped : this.enabledRenderers) {
            ensureStopped(ensureStopped);
        }
    }

    private void updateLoadControlTrackSelection(TrackGroupArray trackGroupArray, TrackSelectorResult trackSelectorResult) {
        this.loadControl.onTracksSelected(this.renderers, trackGroupArray, trackSelectorResult.selections);
    }

    private void updatePeriods() {
        MediaSource mediaSource2 = this.mediaSource;
        if (mediaSource2 != null) {
            if (this.pendingPrepareCount > 0) {
                mediaSource2.maybeThrowSourceInfoRefreshError();
                return;
            }
            maybeUpdateLoadingPeriod();
            MediaPeriodHolder loadingPeriod = this.queue.getLoadingPeriod();
            int i = 0;
            if (loadingPeriod == null || loadingPeriod.isFullyBuffered()) {
                setIsLoading(false);
            } else if (!this.playbackInfo.isLoading) {
                maybeContinueLoading();
            }
            if (this.queue.hasPlayingPeriod()) {
                MediaPeriodHolder playingPeriod = this.queue.getPlayingPeriod();
                MediaPeriodHolder readingPeriod = this.queue.getReadingPeriod();
                boolean z = false;
                while (this.playWhenReady && playingPeriod != readingPeriod && this.rendererPositionUs >= playingPeriod.next.rendererPositionOffsetUs) {
                    if (z) {
                        maybeNotifyPlaybackInfoChanged();
                    }
                    int i2 = playingPeriod.info.isLastInTimelinePeriod ? 0 : 3;
                    MediaPeriodHolder advancePlayingPeriod = this.queue.advancePlayingPeriod();
                    updatePlayingPeriodRenderers(playingPeriod);
                    PlaybackInfo playbackInfo2 = this.playbackInfo;
                    MediaPeriodInfo mediaPeriodInfo = advancePlayingPeriod.info;
                    this.playbackInfo = playbackInfo2.fromNewPosition(mediaPeriodInfo.id, mediaPeriodInfo.startPositionUs, mediaPeriodInfo.contentPositionUs);
                    this.playbackInfoUpdate.setPositionDiscontinuity(i2);
                    updatePlaybackPositions();
                    z = true;
                    playingPeriod = advancePlayingPeriod;
                }
                if (readingPeriod.info.isFinal) {
                    while (true) {
                        Renderer[] rendererArr = this.renderers;
                        if (i < rendererArr.length) {
                            Renderer renderer = rendererArr[i];
                            SampleStream sampleStream = readingPeriod.sampleStreams[i];
                            if (sampleStream != null && renderer.getStream() == sampleStream && renderer.hasReadStreamToEnd()) {
                                renderer.setCurrentStreamFinal();
                            }
                            i++;
                        } else {
                            return;
                        }
                    }
                } else {
                    MediaPeriodHolder mediaPeriodHolder = readingPeriod.next;
                    if (mediaPeriodHolder != null && mediaPeriodHolder.prepared) {
                        int i3 = 0;
                        while (true) {
                            Renderer[] rendererArr2 = this.renderers;
                            if (i3 < rendererArr2.length) {
                                Renderer renderer2 = rendererArr2[i3];
                                SampleStream sampleStream2 = readingPeriod.sampleStreams[i3];
                                if (renderer2.getStream() != sampleStream2) {
                                    return;
                                }
                                if (sampleStream2 == null || renderer2.hasReadStreamToEnd()) {
                                    i3++;
                                } else {
                                    return;
                                }
                            } else {
                                TrackSelectorResult trackSelectorResult = readingPeriod.trackSelectorResult;
                                MediaPeriodHolder advanceReadingPeriod = this.queue.advanceReadingPeriod();
                                TrackSelectorResult trackSelectorResult2 = advanceReadingPeriod.trackSelectorResult;
                                boolean z2 = advanceReadingPeriod.mediaPeriod.readDiscontinuity() != C.TIME_UNSET;
                                int i4 = 0;
                                while (true) {
                                    Renderer[] rendererArr3 = this.renderers;
                                    if (i4 < rendererArr3.length) {
                                        Renderer renderer3 = rendererArr3[i4];
                                        if (trackSelectorResult.isRendererEnabled(i4)) {
                                            if (!z2) {
                                                if (!renderer3.isCurrentStreamFinal()) {
                                                    TrackSelection trackSelection = trackSelectorResult2.selections.get(i4);
                                                    boolean isRendererEnabled = trackSelectorResult2.isRendererEnabled(i4);
                                                    boolean z3 = this.rendererCapabilities[i4].getTrackType() == 5;
                                                    RendererConfiguration rendererConfiguration = trackSelectorResult.rendererConfigurations[i4];
                                                    RendererConfiguration rendererConfiguration2 = trackSelectorResult2.rendererConfigurations[i4];
                                                    if (isRendererEnabled && rendererConfiguration2.equals(rendererConfiguration) && !z3) {
                                                        renderer3.replaceStream(getFormats(trackSelection), advanceReadingPeriod.sampleStreams[i4], advanceReadingPeriod.getRendererOffset());
                                                    }
                                                }
                                            }
                                            renderer3.setCurrentStreamFinal();
                                        }
                                        i4++;
                                    } else {
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void updatePlaybackPositions() {
        if (this.queue.hasPlayingPeriod()) {
            MediaPeriodHolder playingPeriod = this.queue.getPlayingPeriod();
            long readDiscontinuity = playingPeriod.mediaPeriod.readDiscontinuity();
            if (readDiscontinuity != C.TIME_UNSET) {
                resetRendererPosition(readDiscontinuity);
                if (readDiscontinuity != this.playbackInfo.positionUs) {
                    PlaybackInfo playbackInfo2 = this.playbackInfo;
                    this.playbackInfo = playbackInfo2.fromNewPosition(playbackInfo2.periodId, readDiscontinuity, playbackInfo2.contentPositionUs);
                    this.playbackInfoUpdate.setPositionDiscontinuity(4);
                }
            } else {
                this.rendererPositionUs = this.mediaClock.syncAndGetPositionUs();
                long periodTime = playingPeriod.toPeriodTime(this.rendererPositionUs);
                maybeTriggerPendingMessages(this.playbackInfo.positionUs, periodTime);
                this.playbackInfo.positionUs = periodTime;
            }
            this.playbackInfo.bufferedPositionUs = this.enabledRenderers.length == 0 ? playingPeriod.info.durationUs : playingPeriod.getBufferedPositionUs(true);
        }
    }

    private void updatePlayingPeriodRenderers(@Nullable MediaPeriodHolder mediaPeriodHolder) {
        MediaPeriodHolder playingPeriod = this.queue.getPlayingPeriod();
        if (playingPeriod != null && mediaPeriodHolder != playingPeriod) {
            boolean[] zArr = new boolean[this.renderers.length];
            int i = 0;
            int i2 = 0;
            while (true) {
                Renderer[] rendererArr = this.renderers;
                if (i < rendererArr.length) {
                    Renderer renderer = rendererArr[i];
                    zArr[i] = renderer.getState() != 0;
                    if (playingPeriod.trackSelectorResult.isRendererEnabled(i)) {
                        i2++;
                    }
                    if (zArr[i] && (!playingPeriod.trackSelectorResult.isRendererEnabled(i) || (renderer.isCurrentStreamFinal() && renderer.getStream() == mediaPeriodHolder.sampleStreams[i]))) {
                        disableRenderer(renderer);
                    }
                    i++;
                } else {
                    this.playbackInfo = this.playbackInfo.copyWithTrackInfo(playingPeriod.trackGroups, playingPeriod.trackSelectorResult);
                    enableRenderers(zArr, i2);
                    return;
                }
            }
        }
    }

    private void updateTrackSelectionPlaybackSpeed(float f) {
        for (MediaPeriodHolder frontPeriod = this.queue.getFrontPeriod(); frontPeriod != null; frontPeriod = frontPeriod.next) {
            TrackSelectorResult trackSelectorResult = frontPeriod.trackSelectorResult;
            if (trackSelectorResult != null) {
                for (TrackSelection trackSelection : trackSelectorResult.selections.getAll()) {
                    if (trackSelection != null) {
                        trackSelection.onPlaybackSpeed(f);
                    }
                }
            }
        }
    }

    public Looper getPlaybackLooper() {
        return this.internalPlaybackThread.getLooper();
    }

    public boolean handleMessage(Message message) {
        Handler handler2;
        try {
            switch (message.what) {
                case 0:
                    prepareInternal((MediaSource) message.obj, message.arg1 != 0, message.arg2 != 0);
                    break;
                case 1:
                    setPlayWhenReadyInternal(message.arg1 != 0);
                    break;
                case 2:
                    doSomeWork();
                    break;
                case 3:
                    seekToInternal((SeekPosition) message.obj);
                    break;
                case 4:
                    setPlaybackParametersInternal((PlaybackParameters) message.obj);
                    break;
                case 5:
                    setSeekParametersInternal((SeekParameters) message.obj);
                    break;
                case 6:
                    stopInternal(message.arg1 != 0, true);
                    break;
                case 7:
                    releaseInternal();
                    return true;
                case 8:
                    handleSourceInfoRefreshed((MediaSourceRefreshInfo) message.obj);
                    break;
                case 9:
                    handlePeriodPrepared((MediaPeriod) message.obj);
                    break;
                case 10:
                    handleContinueLoadingRequested((MediaPeriod) message.obj);
                    break;
                case 11:
                    reselectTracksInternal();
                    break;
                case 12:
                    setRepeatModeInternal(message.arg1);
                    break;
                case 13:
                    setShuffleModeEnabledInternal(message.arg1 != 0);
                    break;
                case 14:
                    sendMessageInternal((PlayerMessage) message.obj);
                    break;
                case 15:
                    sendMessageToTargetThread((PlayerMessage) message.obj);
                    break;
                default:
                    return false;
            }
            maybeNotifyPlaybackInfoChanged();
        } catch (ExoPlaybackException e) {
            e = e;
            Log.e(TAG, "Playback error.", e);
            stopInternal(false, false);
            handler2 = this.eventHandler;
            handler2.obtainMessage(2, e).sendToTarget();
            maybeNotifyPlaybackInfoChanged();
            return true;
        } catch (IOException e2) {
            Log.e(TAG, "Source error.", e2);
            stopInternal(false, false);
            handler2 = this.eventHandler;
            e = ExoPlaybackException.createForSource(e2);
            handler2.obtainMessage(2, e).sendToTarget();
            maybeNotifyPlaybackInfoChanged();
            return true;
        } catch (RuntimeException e3) {
            Log.e(TAG, "Internal runtime error.", e3);
            stopInternal(false, false);
            handler2 = this.eventHandler;
            e = ExoPlaybackException.createForUnexpected(e3);
            handler2.obtainMessage(2, e).sendToTarget();
            maybeNotifyPlaybackInfoChanged();
            return true;
        }
        return true;
    }

    public void onContinueLoadingRequested(MediaPeriod mediaPeriod) {
        this.handler.obtainMessage(10, mediaPeriod).sendToTarget();
    }

    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
        this.eventHandler.obtainMessage(1, playbackParameters).sendToTarget();
        updateTrackSelectionPlaybackSpeed(playbackParameters.speed);
    }

    public void onPrepared(MediaPeriod mediaPeriod) {
        this.handler.obtainMessage(9, mediaPeriod).sendToTarget();
    }

    public void onSourceInfoRefreshed(MediaSource mediaSource2, Timeline timeline, Object obj) {
        this.handler.obtainMessage(8, new MediaSourceRefreshInfo(mediaSource2, timeline, obj)).sendToTarget();
    }

    public void onTrackSelectionsInvalidated() {
        this.handler.sendEmptyMessage(11);
    }

    public void prepare(MediaSource mediaSource2, boolean z, boolean z2) {
        this.handler.obtainMessage(0, z ? 1 : 0, z2 ? 1 : 0, mediaSource2).sendToTarget();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0022, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void release() {
        /*
            r2 = this;
            monitor-enter(r2)
            boolean r0 = r2.released     // Catch:{ all -> 0x0023 }
            if (r0 == 0) goto L_0x0007
            monitor-exit(r2)
            return
        L_0x0007:
            com.google.android.exoplayer2.util.HandlerWrapper r0 = r2.handler     // Catch:{ all -> 0x0023 }
            r1 = 7
            r0.sendEmptyMessage(r1)     // Catch:{ all -> 0x0023 }
            r0 = 0
        L_0x000e:
            boolean r1 = r2.released     // Catch:{ all -> 0x0023 }
            if (r1 != 0) goto L_0x0018
            r2.wait()     // Catch:{ InterruptedException -> 0x0016 }
            goto L_0x000e
        L_0x0016:
            r0 = 1
            goto L_0x000e
        L_0x0018:
            if (r0 == 0) goto L_0x0021
            java.lang.Thread r0 = java.lang.Thread.currentThread()     // Catch:{ all -> 0x0023 }
            r0.interrupt()     // Catch:{ all -> 0x0023 }
        L_0x0021:
            monitor-exit(r2)
            return
        L_0x0023:
            r0 = move-exception
            monitor-exit(r2)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.ExoPlayerImplInternal.release():void");
    }

    public void seekTo(Timeline timeline, int i, long j) {
        this.handler.obtainMessage(3, new SeekPosition(timeline, i, j)).sendToTarget();
    }

    public synchronized void sendMessage(PlayerMessage playerMessage) {
        if (this.released) {
            Log.w(TAG, "Ignoring messages sent after release.");
            playerMessage.markAsProcessed(false);
            return;
        }
        this.handler.obtainMessage(14, playerMessage).sendToTarget();
    }

    public void setPlayWhenReady(boolean z) {
        this.handler.obtainMessage(1, z ? 1 : 0, 0).sendToTarget();
    }

    public void setPlaybackParameters(PlaybackParameters playbackParameters) {
        this.handler.obtainMessage(4, playbackParameters).sendToTarget();
    }

    public void setRepeatMode(int i) {
        this.handler.obtainMessage(12, i, 0).sendToTarget();
    }

    public void setSeekParameters(SeekParameters seekParameters2) {
        this.handler.obtainMessage(5, seekParameters2).sendToTarget();
    }

    public void setShuffleModeEnabled(boolean z) {
        this.handler.obtainMessage(13, z ? 1 : 0, 0).sendToTarget();
    }

    public void stop(boolean z) {
        this.handler.obtainMessage(6, z ? 1 : 0, 0).sendToTarget();
    }
}
