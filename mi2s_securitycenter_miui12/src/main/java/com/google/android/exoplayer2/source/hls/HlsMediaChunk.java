package com.google.android.exoplayer2.source.hls;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.extractor.DefaultExtractorInput;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.id3.Id3Decoder;
import com.google.android.exoplayer2.metadata.id3.PrivFrame;
import com.google.android.exoplayer2.source.chunk.MediaChunk;
import com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import com.google.android.exoplayer2.util.Util;
import java.util.concurrent.atomic.AtomicInteger;

final class HlsMediaChunk extends MediaChunk {
    private static final String PRIV_TIMESTAMP_FRAME_OWNER = "com.apple.streaming.transportStreamTimestamp";
    private static final AtomicInteger uidSource = new AtomicInteger();
    private int bytesLoaded;
    public final int discontinuitySequenceNumber;
    private final Extractor extractor;
    private final boolean hasGapTag;
    public final HlsMasterPlaylist.HlsUrl hlsUrl;
    private final ParsableByteArray id3Data;
    private final Id3Decoder id3Decoder;
    private boolean id3TimestampPeeked;
    private final DataSource initDataSource;
    private final DataSpec initDataSpec;
    private boolean initLoadCompleted;
    private int initSegmentBytesLoaded;
    private final boolean isEncrypted = (this.dataSource instanceof Aes128DataSource);
    private final boolean isMasterTimestampSource;
    private final boolean isPackedAudioExtractor;
    private volatile boolean loadCanceled;
    private volatile boolean loadCompleted;
    private HlsSampleStreamWrapper output;
    private final boolean reusingExtractor;
    private final boolean shouldSpliceIn;
    private final TimestampAdjuster timestampAdjuster;
    public final int uid;

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public HlsMediaChunk(com.google.android.exoplayer2.source.hls.HlsExtractorFactory r17, com.google.android.exoplayer2.upstream.DataSource r18, com.google.android.exoplayer2.upstream.DataSpec r19, com.google.android.exoplayer2.upstream.DataSpec r20, com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist.HlsUrl r21, java.util.List<com.google.android.exoplayer2.Format> r22, int r23, java.lang.Object r24, long r25, long r27, long r29, int r31, boolean r32, boolean r33, com.google.android.exoplayer2.util.TimestampAdjuster r34, com.google.android.exoplayer2.source.hls.HlsMediaChunk r35, com.google.android.exoplayer2.drm.DrmInitData r36, byte[] r37, byte[] r38) {
        /*
            r16 = this;
            r12 = r16
            r13 = r18
            r14 = r20
            r15 = r21
            r10 = r31
            r11 = r35
            r0 = r37
            r1 = r38
            com.google.android.exoplayer2.upstream.DataSource r1 = buildDataSource(r13, r0, r1)
            com.google.android.exoplayer2.Format r3 = r15.format
            r0 = r16
            r2 = r19
            r4 = r23
            r5 = r24
            r6 = r25
            r8 = r27
            r13 = r10
            r10 = r29
            r0.<init>(r1, r2, r3, r4, r5, r6, r8, r10)
            r12.discontinuitySequenceNumber = r13
            r12.initDataSpec = r14
            r12.hlsUrl = r15
            r0 = r33
            r12.isMasterTimestampSource = r0
            r0 = r34
            r12.timestampAdjuster = r0
            com.google.android.exoplayer2.upstream.DataSource r1 = r12.dataSource
            boolean r1 = r1 instanceof com.google.android.exoplayer2.source.hls.Aes128DataSource
            r12.isEncrypted = r1
            r1 = r32
            r12.hasGapTag = r1
            r1 = 1
            r2 = 0
            r3 = 0
            r4 = r35
            if (r4 == 0) goto L_0x0061
            com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist$HlsUrl r5 = r4.hlsUrl
            if (r5 == r15) goto L_0x004d
            r5 = r1
            goto L_0x004e
        L_0x004d:
            r5 = r2
        L_0x004e:
            r12.shouldSpliceIn = r5
            int r5 = r4.discontinuitySequenceNumber
            if (r5 != r13) goto L_0x005c
            boolean r5 = r12.shouldSpliceIn
            if (r5 == 0) goto L_0x0059
            goto L_0x005c
        L_0x0059:
            com.google.android.exoplayer2.extractor.Extractor r5 = r4.extractor
            goto L_0x005d
        L_0x005c:
            r5 = r3
        L_0x005d:
            r6 = r5
            r5 = r19
            goto L_0x0066
        L_0x0061:
            r12.shouldSpliceIn = r2
            r5 = r19
            r6 = r3
        L_0x0066:
            android.net.Uri r5 = r5.uri
            com.google.android.exoplayer2.Format r7 = r12.trackFormat
            r23 = r17
            r24 = r6
            r25 = r5
            r26 = r7
            r27 = r22
            r28 = r36
            r29 = r34
            android.util.Pair r0 = r23.createExtractor(r24, r25, r26, r27, r28, r29)
            java.lang.Object r5 = r0.first
            com.google.android.exoplayer2.extractor.Extractor r5 = (com.google.android.exoplayer2.extractor.Extractor) r5
            r12.extractor = r5
            java.lang.Object r0 = r0.second
            java.lang.Boolean r0 = (java.lang.Boolean) r0
            boolean r0 = r0.booleanValue()
            r12.isPackedAudioExtractor = r0
            com.google.android.exoplayer2.extractor.Extractor r0 = r12.extractor
            if (r0 != r6) goto L_0x0092
            r0 = r1
            goto L_0x0093
        L_0x0092:
            r0 = r2
        L_0x0093:
            r12.reusingExtractor = r0
            boolean r0 = r12.reusingExtractor
            if (r0 == 0) goto L_0x009c
            if (r14 == 0) goto L_0x009c
            goto L_0x009d
        L_0x009c:
            r1 = r2
        L_0x009d:
            r12.initLoadCompleted = r1
            boolean r0 = r12.isPackedAudioExtractor
            if (r0 == 0) goto L_0x00bf
            if (r4 == 0) goto L_0x00ae
            com.google.android.exoplayer2.util.ParsableByteArray r0 = r4.id3Data
            if (r0 == 0) goto L_0x00ae
            com.google.android.exoplayer2.metadata.id3.Id3Decoder r1 = r4.id3Decoder
            r12.id3Decoder = r1
            goto L_0x00bc
        L_0x00ae:
            com.google.android.exoplayer2.metadata.id3.Id3Decoder r0 = new com.google.android.exoplayer2.metadata.id3.Id3Decoder
            r0.<init>()
            r12.id3Decoder = r0
            com.google.android.exoplayer2.util.ParsableByteArray r0 = new com.google.android.exoplayer2.util.ParsableByteArray
            r1 = 10
            r0.<init>((int) r1)
        L_0x00bc:
            r12.id3Data = r0
            goto L_0x00c3
        L_0x00bf:
            r12.id3Decoder = r3
            r12.id3Data = r3
        L_0x00c3:
            r0 = r18
            r12.initDataSource = r0
            java.util.concurrent.atomic.AtomicInteger r0 = uidSource
            int r0 = r0.getAndIncrement()
            r12.uid = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.hls.HlsMediaChunk.<init>(com.google.android.exoplayer2.source.hls.HlsExtractorFactory, com.google.android.exoplayer2.upstream.DataSource, com.google.android.exoplayer2.upstream.DataSpec, com.google.android.exoplayer2.upstream.DataSpec, com.google.android.exoplayer2.source.hls.playlist.HlsMasterPlaylist$HlsUrl, java.util.List, int, java.lang.Object, long, long, long, int, boolean, boolean, com.google.android.exoplayer2.util.TimestampAdjuster, com.google.android.exoplayer2.source.hls.HlsMediaChunk, com.google.android.exoplayer2.drm.DrmInitData, byte[], byte[]):void");
    }

    private static DataSource buildDataSource(DataSource dataSource, byte[] bArr, byte[] bArr2) {
        return bArr != null ? new Aes128DataSource(dataSource, bArr, bArr2) : dataSource;
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x0022  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0061 A[Catch:{ all -> 0x0082, all -> 0x00a2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0068 A[Catch:{ all -> 0x0082, all -> 0x00a2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x006f A[Catch:{ all -> 0x0082, all -> 0x00a2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0076 A[SYNTHETIC, Splitter:B:26:0x0076] */
    /* JADX WARNING: Removed duplicated region for block: B:9:0x001c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void loadMedia() {
        /*
            r13 = this;
            boolean r0 = r13.isEncrypted
            r1 = 1
            r2 = 0
            if (r0 == 0) goto L_0x000e
            com.google.android.exoplayer2.upstream.DataSpec r0 = r13.dataSpec
            int r3 = r13.bytesLoaded
            if (r3 == 0) goto L_0x0017
            r3 = r1
            goto L_0x0018
        L_0x000e:
            com.google.android.exoplayer2.upstream.DataSpec r0 = r13.dataSpec
            int r3 = r13.bytesLoaded
            long r3 = (long) r3
            com.google.android.exoplayer2.upstream.DataSpec r0 = r0.subrange(r3)
        L_0x0017:
            r3 = r2
        L_0x0018:
            boolean r4 = r13.isMasterTimestampSource
            if (r4 != 0) goto L_0x0022
            com.google.android.exoplayer2.util.TimestampAdjuster r4 = r13.timestampAdjuster
            r4.waitUntilInitialized()
            goto L_0x0038
        L_0x0022:
            com.google.android.exoplayer2.util.TimestampAdjuster r4 = r13.timestampAdjuster
            long r4 = r4.getFirstSampleTimestampUs()
            r6 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            int r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r4 != 0) goto L_0x0038
            com.google.android.exoplayer2.util.TimestampAdjuster r4 = r13.timestampAdjuster
            long r5 = r13.startTimeUs
            r4.setFirstSampleTimestampUs(r5)
        L_0x0038:
            com.google.android.exoplayer2.extractor.DefaultExtractorInput r4 = new com.google.android.exoplayer2.extractor.DefaultExtractorInput     // Catch:{ all -> 0x00a2 }
            com.google.android.exoplayer2.upstream.DataSource r8 = r13.dataSource     // Catch:{ all -> 0x00a2 }
            long r9 = r0.absoluteStreamPosition     // Catch:{ all -> 0x00a2 }
            com.google.android.exoplayer2.upstream.DataSource r5 = r13.dataSource     // Catch:{ all -> 0x00a2 }
            long r11 = r5.open(r0)     // Catch:{ all -> 0x00a2 }
            r7 = r4
            r7.<init>(r8, r9, r11)     // Catch:{ all -> 0x00a2 }
            boolean r0 = r13.isPackedAudioExtractor     // Catch:{ all -> 0x00a2 }
            if (r0 == 0) goto L_0x006d
            boolean r0 = r13.id3TimestampPeeked     // Catch:{ all -> 0x00a2 }
            if (r0 != 0) goto L_0x006d
            long r5 = r13.peekId3PrivTimestamp(r4)     // Catch:{ all -> 0x00a2 }
            r13.id3TimestampPeeked = r1     // Catch:{ all -> 0x00a2 }
            com.google.android.exoplayer2.source.hls.HlsSampleStreamWrapper r0 = r13.output     // Catch:{ all -> 0x00a2 }
            r7 = -9223372036854775807(0x8000000000000001, double:-4.9E-324)
            int r1 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r1 == 0) goto L_0x0068
            com.google.android.exoplayer2.util.TimestampAdjuster r1 = r13.timestampAdjuster     // Catch:{ all -> 0x00a2 }
            long r5 = r1.adjustTsTimestamp(r5)     // Catch:{ all -> 0x00a2 }
            goto L_0x006a
        L_0x0068:
            long r5 = r13.startTimeUs     // Catch:{ all -> 0x00a2 }
        L_0x006a:
            r0.setSampleOffsetUs(r5)     // Catch:{ all -> 0x00a2 }
        L_0x006d:
            if (r3 == 0) goto L_0x0074
            int r0 = r13.bytesLoaded     // Catch:{ all -> 0x00a2 }
            r4.skipFully(r0)     // Catch:{ all -> 0x00a2 }
        L_0x0074:
            if (r2 != 0) goto L_0x0090
            boolean r0 = r13.loadCanceled     // Catch:{ all -> 0x0082 }
            if (r0 != 0) goto L_0x0090
            com.google.android.exoplayer2.extractor.Extractor r0 = r13.extractor     // Catch:{ all -> 0x0082 }
            r1 = 0
            int r2 = r0.read(r4, r1)     // Catch:{ all -> 0x0082 }
            goto L_0x0074
        L_0x0082:
            r0 = move-exception
            long r1 = r4.getPosition()     // Catch:{ all -> 0x00a2 }
            com.google.android.exoplayer2.upstream.DataSpec r3 = r13.dataSpec     // Catch:{ all -> 0x00a2 }
            long r3 = r3.absoluteStreamPosition     // Catch:{ all -> 0x00a2 }
            long r1 = r1 - r3
            int r1 = (int) r1     // Catch:{ all -> 0x00a2 }
            r13.bytesLoaded = r1     // Catch:{ all -> 0x00a2 }
            throw r0     // Catch:{ all -> 0x00a2 }
        L_0x0090:
            long r0 = r4.getPosition()     // Catch:{ all -> 0x00a2 }
            com.google.android.exoplayer2.upstream.DataSpec r2 = r13.dataSpec     // Catch:{ all -> 0x00a2 }
            long r2 = r2.absoluteStreamPosition     // Catch:{ all -> 0x00a2 }
            long r0 = r0 - r2
            int r0 = (int) r0     // Catch:{ all -> 0x00a2 }
            r13.bytesLoaded = r0     // Catch:{ all -> 0x00a2 }
            com.google.android.exoplayer2.upstream.DataSource r0 = r13.dataSource
            com.google.android.exoplayer2.util.Util.closeQuietly((com.google.android.exoplayer2.upstream.DataSource) r0)
            return
        L_0x00a2:
            r0 = move-exception
            com.google.android.exoplayer2.upstream.DataSource r1 = r13.dataSource
            com.google.android.exoplayer2.util.Util.closeQuietly((com.google.android.exoplayer2.upstream.DataSource) r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.hls.HlsMediaChunk.loadMedia():void");
    }

    private void maybeLoadInitData() {
        DataSpec dataSpec;
        DefaultExtractorInput defaultExtractorInput;
        if (!this.initLoadCompleted && (dataSpec = this.initDataSpec) != null) {
            DataSpec subrange = dataSpec.subrange((long) this.initSegmentBytesLoaded);
            try {
                defaultExtractorInput = new DefaultExtractorInput(this.initDataSource, subrange.absoluteStreamPosition, this.initDataSource.open(subrange));
                int i = 0;
                while (i == 0) {
                    if (this.loadCanceled) {
                        break;
                    }
                    i = this.extractor.read(defaultExtractorInput, (PositionHolder) null);
                }
                this.initSegmentBytesLoaded = (int) (defaultExtractorInput.getPosition() - this.initDataSpec.absoluteStreamPosition);
                Util.closeQuietly(this.initDataSource);
                this.initLoadCompleted = true;
            } catch (Throwable th) {
                Util.closeQuietly(this.initDataSource);
                throw th;
            }
        }
    }

    private long peekId3PrivTimestamp(ExtractorInput extractorInput) {
        Metadata decode;
        extractorInput.resetPeekPosition();
        if (!extractorInput.peekFully(this.id3Data.data, 0, 10, true)) {
            return C.TIME_UNSET;
        }
        this.id3Data.reset(10);
        if (this.id3Data.readUnsignedInt24() != Id3Decoder.ID3_TAG) {
            return C.TIME_UNSET;
        }
        this.id3Data.skipBytes(3);
        int readSynchSafeInt = this.id3Data.readSynchSafeInt();
        int i = readSynchSafeInt + 10;
        if (i > this.id3Data.capacity()) {
            ParsableByteArray parsableByteArray = this.id3Data;
            byte[] bArr = parsableByteArray.data;
            parsableByteArray.reset(i);
            System.arraycopy(bArr, 0, this.id3Data.data, 0, 10);
        }
        if (!extractorInput.peekFully(this.id3Data.data, 10, readSynchSafeInt, true) || (decode = this.id3Decoder.decode(this.id3Data.data, readSynchSafeInt)) == null) {
            return C.TIME_UNSET;
        }
        int length = decode.length();
        for (int i2 = 0; i2 < length; i2++) {
            Metadata.Entry entry = decode.get(i2);
            if (entry instanceof PrivFrame) {
                PrivFrame privFrame = (PrivFrame) entry;
                if (PRIV_TIMESTAMP_FRAME_OWNER.equals(privFrame.owner)) {
                    System.arraycopy(privFrame.privateData, 0, this.id3Data.data, 0, 8);
                    this.id3Data.reset(8);
                    return this.id3Data.readLong() & 8589934591L;
                }
            }
        }
        return C.TIME_UNSET;
    }

    public long bytesLoaded() {
        return (long) this.bytesLoaded;
    }

    public void cancelLoad() {
        this.loadCanceled = true;
    }

    public void init(HlsSampleStreamWrapper hlsSampleStreamWrapper) {
        this.output = hlsSampleStreamWrapper;
        hlsSampleStreamWrapper.init(this.uid, this.shouldSpliceIn, this.reusingExtractor);
        if (!this.reusingExtractor) {
            this.extractor.init(hlsSampleStreamWrapper);
        }
    }

    public boolean isLoadCompleted() {
        return this.loadCompleted;
    }

    public void load() {
        maybeLoadInitData();
        if (!this.loadCanceled) {
            if (!this.hasGapTag) {
                loadMedia();
            }
            this.loadCompleted = true;
        }
    }
}
