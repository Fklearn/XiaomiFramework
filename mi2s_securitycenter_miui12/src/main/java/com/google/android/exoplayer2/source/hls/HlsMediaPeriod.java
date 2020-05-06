package com.google.android.exoplayer2.source.hls;

import android.support.annotation.Nullable;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.source.CompositeSequenceableLoaderFactory;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.SampleStream;
import com.google.android.exoplayer2.source.SequenceableLoader;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsSampleStreamWrapper;
import com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistTracker;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;

public final class HlsMediaPeriod implements MediaPeriod, HlsSampleStreamWrapper.Callback, HlsPlaylistTracker.PlaylistEventListener {
    private final Allocator allocator;
    private final boolean allowChunklessPreparation;
    @Nullable
    private MediaPeriod.Callback callback;
    private SequenceableLoader compositeSequenceableLoader;
    private final CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
    private final HlsDataSourceFactory dataSourceFactory;
    private HlsSampleStreamWrapper[] enabledSampleStreamWrappers = new HlsSampleStreamWrapper[0];
    private final MediaSourceEventListener.EventDispatcher eventDispatcher;
    private final HlsExtractorFactory extractorFactory;
    private final int minLoadableRetryCount;
    private boolean notifiedReadingStarted;
    private int pendingPrepareCount;
    private final HlsPlaylistTracker playlistTracker;
    private HlsSampleStreamWrapper[] sampleStreamWrappers = new HlsSampleStreamWrapper[0];
    private final IdentityHashMap<SampleStream, Integer> streamWrapperIndices = new IdentityHashMap<>();
    private final TimestampAdjusterProvider timestampAdjusterProvider = new TimestampAdjusterProvider();
    private TrackGroupArray trackGroups;

    public HlsMediaPeriod(HlsExtractorFactory hlsExtractorFactory, HlsPlaylistTracker hlsPlaylistTracker, HlsDataSourceFactory hlsDataSourceFactory, int i, MediaSourceEventListener.EventDispatcher eventDispatcher2, Allocator allocator2, CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory2, boolean z) {
        this.extractorFactory = hlsExtractorFactory;
        this.playlistTracker = hlsPlaylistTracker;
        this.dataSourceFactory = hlsDataSourceFactory;
        this.minLoadableRetryCount = i;
        this.eventDispatcher = eventDispatcher2;
        this.allocator = allocator2;
        this.compositeSequenceableLoaderFactory = compositeSequenceableLoaderFactory2;
        this.allowChunklessPreparation = z;
        this.compositeSequenceableLoader = compositeSequenceableLoaderFactory2.createCompositeSequenceableLoader(new SequenceableLoader[0]);
        eventDispatcher2.mediaPeriodCreated();
    }

