package com.google.android.exoplayer2.source;

import android.support.annotation.Nullable;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.util.Assertions;
import com.miui.activityutil.h;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

public final class ClippingMediaSource extends CompositeMediaSource<Void> {
    private final boolean allowDynamicClippingUpdates;
    private IllegalClippingException clippingError;
    private ClippingTimeline clippingTimeline;
    private final boolean enableInitialDiscontinuity;
    private final long endUs;
    @Nullable
    private Object manifest;
    private final ArrayList<ClippingMediaPeriod> mediaPeriods;
    private final MediaSource mediaSource;
    private long periodEndUs;
    private long periodStartUs;
    private final boolean relativeToDefaultPosition;
    private final long startUs;
    private final Timeline.Window window;

    private static final class ClippingTimeline extends ForwardingTimeline {
        private final long durationUs;
        private final long endUs;
        private final boolean isDynamic;
        private final long startUs;

        /* JADX WARNING: Code restructure failed: missing block: B:31:0x006e, code lost:
            if (r13 == r10) goto L_0x0072;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public ClippingTimeline(com.google.android.exoplayer2.Timeline r10, long r11, long r13) {
            /*
                r9 = this;
                r9.<init>(r10)
                int r0 = r10.getPeriodCount()
                r1 = 1
                r2 = 0
                if (r0 != r1) goto L_0x0075
                com.google.android.exoplayer2.Timeline$Window r0 = new com.google.android.exoplayer2.Timeline$Window
                r0.<init>()
                com.google.android.exoplayer2.Timeline$Window r10 = r10.getWindow(r2, r0, r2)
                r3 = 0
                long r11 = java.lang.Math.max(r3, r11)
                r5 = -9223372036854775808
                int r0 = (r13 > r5 ? 1 : (r13 == r5 ? 0 : -1))
                if (r0 != 0) goto L_0x0023
                long r13 = r10.durationUs
                goto L_0x0027
            L_0x0023:
                long r13 = java.lang.Math.max(r3, r13)
            L_0x0027:
                long r5 = r10.durationUs
                r7 = -9223372036854775807(0x8000000000000001, double:-4.9E-324)
                int r0 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
                if (r0 == 0) goto L_0x0052
                int r0 = (r13 > r5 ? 1 : (r13 == r5 ? 0 : -1))
                if (r0 <= 0) goto L_0x0037
                r13 = r5
            L_0x0037:
                int r0 = (r11 > r3 ? 1 : (r11 == r3 ? 0 : -1))
                if (r0 == 0) goto L_0x0046
                boolean r0 = r10.isSeekable
                if (r0 == 0) goto L_0x0040
                goto L_0x0046
            L_0x0040:
                com.google.android.exoplayer2.source.ClippingMediaSource$IllegalClippingException r10 = new com.google.android.exoplayer2.source.ClippingMediaSource$IllegalClippingException
                r10.<init>(r1)
                throw r10
            L_0x0046:
                int r0 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
                if (r0 > 0) goto L_0x004b
                goto L_0x0052
            L_0x004b:
                com.google.android.exoplayer2.source.ClippingMediaSource$IllegalClippingException r10 = new com.google.android.exoplayer2.source.ClippingMediaSource$IllegalClippingException
                r11 = 2
                r10.<init>(r11)
                throw r10
            L_0x0052:
                r9.startUs = r11
                r9.endUs = r13
                int r0 = (r13 > r7 ? 1 : (r13 == r7 ? 0 : -1))
                if (r0 != 0) goto L_0x005c
                r11 = r7
                goto L_0x005e
            L_0x005c:
                long r11 = r13 - r11
            L_0x005e:
                r9.durationUs = r11
                boolean r11 = r10.isDynamic
                if (r11 == 0) goto L_0x0071
                if (r0 == 0) goto L_0x0072
                long r10 = r10.durationUs
                int r12 = (r10 > r7 ? 1 : (r10 == r7 ? 0 : -1))
                if (r12 == 0) goto L_0x0071
                int r10 = (r13 > r10 ? 1 : (r13 == r10 ? 0 : -1))
                if (r10 != 0) goto L_0x0071
                goto L_0x0072
            L_0x0071:
                r1 = r2
            L_0x0072:
                r9.isDynamic = r1
                return
            L_0x0075:
                com.google.android.exoplayer2.source.ClippingMediaSource$IllegalClippingException r10 = new com.google.android.exoplayer2.source.ClippingMediaSource$IllegalClippingException
                r10.<init>(r2)
                throw r10
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.ClippingMediaSource.ClippingTimeline.<init>(com.google.android.exoplayer2.Timeline, long, long):void");
        }

        public Timeline.Period getPeriod(int i, Timeline.Period period, boolean z) {
            this.timeline.getPeriod(0, period, z);
            long positionInWindowUs = period.getPositionInWindowUs() - this.startUs;
            long j = this.durationUs;
            return period.set(period.id, period.uid, 0, j == C.TIME_UNSET ? -9223372036854775807L : j - positionInWindowUs, positionInWindowUs);
        }

        public Timeline.Window getWindow(int i, Timeline.Window window, boolean z, long j) {
            this.timeline.getWindow(0, window, z, 0);
            long j2 = window.positionInFirstPeriodUs;
            long j3 = this.startUs;
            window.positionInFirstPeriodUs = j2 + j3;
            window.durationUs = this.durationUs;
            window.isDynamic = this.isDynamic;
            long j4 = window.defaultPositionUs;
            if (j4 != C.TIME_UNSET) {
                window.defaultPositionUs = Math.max(j4, j3);
                long j5 = this.endUs;
                window.defaultPositionUs = j5 == C.TIME_UNSET ? window.defaultPositionUs : Math.min(window.defaultPositionUs, j5);
                window.defaultPositionUs -= this.startUs;
            }
            long usToMs = C.usToMs(this.startUs);
            long j6 = window.presentationStartTimeMs;
            if (j6 != C.TIME_UNSET) {
                window.presentationStartTimeMs = j6 + usToMs;
            }
            long j7 = window.windowStartTimeMs;
            if (j7 != C.TIME_UNSET) {
                window.windowStartTimeMs = j7 + usToMs;
            }
            return window;
        }
    }

    public static final class IllegalClippingException extends IOException {
        public static final int REASON_INVALID_PERIOD_COUNT = 0;
        public static final int REASON_NOT_SEEKABLE_TO_START = 1;
        public static final int REASON_START_EXCEEDS_END = 2;
        public final int reason;

        @Retention(RetentionPolicy.SOURCE)
        public @interface Reason {
        }

        public IllegalClippingException(int i) {
            super("Illegal clipping: " + getReasonDescription(i));
            this.reason = i;
        }

        private static String getReasonDescription(int i) {
            return i != 0 ? i != 1 ? i != 2 ? h.f2289a : "start exceeds end" : "not seekable to start" : "invalid period count";
        }
    }

    public ClippingMediaSource(MediaSource mediaSource2, long j) {
        this(mediaSource2, 0, j, true, false, true);
    }

    public ClippingMediaSource(MediaSource mediaSource2, long j, long j2) {
        this(mediaSource2, j, j2, true, false, false);
    }

    @Deprecated
    public ClippingMediaSource(MediaSource mediaSource2, long j, long j2, boolean z) {
        this(mediaSource2, j, j2, z, false, false);
    }

    public ClippingMediaSource(MediaSource mediaSource2, long j, long j2, boolean z, boolean z2, boolean z3) {
        Assertions.checkArgument(j >= 0);
        Assertions.checkNotNull(mediaSource2);
        this.mediaSource = mediaSource2;
        this.startUs = j;
        this.endUs = j2;
        this.enableInitialDiscontinuity = z;
        this.allowDynamicClippingUpdates = z2;
        this.relativeToDefaultPosition = z3;
        this.mediaPeriods = new ArrayList<>();
        this.window = new Timeline.Window();
    }

    private void refreshClippedTimeline(Timeline timeline) {
        long j;
        timeline.getWindow(0, this.window);
        long positionInFirstPeriodUs = this.window.getPositionInFirstPeriodUs();
        long j2 = Long.MIN_VALUE;
        if (this.clippingTimeline == null || this.mediaPeriods.isEmpty() || this.allowDynamicClippingUpdates) {
            long j3 = this.startUs;
            long j4 = this.endUs;
            if (this.relativeToDefaultPosition) {
                long defaultPositionUs = this.window.getDefaultPositionUs();
                j3 += defaultPositionUs;
                j4 += defaultPositionUs;
            }
            this.periodStartUs = positionInFirstPeriodUs + j3;
            if (this.endUs != Long.MIN_VALUE) {
                j2 = positionInFirstPeriodUs + j4;
            }
            this.periodEndUs = j2;
            int size = this.mediaPeriods.size();
            for (int i = 0; i < size; i++) {
                this.mediaPeriods.get(i).updateClipping(this.periodStartUs, this.periodEndUs);
            }
            j = j3;
            j2 = j4;
        } else {
            long j5 = this.periodStartUs - positionInFirstPeriodUs;
            if (this.endUs != Long.MIN_VALUE) {
                j2 = this.periodEndUs - positionInFirstPeriodUs;
            }
            j = j5;
        }
        try {
            this.clippingTimeline = new ClippingTimeline(timeline, j, j2);
            refreshSourceInfo(this.clippingTimeline, this.manifest);
        } catch (IllegalClippingException e) {
            this.clippingError = e;
        }
    }

    public MediaPeriod createPeriod(MediaSource.MediaPeriodId mediaPeriodId, Allocator allocator) {
        ClippingMediaPeriod clippingMediaPeriod = new ClippingMediaPeriod(this.mediaSource.createPeriod(mediaPeriodId, allocator), this.enableInitialDiscontinuity, this.periodStartUs, this.periodEndUs);
        this.mediaPeriods.add(clippingMediaPeriod);
        return clippingMediaPeriod;
    }

    /* access modifiers changed from: protected */
    public long getMediaTimeForChildMediaTime(Void voidR, long j) {
        if (j == C.TIME_UNSET) {
            return C.TIME_UNSET;
        }
        long usToMs = C.usToMs(this.startUs);
        long max = Math.max(0, j - usToMs);
        long j2 = this.endUs;
        return j2 != Long.MIN_VALUE ? Math.min(C.usToMs(j2) - usToMs, max) : max;
    }

