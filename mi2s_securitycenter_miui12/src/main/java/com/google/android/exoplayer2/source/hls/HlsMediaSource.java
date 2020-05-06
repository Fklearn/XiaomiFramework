package com.google.android.exoplayer2.source.hls;

import android.net.Uri;
import android.os.Handler;
import android.support.annotation.Nullable;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerLibraryInfo;
import com.google.android.exoplayer2.source.BaseMediaSource;
import com.google.android.exoplayer2.source.CompositeSequenceableLoaderFactory;
import com.google.android.exoplayer2.source.DefaultCompositeSequenceableLoaderFactory;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.SinglePeriodTimeline;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.source.hls.playlist.DefaultHlsPlaylistTracker;
import com.google.android.exoplayer2.source.hls.playlist.HlsMediaPlaylist;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylist;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistParser;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistTracker;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.ParsingLoadable;
import com.google.android.exoplayer2.util.Assertions;
import java.util.List;

public final class HlsMediaSource extends BaseMediaSource implements HlsPlaylistTracker.PrimaryPlaylistListener {
    public static final int DEFAULT_MIN_LOADABLE_RETRY_COUNT = 3;
    private final boolean allowChunklessPreparation;
    private final CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
    private final HlsDataSourceFactory dataSourceFactory;
    private final HlsExtractorFactory extractorFactory;
    private final Uri manifestUri;
    private final int minLoadableRetryCount;
    private final HlsPlaylistTracker playlistTracker;
    @Nullable
    private final Object tag;

    public static final class Factory implements AdsMediaSource.MediaSourceFactory {
        private boolean allowChunklessPreparation;
        private CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
        private HlsExtractorFactory extractorFactory;
        private final HlsDataSourceFactory hlsDataSourceFactory;
        private boolean isCreateCalled;
        private int minLoadableRetryCount;
        @Nullable
        private ParsingLoadable.Parser<HlsPlaylist> playlistParser;
        @Nullable
        private HlsPlaylistTracker playlistTracker;
        @Nullable
        private Object tag;

        public Factory(HlsDataSourceFactory hlsDataSourceFactory2) {
            Assertions.checkNotNull(hlsDataSourceFactory2);
            this.hlsDataSourceFactory = hlsDataSourceFactory2;
            this.extractorFactory = HlsExtractorFactory.DEFAULT;
            this.minLoadableRetryCount = 3;
            this.compositeSequenceableLoaderFactory = new DefaultCompositeSequenceableLoaderFactory();
        }

        public Factory(DataSource.Factory factory) {
            this((HlsDataSourceFactory) new DefaultHlsDataSourceFactory(factory));
        }

        public HlsMediaSource createMediaSource(Uri uri) {
            this.isCreateCalled = true;
            if (this.playlistTracker == null) {
                HlsDataSourceFactory hlsDataSourceFactory2 = this.hlsDataSourceFactory;
                int i = this.minLoadableRetryCount;
                ParsingLoadable.Parser parser = this.playlistParser;
                if (parser == null) {
                    parser = new HlsPlaylistParser();
                }
                this.playlistTracker = new DefaultHlsPlaylistTracker(hlsDataSourceFactory2, i, parser);
            }
            return new HlsMediaSource(uri, this.hlsDataSourceFactory, this.extractorFactory, this.compositeSequenceableLoaderFactory, this.minLoadableRetryCount, this.playlistTracker, this.allowChunklessPreparation, this.tag);
        }

        @Deprecated
        public HlsMediaSource createMediaSource(Uri uri, @Nullable Handler handler, @Nullable MediaSourceEventListener mediaSourceEventListener) {
            HlsMediaSource createMediaSource = createMediaSource(uri);
            if (!(handler == null || mediaSourceEventListener == null)) {
                createMediaSource.addEventListener(handler, mediaSourceEventListener);
            }
            return createMediaSource;
        }

        public int[] getSupportedTypes() {
            return new int[]{2};
        }

        public Factory setAllowChunklessPreparation(boolean z) {
            Assertions.checkState(!this.isCreateCalled);
            this.allowChunklessPreparation = z;
            return this;
        }