    private void buildAndPrepareMainSampleStreamWrapper(HlsMasterPlaylist hlsMasterPlaylist, long j) {
        ArrayList arrayList;
        HlsMasterPlaylist hlsMasterPlaylist2 = hlsMasterPlaylist;
        ArrayList arrayList2 = new ArrayList(hlsMasterPlaylist2.variants);
        ArrayList arrayList3 = new ArrayList();
        ArrayList arrayList4 = new ArrayList();
        for (int i = 0; i < arrayList2.size(); i++) {
            HlsMasterPlaylist.HlsUrl hlsUrl = (HlsMasterPlaylist.HlsUrl) arrayList2.get(i);
            Format format = hlsUrl.format;
            if (format.height > 0 || Util.getCodecsOfType(format.codecs, 2) != null) {
                arrayList3.add(hlsUrl);
            } else if (Util.getCodecsOfType(format.codecs, 1) != null) {
                arrayList4.add(hlsUrl);
            }
        }
        if (!arrayList3.isEmpty()) {
            arrayList = arrayList3;
        } else {
            if (arrayList4.size() < arrayList2.size()) {
                arrayList2.removeAll(arrayList4);
            }
            arrayList = arrayList2;
        }
        Assertions.checkArgument(!arrayList.isEmpty());
        HlsMasterPlaylist.HlsUrl[] hlsUrlArr = (HlsMasterPlaylist.HlsUrl[]) arrayList.toArray(new HlsMasterPlaylist.HlsUrl[0]);
        String str = hlsUrlArr[0].format.codecs;
        HlsSampleStreamWrapper buildSampleStreamWrapper = buildSampleStreamWrapper(0, hlsUrlArr, hlsMasterPlaylist2.muxedAudioFormat, hlsMasterPlaylist2.muxedCaptionFormats, j);
        this.sampleStreamWrappers[0] = buildSampleStreamWrapper;
        if (!this.allowChunklessPreparation || str == null) {
            buildSampleStreamWrapper.setIsTimestampMaster(true);
            buildSampleStreamWrapper.continuePreparing();
            return;
        }
        boolean z = Util.getCodecsOfType(str, 2) != null;
        boolean z2 = Util.getCodecsOfType(str, 1) != null;
        ArrayList arrayList5 = new ArrayList();
        if (z) {
            Format[] formatArr = new Format[arrayList.size()];
            for (int i2 = 0; i2 < formatArr.length; i2++) {
                formatArr[i2] = deriveVideoFormat(hlsUrlArr[i2].format);
            }
            arrayList5.add(new TrackGroup(formatArr));
            if (z2 && (hlsMasterPlaylist2.muxedAudioFormat != null || hlsMasterPlaylist2.audios.isEmpty())) {
                arrayList5.add(new TrackGroup(deriveMuxedAudioFormat(hlsUrlArr[0].format, hlsMasterPlaylist2.muxedAudioFormat, -1)));
            }
            List<Format> list = hlsMasterPlaylist2.muxedCaptionFormats;
            if (list != null) {
                for (int i3 = 0; i3 < list.size(); i3++) {
                    arrayList5.add(new TrackGroup(list.get(i3)));
                }
            }
        } else if (z2) {
            Format[] formatArr2 = new Format[arrayList.size()];
            for (int i4 = 0; i4 < formatArr2.length; i4++) {
                Format format2 = hlsUrlArr[i4].format;
                formatArr2[i4] = deriveMuxedAudioFormat(format2, hlsMasterPlaylist2.muxedAudioFormat, format2.bitrate);
            }
            arrayList5.add(new TrackGroup(formatArr2));
        } else {
            throw new IllegalArgumentException("Unexpected codecs attribute: " + str);
        }
        TrackGroup trackGroup = new TrackGroup(Format.createSampleFormat("ID3", MimeTypes.APPLICATION_ID3, (String) null, -1, (DrmInitData) null));
        arrayList5.add(trackGroup);
        buildSampleStreamWrapper.prepareWithMasterPlaylistInfo(new TrackGroupArray((TrackGroup[]) arrayList5.toArray(new TrackGroup[0])), 0, new TrackGroupArray(trackGroup));
    }

    private void buildAndPrepareSampleStreamWrappers(long j) {
        HlsMasterPlaylist masterPlaylist = this.playlistTracker.getMasterPlaylist();
        List<HlsMasterPlaylist.HlsUrl> list = masterPlaylist.audios;
        List<HlsMasterPlaylist.HlsUrl> list2 = masterPlaylist.subtitles;
        int size = list.size() + 1 + list2.size();
        this.sampleStreamWrappers = new HlsSampleStreamWrapper[size];
        this.pendingPrepareCount = size;
        buildAndPrepareMainSampleStreamWrapper(masterPlaylist, j);
        char c2 = 0;
        int i = 1;
        int i2 = 0;
        while (i2 < list.size()) {
            HlsMasterPlaylist.HlsUrl hlsUrl = list.get(i2);
            HlsMasterPlaylist.HlsUrl[] hlsUrlArr = new HlsMasterPlaylist.HlsUrl[1];
            hlsUrlArr[c2] = hlsUrl;
            HlsSampleStreamWrapper buildSampleStreamWrapper = buildSampleStreamWrapper(1, hlsUrlArr, (Format) null, Collections.emptyList(), j);
            int i3 = i + 1;
            this.sampleStreamWrappers[i] = buildSampleStreamWrapper;
            Format format = hlsUrl.format;
            if (!this.allowChunklessPreparation || format.codecs == null) {
                buildSampleStreamWrapper.continuePreparing();
            } else {
                buildSampleStreamWrapper.prepareWithMasterPlaylistInfo(new TrackGroupArray(new TrackGroup(format)), 0, TrackGroupArray.EMPTY);
            }
            i2++;
            i = i3;
            c2 = 0;
        }
        int i4 = 0;
        while (i4 < list2.size()) {
            HlsMasterPlaylist.HlsUrl hlsUrl2 = list2.get(i4);
            HlsSampleStreamWrapper buildSampleStreamWrapper2 = buildSampleStreamWrapper(3, new HlsMasterPlaylist.HlsUrl[]{hlsUrl2}, (Format) null, Collections.emptyList(), j);
            this.sampleStreamWrappers[i] = buildSampleStreamWrapper2;
            buildSampleStreamWrapper2.prepareWithMasterPlaylistInfo(new TrackGroupArray(new TrackGroup(hlsUrl2.format)), 0, TrackGroupArray.EMPTY);
            i4++;
            i++;
        }
        this.enabledSampleStreamWrappers = this.sampleStreamWrappers;
    }

