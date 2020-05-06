package com.google.android.exoplayer2;

import android.util.Log;
import com.google.android.exoplayer2.source.ClippingMediaPeriod;
import com.google.android.exoplayer2.source.EmptySampleStream;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.SampleStream;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectorResult;
import com.google.android.exoplayer2.util.Assertions;

final class MediaPeriodHolder {
    private static final String TAG = "MediaPeriodHolder";
    public boolean hasEnabledTracks;
    public MediaPeriodInfo info;
    public final boolean[] mayRetainStreamFlags;
    public final MediaPeriod mediaPeriod;
    private final MediaSource mediaSource;
    public MediaPeriodHolder next;
    private TrackSelectorResult periodTrackSelectorResult;
    public boolean prepared;
    private final RendererCapabilities[] rendererCapabilities;
    public long rendererPositionOffsetUs;
    public final SampleStream[] sampleStreams;
    public TrackGroupArray trackGroups;
    private final TrackSelector trackSelector;
    public TrackSelectorResult trackSelectorResult;
    public final Object uid;

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v0, resolved type: com.google.android.exoplayer2.source.MediaPeriod} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v6, resolved type: com.google.android.exoplayer2.source.ClippingMediaPeriod} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v7, resolved type: com.google.android.exoplayer2.source.ClippingMediaPeriod} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v5, resolved type: com.google.android.exoplayer2.source.ClippingMediaPeriod} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public MediaPeriodHolder(com.google.android.exoplayer2.RendererCapabilities[] r3, long r4, com.google.android.exoplayer2.trackselection.TrackSelector r6, com.google.android.exoplayer2.upstream.Allocator r7, com.google.android.exoplayer2.source.MediaSource r8, java.lang.Object r9, com.google.android.exoplayer2.MediaPeriodInfo r10) {
        /*
            r2 = this;
            r2.<init>()
            r2.rendererCapabilities = r3
            long r0 = r10.startPositionUs
            long r4 = r4 - r0
            r2.rendererPositionOffsetUs = r4
            r2.trackSelector = r6
            r2.mediaSource = r8
            com.google.android.exoplayer2.util.Assertions.checkNotNull(r9)
            r2.uid = r9
            r2.info = r10
            int r4 = r3.length
            com.google.android.exoplayer2.source.SampleStream[] r4 = new com.google.android.exoplayer2.source.SampleStream[r4]
            r2.sampleStreams = r4
            int r3 = r3.length
            boolean[] r3 = new boolean[r3]
            r2.mayRetainStreamFlags = r3
            com.google.android.exoplayer2.source.MediaSource$MediaPeriodId r3 = r10.id
            com.google.android.exoplayer2.source.MediaPeriod r5 = r8.createPeriod(r3, r7)
            long r9 = r10.endPositionUs
            r3 = -9223372036854775808
            int r3 = (r9 > r3 ? 1 : (r9 == r3 ? 0 : -1))
            if (r3 == 0) goto L_0x0037
            com.google.android.exoplayer2.source.ClippingMediaPeriod r3 = new com.google.android.exoplayer2.source.ClippingMediaPeriod
            r6 = 1
            r7 = 0
            r4 = r3
            r4.<init>(r5, r6, r7, r9)
            goto L_0x0038
        L_0x0037:
            r3 = r5
        L_0x0038:
            r2.mediaPeriod = r3
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.MediaPeriodHolder.<init>(com.google.android.exoplayer2.RendererCapabilities[], long, com.google.android.exoplayer2.trackselection.TrackSelector, com.google.android.exoplayer2.upstream.Allocator, com.google.android.exoplayer2.source.MediaSource, java.lang.Object, com.google.android.exoplayer2.MediaPeriodInfo):void");
    }

    private void associateNoSampleRenderersWithEmptySampleStream(SampleStream[] sampleStreamArr) {
        int i = 0;
        while (true) {
            RendererCapabilities[] rendererCapabilitiesArr = this.rendererCapabilities;
            if (i < rendererCapabilitiesArr.length) {
                if (rendererCapabilitiesArr[i].getTrackType() == 5 && this.trackSelectorResult.isRendererEnabled(i)) {
                    sampleStreamArr[i] = new EmptySampleStream();
                }
                i++;
            } else {
                return;
            }
        }
    }

    private void disableTrackSelectionsInResult(TrackSelectorResult trackSelectorResult2) {
        for (int i = 0; i < trackSelectorResult2.length; i++) {
            boolean isRendererEnabled = trackSelectorResult2.isRendererEnabled(i);
            TrackSelection trackSelection = trackSelectorResult2.selections.get(i);
            if (isRendererEnabled && trackSelection != null) {
                trackSelection.disable();
            }
        }
    }

    private void disassociateNoSampleRenderersWithEmptySampleStream(SampleStream[] sampleStreamArr) {
        int i = 0;
        while (true) {
            RendererCapabilities[] rendererCapabilitiesArr = this.rendererCapabilities;
            if (i < rendererCapabilitiesArr.length) {
                if (rendererCapabilitiesArr[i].getTrackType() == 5) {
                    sampleStreamArr[i] = null;
                }
                i++;
            } else {
                return;
            }
        }
    }

    private void enableTrackSelectionsInResult(TrackSelectorResult trackSelectorResult2) {
        for (int i = 0; i < trackSelectorResult2.length; i++) {
            boolean isRendererEnabled = trackSelectorResult2.isRendererEnabled(i);
            TrackSelection trackSelection = trackSelectorResult2.selections.get(i);
            if (isRendererEnabled && trackSelection != null) {
                trackSelection.enable();
            }
        }
    }

    private void updatePeriodTrackSelectorResult(TrackSelectorResult trackSelectorResult2) {
        TrackSelectorResult trackSelectorResult3 = this.periodTrackSelectorResult;
        if (trackSelectorResult3 != null) {
            disableTrackSelectionsInResult(trackSelectorResult3);
        }
        this.periodTrackSelectorResult = trackSelectorResult2;
        TrackSelectorResult trackSelectorResult4 = this.periodTrackSelectorResult;
        if (trackSelectorResult4 != null) {
            enableTrackSelectionsInResult(trackSelectorResult4);
        }
    }

    public long applyTrackSelection(long j, boolean z) {
        return applyTrackSelection(j, z, new boolean[this.rendererCapabilities.length]);
    }

    public long applyTrackSelection(long j, boolean z, boolean[] zArr) {
        int i = 0;
        while (true) {
            TrackSelectorResult trackSelectorResult2 = this.trackSelectorResult;
            boolean z2 = true;
            if (i >= trackSelectorResult2.length) {
                break;
            }
            boolean[] zArr2 = this.mayRetainStreamFlags;
            if (z || !trackSelectorResult2.isEquivalent(this.periodTrackSelectorResult, i)) {
                z2 = false;
            }
            zArr2[i] = z2;
            i++;
        }
        disassociateNoSampleRenderersWithEmptySampleStream(this.sampleStreams);
        updatePeriodTrackSelectorResult(this.trackSelectorResult);
        TrackSelectionArray trackSelectionArray = this.trackSelectorResult.selections;
        long selectTracks = this.mediaPeriod.selectTracks(trackSelectionArray.getAll(), this.mayRetainStreamFlags, this.sampleStreams, zArr, j);
        associateNoSampleRenderersWithEmptySampleStream(this.sampleStreams);
        this.hasEnabledTracks = false;
        int i2 = 0;
        while (true) {
            SampleStream[] sampleStreamArr = this.sampleStreams;
            if (i2 >= sampleStreamArr.length) {
                return selectTracks;
            }
            if (sampleStreamArr[i2] != null) {
                Assertions.checkState(this.trackSelectorResult.isRendererEnabled(i2));
                if (this.rendererCapabilities[i2].getTrackType() != 5) {
                    this.hasEnabledTracks = true;
                }
            } else {
                Assertions.checkState(trackSelectionArray.get(i2) == null);
            }
            i2++;
        }
    }

    public void continueLoading(long j) {
        this.mediaPeriod.continueLoading(toPeriodTime(j));
    }

    public long getBufferedPositionUs(boolean z) {
        if (!this.prepared) {
            return this.info.startPositionUs;
        }
        long bufferedPositionUs = this.mediaPeriod.getBufferedPositionUs();
        return (bufferedPositionUs != Long.MIN_VALUE || !z) ? bufferedPositionUs : this.info.durationUs;
    }

    public long getDurationUs() {
        return this.info.durationUs;
    }

    public long getNextLoadPositionUs() {
        if (!this.prepared) {
            return 0;
        }
        return this.mediaPeriod.getNextLoadPositionUs();
    }

    public long getRendererOffset() {
        return this.rendererPositionOffsetUs;
    }

    public void handlePrepared(float f) {
        this.prepared = true;
        this.trackGroups = this.mediaPeriod.getTrackGroups();
        selectTracks(f);
        long applyTrackSelection = applyTrackSelection(this.info.startPositionUs, false);
        long j = this.rendererPositionOffsetUs;
        MediaPeriodInfo mediaPeriodInfo = this.info;
        this.rendererPositionOffsetUs = j + (mediaPeriodInfo.startPositionUs - applyTrackSelection);
        this.info = mediaPeriodInfo.copyWithStartPositionUs(applyTrackSelection);
    }

    public boolean isFullyBuffered() {
        return this.prepared && (!this.hasEnabledTracks || this.mediaPeriod.getBufferedPositionUs() == Long.MIN_VALUE);
    }

    public void reevaluateBuffer(long j) {
        if (this.prepared) {
            this.mediaPeriod.reevaluateBuffer(toPeriodTime(j));
        }
    }

    public void release() {
        MediaSource mediaSource2;
        MediaPeriod mediaPeriod2;
        updatePeriodTrackSelectorResult((TrackSelectorResult) null);
        try {
            if (this.info.endPositionUs != Long.MIN_VALUE) {
                mediaSource2 = this.mediaSource;
                mediaPeriod2 = ((ClippingMediaPeriod) this.mediaPeriod).mediaPeriod;
            } else {
                mediaSource2 = this.mediaSource;
                mediaPeriod2 = this.mediaPeriod;
            }
            mediaSource2.releasePeriod(mediaPeriod2);
        } catch (RuntimeException e) {
            Log.e(TAG, "Period release failed.", e);
        }
    }

    public boolean selectTracks(float f) {
        TrackSelectorResult selectTracks = this.trackSelector.selectTracks(this.rendererCapabilities, this.trackGroups);
        if (selectTracks.isEquivalent(this.periodTrackSelectorResult)) {
            return false;
        }
        this.trackSelectorResult = selectTracks;
        for (TrackSelection trackSelection : this.trackSelectorResult.selections.getAll()) {
            if (trackSelection != null) {
                trackSelection.onPlaybackSpeed(f);
            }
        }
        return true;
    }

    public long toPeriodTime(long j) {
        return j - getRendererOffset();
    }

    public long toRendererTime(long j) {
        return j + getRendererOffset();
    }
}