        public Factory setCompositeSequenceableLoaderFactory(CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory2) {
            Assertions.checkState(!this.isCreateCalled);
            Assertions.checkNotNull(compositeSequenceableLoaderFactory2);
            this.compositeSequenceableLoaderFactory = compositeSequenceableLoaderFactory2;
            return this;
        }

        public Factory setExtractorFactory(HlsExtractorFactory hlsExtractorFactory) {
            Assertions.checkState(!this.isCreateCalled);
            Assertions.checkNotNull(hlsExtractorFactory);
            this.extractorFactory = hlsExtractorFactory;
            return this;
        }

        public Factory setMinLoadableRetryCount(int i) {
            Assertions.checkState(!this.isCreateCalled);
            this.minLoadableRetryCount = i;
            return this;
        }

        public Factory setPlaylistParser(ParsingLoadable.Parser<HlsPlaylist> parser) {
            boolean z = true;
            Assertions.checkState(!this.isCreateCalled);
            if (this.playlistTracker != null) {
                z = false;
            }
            Assertions.checkState(z, "A playlist tracker has already been set.");
            Assertions.checkNotNull(parser);
            this.playlistParser = parser;
            return this;
        }

        public Factory setPlaylistTracker(HlsPlaylistTracker hlsPlaylistTracker) {
            boolean z = true;
            Assertions.checkState(!this.isCreateCalled);
            if (this.playlistParser != null) {
                z = false;
            }
            Assertions.checkState(z, "A playlist parser has already been set.");
            Assertions.checkNotNull(hlsPlaylistTracker);
            this.playlistTracker = hlsPlaylistTracker;
            return this;
        }

        public Factory setTag(Object obj) {
            Assertions.checkState(!this.isCreateCalled);
            this.tag = obj;
            return this;
        }
    }