    private HlsSampleStreamWrapper buildSampleStreamWrapper(int i, HlsMasterPlaylist.HlsUrl[] hlsUrlArr, Format format, List<Format> list, long j) {
        return new HlsSampleStreamWrapper(i, this, new HlsChunkSource(this.extractorFactory, this.playlistTracker, hlsUrlArr, this.dataSourceFactory, this.timestampAdjusterProvider, list), this.allocator, j, format, this.minLoadableRetryCount, this.eventDispatcher);
    }

    private static Format deriveMuxedAudioFormat(Format format, Format format2, int i) {
        int i2;
        int i3;
        String str;
        String str2;
        Format format3 = format;
        Format format4 = format2;
        if (format4 != null) {
            str = format4.codecs;
            i3 = format4.channelCount;
            i2 = format4.selectionFlags;
            str2 = format4.language;
        } else {
            str = Util.getCodecsOfType(format3.codecs, 1);
            i3 = -1;
            i2 = 0;
            str2 = null;
        }
        String str3 = str;
        int i4 = i3;
        int i5 = i2;
        String mediaMimeType = MimeTypes.getMediaMimeType(str3);
        return Format.createAudioSampleFormat(format3.id, mediaMimeType, str3, i, -1, i4, -1, (List<byte[]>) null, (DrmInitData) null, i5, str2);
    }

    private static Format deriveVideoFormat(Format format) {
        String codecsOfType = Util.getCodecsOfType(format.codecs, 2);
        return Format.createVideoSampleFormat(format.id, MimeTypes.getMediaMimeType(codecsOfType), codecsOfType, format.bitrate, -1, format.width, format.height, format.frameRate, (List<byte[]>) null, (DrmInitData) null);
    }

    public boolean continueLoading(long j) {
        if (this.trackGroups != null) {
            return this.compositeSequenceableLoader.continueLoading(j);
        }
        for (HlsSampleStreamWrapper continuePreparing : this.sampleStreamWrappers) {
            continuePreparing.continuePreparing();
        }
        return false;
    }

    public void discardBuffer(long j, boolean z) {
        for (HlsSampleStreamWrapper discardBuffer : this.enabledSampleStreamWrappers) {
            discardBuffer.discardBuffer(j, z);
        }
    }

    public long getAdjustedSeekPositionUs(long j, SeekParameters seekParameters) {
        return j;
    }

    public long getBufferedPositionUs() {
        return this.compositeSequenceableLoader.getBufferedPositionUs();
    }

    public long getNextLoadPositionUs() {
        return this.compositeSequenceableLoader.getNextLoadPositionUs();
    }

    public TrackGroupArray getTrackGroups() {
        return this.trackGroups;
    }

    public void maybeThrowPrepareError() {
        for (HlsSampleStreamWrapper maybeThrowPrepareError : this.sampleStreamWrappers) {
            maybeThrowPrepareError.maybeThrowPrepareError();
        }
    }