    public void maybeThrowSourceInfoRefreshError() {
        IllegalClippingException illegalClippingException = this.clippingError;
        if (illegalClippingException == null) {
            super.maybeThrowSourceInfoRefreshError();
            return;
        }
        throw illegalClippingException;
    }

    /* access modifiers changed from: protected */
    public void onChildSourceInfoRefreshed(Void voidR, MediaSource mediaSource2, Timeline timeline, @Nullable Object obj) {
        if (this.clippingError == null) {
            this.manifest = obj;
            refreshClippedTimeline(timeline);
        }
    }

    public void prepareSourceInternal(ExoPlayer exoPlayer, boolean z) {
        super.prepareSourceInternal(exoPlayer, z);
        prepareChildSource(null, this.mediaSource);
    }

    public void releasePeriod(MediaPeriod mediaPeriod) {
        Assertions.checkState(this.mediaPeriods.remove(mediaPeriod));
        this.mediaSource.releasePeriod(((ClippingMediaPeriod) mediaPeriod).mediaPeriod);
        if (this.mediaPeriods.isEmpty() && !this.allowDynamicClippingUpdates) {
            refreshClippedTimeline(this.clippingTimeline.timeline);
        }
    }

    public void releaseSourceInternal() {
        super.releaseSourceInternal();
        this.clippingError = null;
        this.clippingTimeline = null;
    }
}