    static {
        ExoPlayerLibraryInfo.registerModule("goog.exo.hls");
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    @java.lang.Deprecated
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public HlsMediaSource(android.net.Uri r12, com.google.android.exoplayer2.source.hls.HlsDataSourceFactory r13, com.google.android.exoplayer2.source.hls.HlsExtractorFactory r14, int r15, android.os.Handler r16, com.google.android.exoplayer2.source.MediaSourceEventListener r17, com.google.android.exoplayer2.upstream.ParsingLoadable.Parser<com.google.android.exoplayer2.source.hls.playlist.HlsPlaylist> r18) {
        /*
            r11 = this;
            r0 = r16
            r1 = r17
            com.google.android.exoplayer2.source.DefaultCompositeSequenceableLoaderFactory r6 = new com.google.android.exoplayer2.source.DefaultCompositeSequenceableLoaderFactory
            r6.<init>()
            com.google.android.exoplayer2.source.hls.playlist.DefaultHlsPlaylistTracker r8 = new com.google.android.exoplayer2.source.hls.playlist.DefaultHlsPlaylistTracker
            com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistParser r2 = new com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistParser
            r2.<init>()
            r4 = r13
            r7 = r15
            r8.<init>(r13, r15, r2)
            r9 = 0
            r10 = 0
            r2 = r11
            r3 = r12
            r5 = r14
            r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10)
            if (r0 == 0) goto L_0x0026
            if (r1 == 0) goto L_0x0026
            r2 = r11
            r11.addEventListener(r0, r1)
            goto L_0x0027
        L_0x0026:
            r2 = r11
        L_0x0027:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.hls.HlsMediaSource.<init>(android.net.Uri, com.google.android.exoplayer2.source.hls.HlsDataSourceFactory, com.google.android.exoplayer2.source.hls.HlsExtractorFactory, int, android.os.Handler, com.google.android.exoplayer2.source.MediaSourceEventListener, com.google.android.exoplayer2.upstream.ParsingLoadable$Parser):void");
    }

    private HlsMediaSource(Uri uri, HlsDataSourceFactory hlsDataSourceFactory, HlsExtractorFactory hlsExtractorFactory, CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory2, int i, HlsPlaylistTracker hlsPlaylistTracker, boolean z, @Nullable Object obj) {
        this.manifestUri = uri;
        this.dataSourceFactory = hlsDataSourceFactory;
        this.extractorFactory = hlsExtractorFactory;
        this.compositeSequenceableLoaderFactory = compositeSequenceableLoaderFactory2;
        this.minLoadableRetryCount = i;
        this.playlistTracker = hlsPlaylistTracker;
        this.allowChunklessPreparation = z;
        this.tag = obj;
    }

    @Deprecated
    public HlsMediaSource(Uri uri, DataSource.Factory factory, int i, Handler handler, MediaSourceEventListener mediaSourceEventListener) {
        this(uri, new DefaultHlsDataSourceFactory(factory), HlsExtractorFactory.DEFAULT, i, handler, mediaSourceEventListener, new HlsPlaylistParser());
    }

    @Deprecated
    public HlsMediaSource(Uri uri, DataSource.Factory factory, Handler handler, MediaSourceEventListener mediaSourceEventListener) {
        this(uri, factory, 3, handler, mediaSourceEventListener);
    }

    public MediaPeriod createPeriod(MediaSource.MediaPeriodId mediaPeriodId, Allocator allocator) {
        Assertions.checkArgument(mediaPeriodId.periodIndex == 0);
        return new HlsMediaPeriod(this.extractorFactory, this.playlistTracker, this.dataSourceFactory, this.minLoadableRetryCount, createEventDispatcher(mediaPeriodId), allocator, this.compositeSequenceableLoaderFactory, this.allowChunklessPreparation);
    }

    public void maybeThrowSourceInfoRefreshError() {
        this.playlistTracker.maybeThrowPrimaryPlaylistRefreshError();
    }

    public void onPrimaryPlaylistRefreshed(HlsMediaPlaylist hlsMediaPlaylist) {
        SinglePeriodTimeline singlePeriodTimeline;
        long j;
        HlsMediaPlaylist hlsMediaPlaylist2 = hlsMediaPlaylist;
        long usToMs = hlsMediaPlaylist2.hasProgramDateTime ? C.usToMs(hlsMediaPlaylist2.startTimeUs) : -9223372036854775807L;
        int i = hlsMediaPlaylist2.playlistType;
        long j2 = (i == 2 || i == 1) ? usToMs : -9223372036854775807L;
        long j3 = hlsMediaPlaylist2.startOffsetUs;
        if (this.playlistTracker.isLive()) {
            long initialStartTimeUs = hlsMediaPlaylist2.startTimeUs - this.playlistTracker.getInitialStartTimeUs();
            long j4 = hlsMediaPlaylist2.hasEndTag ? initialStartTimeUs + hlsMediaPlaylist2.durationUs : -9223372036854775807L;
            List<HlsMediaPlaylist.Segment> list = hlsMediaPlaylist2.segments;
            if (j3 == C.TIME_UNSET) {
                j = list.isEmpty() ? 0 : list.get(Math.max(0, list.size() - 3)).relativeStartTimeUs;
            } else {
                j = j3;
            }
            singlePeriodTimeline = new SinglePeriodTimeline(j2, usToMs, j4, hlsMediaPlaylist2.durationUs, initialStartTimeUs, j, true, !hlsMediaPlaylist2.hasEndTag, this.tag);
        } else {
            long j5 = j3 == C.TIME_UNSET ? 0 : j3;
            long j6 = hlsMediaPlaylist2.durationUs;
            singlePeriodTimeline = new SinglePeriodTimeline(j2, usToMs, j6, j6, 0, j5, true, false, this.tag);
        }
        refreshSourceInfo(singlePeriodTimeline, new HlsManifest(this.playlistTracker.getMasterPlaylist(), hlsMediaPlaylist2));
    }

    public void prepareSourceInternal(ExoPlayer exoPlayer, boolean z) {
        this.playlistTracker.start(this.manifestUri, createEventDispatcher((MediaSource.MediaPeriodId) null), this);
    }

    public void releasePeriod(MediaPeriod mediaPeriod) {
        ((HlsMediaPeriod) mediaPeriod).release();
    }

    public void releaseSourceInternal() {
        HlsPlaylistTracker hlsPlaylistTracker = this.playlistTracker;
        if (hlsPlaylistTracker != null) {
            hlsPlaylistTracker.release();
        }
    }
}