    public void onContinueLoadingRequested(HlsSampleStreamWrapper hlsSampleStreamWrapper) {
        this.callback.onContinueLoadingRequested(this);
    }

    public void onPlaylistChanged() {
        this.callback.onContinueLoadingRequested(this);
    }

    public boolean onPlaylistError(HlsMasterPlaylist.HlsUrl hlsUrl, boolean z) {
        boolean z2 = true;
        for (HlsSampleStreamWrapper onPlaylistError : this.sampleStreamWrappers) {
            z2 &= onPlaylistError.onPlaylistError(hlsUrl, z);
        }
        this.callback.onContinueLoadingRequested(this);
        return z2;
    }

    public void onPlaylistRefreshRequired(HlsMasterPlaylist.HlsUrl hlsUrl) {
        this.playlistTracker.refreshPlaylist(hlsUrl);
    }

    public void onPrepared() {
        int i = this.pendingPrepareCount - 1;
        this.pendingPrepareCount = i;
        if (i <= 0) {
            int i2 = 0;
            for (HlsSampleStreamWrapper trackGroups2 : this.sampleStreamWrappers) {
                i2 += trackGroups2.getTrackGroups().length;
            }
            TrackGroup[] trackGroupArr = new TrackGroup[i2];
            HlsSampleStreamWrapper[] hlsSampleStreamWrapperArr = this.sampleStreamWrappers;
            int length = hlsSampleStreamWrapperArr.length;
            int i3 = 0;
            int i4 = 0;
            while (i3 < length) {
                HlsSampleStreamWrapper hlsSampleStreamWrapper = hlsSampleStreamWrapperArr[i3];
                int i5 = hlsSampleStreamWrapper.getTrackGroups().length;
                int i6 = i4;
                int i7 = 0;
                while (i7 < i5) {
                    trackGroupArr[i6] = hlsSampleStreamWrapper.getTrackGroups().get(i7);
                    i7++;
                    i6++;
                }
                i3++;
                i4 = i6;
            }
            this.trackGroups = new TrackGroupArray(trackGroupArr);
            this.callback.onPrepared(this);
        }
    }

    public void prepare(MediaPeriod.Callback callback2, long j) {
        this.callback = callback2;
        this.playlistTracker.addListener(this);
        buildAndPrepareSampleStreamWrappers(j);
    }

    public long readDiscontinuity() {
        if (this.notifiedReadingStarted) {
            return C.TIME_UNSET;
        }
        this.eventDispatcher.readingStarted();
        this.notifiedReadingStarted = true;
        return C.TIME_UNSET;
    }

    public void reevaluateBuffer(long j) {
        this.compositeSequenceableLoader.reevaluateBuffer(j);
    }

    public void release() {
        this.playlistTracker.removeListener(this);
        for (HlsSampleStreamWrapper release : this.sampleStreamWrappers) {
            release.release();
        }
        this.callback = null;
        this.eventDispatcher.mediaPeriodReleased();
    }

    public long seekToUs(long j) {
        HlsSampleStreamWrapper[] hlsSampleStreamWrapperArr = this.enabledSampleStreamWrappers;
        if (hlsSampleStreamWrapperArr.length > 0) {
            boolean seekToUs = hlsSampleStreamWrapperArr[0].seekToUs(j, false);
            int i = 1;
            while (true) {
                HlsSampleStreamWrapper[] hlsSampleStreamWrapperArr2 = this.enabledSampleStreamWrappers;
                if (i >= hlsSampleStreamWrapperArr2.length) {
                    break;
                }
                hlsSampleStreamWrapperArr2[i].seekToUs(j, seekToUs);
                i++;
            }
            if (seekToUs) {
                this.timestampAdjusterProvider.reset();
            }
        }
        return j;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:56:0x00ea, code lost:
        if (r5 != r8[0]) goto L_0x00ee;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public long selectTracks(com.google.android.exoplayer2.trackselection.TrackSelection[] r21, boolean[] r22, com.google.android.exoplayer2.source.SampleStream[] r23, boolean[] r24, long r25) {
        /*
            r20 = this;
            r0 = r20
            r1 = r21
            r2 = r23
            int r3 = r1.length
            int[] r3 = new int[r3]
            int r4 = r1.length
            int[] r4 = new int[r4]
            r6 = 0
        L_0x000d:
            int r7 = r1.length
            if (r6 >= r7) goto L_0x004e
            r7 = r2[r6]
            r8 = -1
            if (r7 != 0) goto L_0x0017
            r7 = r8
            goto L_0x0025
        L_0x0017:
            java.util.IdentityHashMap<com.google.android.exoplayer2.source.SampleStream, java.lang.Integer> r7 = r0.streamWrapperIndices
            r9 = r2[r6]
            java.lang.Object r7 = r7.get(r9)
            java.lang.Integer r7 = (java.lang.Integer) r7
            int r7 = r7.intValue()
        L_0x0025:
            r3[r6] = r7
            r4[r6] = r8
            r7 = r1[r6]
            if (r7 == 0) goto L_0x004b
            r7 = r1[r6]
            com.google.android.exoplayer2.source.TrackGroup r7 = r7.getTrackGroup()
            r9 = 0
        L_0x0034:
            com.google.android.exoplayer2.source.hls.HlsSampleStreamWrapper[] r10 = r0.sampleStreamWrappers
            int r11 = r10.length
            if (r9 >= r11) goto L_0x004b
            r10 = r10[r9]
            com.google.android.exoplayer2.source.TrackGroupArray r10 = r10.getTrackGroups()
            int r10 = r10.indexOf(r7)
            if (r10 == r8) goto L_0x0048
            r4[r6] = r9
            goto L_0x004b
        L_0x0048:
            int r9 = r9 + 1
            goto L_0x0034
        L_0x004b:
            int r6 = r6 + 1
            goto L_0x000d
        L_0x004e:
            java.util.IdentityHashMap<com.google.android.exoplayer2.source.SampleStream, java.lang.Integer> r6 = r0.streamWrapperIndices
            r6.clear()
            int r6 = r1.length
            com.google.android.exoplayer2.source.SampleStream[] r6 = new com.google.android.exoplayer2.source.SampleStream[r6]
            int r7 = r1.length
            com.google.android.exoplayer2.source.SampleStream[] r7 = new com.google.android.exoplayer2.source.SampleStream[r7]
            int r8 = r1.length
            com.google.android.exoplayer2.trackselection.TrackSelection[] r15 = new com.google.android.exoplayer2.trackselection.TrackSelection[r8]
            com.google.android.exoplayer2.source.hls.HlsSampleStreamWrapper[] r8 = r0.sampleStreamWrappers
            int r8 = r8.length
            com.google.android.exoplayer2.source.hls.HlsSampleStreamWrapper[] r13 = new com.google.android.exoplayer2.source.hls.HlsSampleStreamWrapper[r8]
            r12 = 0
            r14 = 0
            r16 = 0
        L_0x0065:
            com.google.android.exoplayer2.source.hls.HlsSampleStreamWrapper[] r8 = r0.sampleStreamWrappers
            int r8 = r8.length
            if (r14 >= r8) goto L_0x0109
            r8 = 0
        L_0x006b:
            int r9 = r1.length
            if (r8 >= r9) goto L_0x0084
            r9 = r3[r8]
            r10 = 0
            if (r9 != r14) goto L_0x0076
            r9 = r2[r8]
            goto L_0x0077
        L_0x0076:
            r9 = r10
        L_0x0077:
            r7[r8] = r9
            r9 = r4[r8]
            if (r9 != r14) goto L_0x007f
            r10 = r1[r8]
        L_0x007f:
            r15[r8] = r10
            int r8 = r8 + 1
            goto L_0x006b
        L_0x0084:
            com.google.android.exoplayer2.source.hls.HlsSampleStreamWrapper[] r8 = r0.sampleStreamWrappers
            r11 = r8[r14]
            r8 = r11
            r9 = r15
            r10 = r22
            r5 = r11
            r11 = r7
            r2 = r12
            r12 = r24
            r17 = r2
            r18 = r13
            r2 = r14
            r13 = r25
            r19 = r15
            r15 = r16
            boolean r8 = r8.selectTracks(r9, r10, r11, r12, r13, r15)
            r9 = 0
            r10 = 0
        L_0x00a2:
            int r11 = r1.length
            r12 = 1
            if (r9 >= r11) goto L_0x00d5
            r11 = r4[r9]
            if (r11 != r2) goto L_0x00c5
            r10 = r7[r9]
            if (r10 == 0) goto L_0x00b0
            r10 = r12
            goto L_0x00b1
        L_0x00b0:
            r10 = 0
        L_0x00b1:
            com.google.android.exoplayer2.util.Assertions.checkState(r10)
            r10 = r7[r9]
            r6[r9] = r10
            java.util.IdentityHashMap<com.google.android.exoplayer2.source.SampleStream, java.lang.Integer> r10 = r0.streamWrapperIndices
            r11 = r7[r9]
            java.lang.Integer r13 = java.lang.Integer.valueOf(r2)
            r10.put(r11, r13)
            r10 = r12
            goto L_0x00d2
        L_0x00c5:
            r11 = r3[r9]
            if (r11 != r2) goto L_0x00d2
            r11 = r7[r9]
            if (r11 != 0) goto L_0x00ce
            goto L_0x00cf
        L_0x00ce:
            r12 = 0
        L_0x00cf:
            com.google.android.exoplayer2.util.Assertions.checkState(r12)
        L_0x00d2:
            int r9 = r9 + 1
            goto L_0x00a2
        L_0x00d5:
            if (r10 == 0) goto L_0x00fc
            r18[r17] = r5
            int r9 = r17 + 1
            if (r17 != 0) goto L_0x00f6
            r5.setIsTimestampMaster(r12)
            if (r8 != 0) goto L_0x00ed
            com.google.android.exoplayer2.source.hls.HlsSampleStreamWrapper[] r8 = r0.enabledSampleStreamWrappers
            int r10 = r8.length
            if (r10 == 0) goto L_0x00ed
            r10 = 0
            r8 = r8[r10]
            if (r5 == r8) goto L_0x00fa
            goto L_0x00ee
        L_0x00ed:
            r10 = 0
        L_0x00ee:
            com.google.android.exoplayer2.source.hls.TimestampAdjusterProvider r5 = r0.timestampAdjusterProvider
            r5.reset()
            r16 = r12
            goto L_0x00fa
        L_0x00f6:
            r10 = 0
            r5.setIsTimestampMaster(r10)
        L_0x00fa:
            r12 = r9
            goto L_0x00ff
        L_0x00fc:
            r10 = 0
            r12 = r17
        L_0x00ff:
            int r14 = r2 + 1
            r2 = r23
            r13 = r18
            r15 = r19
            goto L_0x0065
        L_0x0109:
            r17 = r12
            r18 = r13
            r10 = 0
            int r1 = r6.length
            r2 = r23
            java.lang.System.arraycopy(r6, r10, r2, r10, r1)
            r1 = r18
            java.lang.Object[] r1 = java.util.Arrays.copyOf(r1, r12)
            com.google.android.exoplayer2.source.hls.HlsSampleStreamWrapper[] r1 = (com.google.android.exoplayer2.source.hls.HlsSampleStreamWrapper[]) r1
            r0.enabledSampleStreamWrappers = r1
            com.google.android.exoplayer2.source.CompositeSequenceableLoaderFactory r1 = r0.compositeSequenceableLoaderFactory
            com.google.android.exoplayer2.source.hls.HlsSampleStreamWrapper[] r2 = r0.enabledSampleStreamWrappers
            com.google.android.exoplayer2.source.SequenceableLoader r1 = r1.createCompositeSequenceableLoader(r2)
            r0.compositeSequenceableLoader = r1
            return r25
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.hls.HlsMediaPeriod.selectTracks(com.google.android.exoplayer2.trackselection.TrackSelection[], boolean[], com.google.android.exoplayer2.source.SampleStream[], boolean[], long):long");
    }
}
