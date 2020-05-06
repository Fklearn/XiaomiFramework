package com.google.android.exoplayer2.extractor.ts;

import android.util.SparseArray;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.MpegAudioHeader;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.util.ParsableBitArray;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.TimestampAdjuster;

public final class PsExtractor implements Extractor {
    public static final int AUDIO_STREAM = 192;
    public static final int AUDIO_STREAM_MASK = 224;
    public static final ExtractorsFactory FACTORY = new ExtractorsFactory() {
        public Extractor[] createExtractors() {
            return new Extractor[]{new PsExtractor()};
        }
    };
    private static final long MAX_SEARCH_LENGTH = 1048576;
    private static final int MAX_STREAM_ID_PLUS_ONE = 256;
    private static final int MPEG_PROGRAM_END_CODE = 441;
    private static final int PACKET_START_CODE_PREFIX = 1;
    private static final int PACK_START_CODE = 442;
    public static final int PRIVATE_STREAM_1 = 189;
    private static final int SYSTEM_HEADER_START_CODE = 443;
    public static final int VIDEO_STREAM = 224;
    public static final int VIDEO_STREAM_MASK = 240;
    private boolean foundAllTracks;
    private boolean foundAudioTrack;
    private boolean foundVideoTrack;
    private ExtractorOutput output;
    private final ParsableByteArray psPacketBuffer;
    private final SparseArray<PesReader> psPayloadReaders;
    private final TimestampAdjuster timestampAdjuster;

    private static final class PesReader {
        private static final int PES_SCRATCH_SIZE = 64;
        private boolean dtsFlag;
        private int extendedHeaderLength;
        private final ElementaryStreamReader pesPayloadReader;
        private final ParsableBitArray pesScratch = new ParsableBitArray(new byte[64]);
        private boolean ptsFlag;
        private boolean seenFirstDts;
        private long timeUs;
        private final TimestampAdjuster timestampAdjuster;

        public PesReader(ElementaryStreamReader elementaryStreamReader, TimestampAdjuster timestampAdjuster2) {
            this.pesPayloadReader = elementaryStreamReader;
            this.timestampAdjuster = timestampAdjuster2;
        }

        private void parseHeader() {
            this.pesScratch.skipBits(8);
            this.ptsFlag = this.pesScratch.readBit();
            this.dtsFlag = this.pesScratch.readBit();
            this.pesScratch.skipBits(6);
            this.extendedHeaderLength = this.pesScratch.readBits(8);
        }

        private void parseHeaderExtension() {
            this.timeUs = 0;
            if (this.ptsFlag) {
                this.pesScratch.skipBits(4);
                this.pesScratch.skipBits(1);
                this.pesScratch.skipBits(1);
                long readBits = (((long) this.pesScratch.readBits(3)) << 30) | ((long) (this.pesScratch.readBits(15) << 15)) | ((long) this.pesScratch.readBits(15));
                this.pesScratch.skipBits(1);
                if (!this.seenFirstDts && this.dtsFlag) {
                    this.pesScratch.skipBits(4);
                    this.pesScratch.skipBits(1);
                    this.pesScratch.skipBits(1);
                    this.pesScratch.skipBits(1);
                    this.timestampAdjuster.adjustTsTimestamp((((long) this.pesScratch.readBits(3)) << 30) | ((long) (this.pesScratch.readBits(15) << 15)) | ((long) this.pesScratch.readBits(15)));
                    this.seenFirstDts = true;
                }
                this.timeUs = this.timestampAdjuster.adjustTsTimestamp(readBits);
            }
        }

        public void consume(ParsableByteArray parsableByteArray) {
            parsableByteArray.readBytes(this.pesScratch.data, 0, 3);
            this.pesScratch.setPosition(0);
            parseHeader();
            parsableByteArray.readBytes(this.pesScratch.data, 0, this.extendedHeaderLength);
            this.pesScratch.setPosition(0);
            parseHeaderExtension();
            this.pesPayloadReader.packetStarted(this.timeUs, true);
            this.pesPayloadReader.consume(parsableByteArray);
            this.pesPayloadReader.packetFinished();
        }

        public void seek() {
            this.seenFirstDts = false;
            this.pesPayloadReader.seek();
        }
    }

    public PsExtractor() {
        this(new TimestampAdjuster(0));
    }

    public PsExtractor(TimestampAdjuster timestampAdjuster2) {
        this.timestampAdjuster = timestampAdjuster2;
        this.psPacketBuffer = new ParsableByteArray((int) MpegAudioHeader.MAX_FRAME_SIZE_BYTES);
        this.psPayloadReaders = new SparseArray<>();
    }

    public void init(ExtractorOutput extractorOutput) {
        this.output = extractorOutput;
        extractorOutput.seekMap(new SeekMap.Unseekable(C.TIME_UNSET));
    }

    /* JADX WARNING: Removed duplicated region for block: B:38:0x00a9  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int read(com.google.android.exoplayer2.extractor.ExtractorInput r10, com.google.android.exoplayer2.extractor.PositionHolder r11) {
        /*
            r9 = this;
            com.google.android.exoplayer2.util.ParsableByteArray r11 = r9.psPacketBuffer
            byte[] r11 = r11.data
            r0 = 1
            r1 = 0
            r2 = 4
            boolean r11 = r10.peekFully(r11, r1, r2, r0)
            r2 = -1
            if (r11 != 0) goto L_0x000f
            return r2
        L_0x000f:
            com.google.android.exoplayer2.util.ParsableByteArray r11 = r9.psPacketBuffer
            r11.setPosition(r1)
            com.google.android.exoplayer2.util.ParsableByteArray r11 = r9.psPacketBuffer
            int r11 = r11.readInt()
            r3 = 441(0x1b9, float:6.18E-43)
            if (r11 != r3) goto L_0x001f
            return r2
        L_0x001f:
            r2 = 442(0x1ba, float:6.2E-43)
            if (r11 != r2) goto L_0x0041
            com.google.android.exoplayer2.util.ParsableByteArray r11 = r9.psPacketBuffer
            byte[] r11 = r11.data
            r0 = 10
            r10.peekFully(r11, r1, r0)
            com.google.android.exoplayer2.util.ParsableByteArray r11 = r9.psPacketBuffer
            r0 = 9
            r11.setPosition(r0)
            com.google.android.exoplayer2.util.ParsableByteArray r11 = r9.psPacketBuffer
            int r11 = r11.readUnsignedByte()
            r11 = r11 & 7
            int r11 = r11 + 14
        L_0x003d:
            r10.skipFully(r11)
            return r1
        L_0x0041:
            r2 = 443(0x1bb, float:6.21E-43)
            r3 = 2
            r4 = 6
            if (r11 != r2) goto L_0x005b
            com.google.android.exoplayer2.util.ParsableByteArray r11 = r9.psPacketBuffer
            byte[] r11 = r11.data
            r10.peekFully(r11, r1, r3)
            com.google.android.exoplayer2.util.ParsableByteArray r11 = r9.psPacketBuffer
            r11.setPosition(r1)
            com.google.android.exoplayer2.util.ParsableByteArray r11 = r9.psPacketBuffer
            int r11 = r11.readUnsignedShort()
            int r11 = r11 + r4
            goto L_0x003d
        L_0x005b:
            r2 = r11 & -256(0xffffffffffffff00, float:NaN)
            int r2 = r2 >> 8
            if (r2 == r0) goto L_0x0065
            r10.skipFully(r0)
            return r1
        L_0x0065:
            r11 = r11 & 255(0xff, float:3.57E-43)
            android.util.SparseArray<com.google.android.exoplayer2.extractor.ts.PsExtractor$PesReader> r2 = r9.psPayloadReaders
            java.lang.Object r2 = r2.get(r11)
            com.google.android.exoplayer2.extractor.ts.PsExtractor$PesReader r2 = (com.google.android.exoplayer2.extractor.ts.PsExtractor.PesReader) r2
            boolean r5 = r9.foundAllTracks
            if (r5 != 0) goto L_0x00db
            if (r2 != 0) goto L_0x00c1
            r5 = 0
            boolean r6 = r9.foundAudioTrack
            if (r6 != 0) goto L_0x0086
            r6 = 189(0xbd, float:2.65E-43)
            if (r11 != r6) goto L_0x0086
            com.google.android.exoplayer2.extractor.ts.Ac3Reader r5 = new com.google.android.exoplayer2.extractor.ts.Ac3Reader
            r5.<init>()
        L_0x0083:
            r9.foundAudioTrack = r0
            goto L_0x00a7
        L_0x0086:
            boolean r6 = r9.foundAudioTrack
            if (r6 != 0) goto L_0x0096
            r6 = r11 & 224(0xe0, float:3.14E-43)
            r7 = 192(0xc0, float:2.69E-43)
            if (r6 != r7) goto L_0x0096
            com.google.android.exoplayer2.extractor.ts.MpegAudioReader r5 = new com.google.android.exoplayer2.extractor.ts.MpegAudioReader
            r5.<init>()
            goto L_0x0083
        L_0x0096:
            boolean r6 = r9.foundVideoTrack
            if (r6 != 0) goto L_0x00a7
            r6 = r11 & 240(0xf0, float:3.36E-43)
            r7 = 224(0xe0, float:3.14E-43)
            if (r6 != r7) goto L_0x00a7
            com.google.android.exoplayer2.extractor.ts.H262Reader r5 = new com.google.android.exoplayer2.extractor.ts.H262Reader
            r5.<init>()
            r9.foundVideoTrack = r0
        L_0x00a7:
            if (r5 == 0) goto L_0x00c1
            com.google.android.exoplayer2.extractor.ts.TsPayloadReader$TrackIdGenerator r2 = new com.google.android.exoplayer2.extractor.ts.TsPayloadReader$TrackIdGenerator
            r6 = 256(0x100, float:3.59E-43)
            r2.<init>(r11, r6)
            com.google.android.exoplayer2.extractor.ExtractorOutput r6 = r9.output
            r5.createTracks(r6, r2)
            com.google.android.exoplayer2.extractor.ts.PsExtractor$PesReader r2 = new com.google.android.exoplayer2.extractor.ts.PsExtractor$PesReader
            com.google.android.exoplayer2.util.TimestampAdjuster r6 = r9.timestampAdjuster
            r2.<init>(r5, r6)
            android.util.SparseArray<com.google.android.exoplayer2.extractor.ts.PsExtractor$PesReader> r5 = r9.psPayloadReaders
            r5.put(r11, r2)
        L_0x00c1:
            boolean r11 = r9.foundAudioTrack
            if (r11 == 0) goto L_0x00c9
            boolean r11 = r9.foundVideoTrack
            if (r11 != 0) goto L_0x00d4
        L_0x00c9:
            long r5 = r10.getPosition()
            r7 = 1048576(0x100000, double:5.180654E-318)
            int r11 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r11 <= 0) goto L_0x00db
        L_0x00d4:
            r9.foundAllTracks = r0
            com.google.android.exoplayer2.extractor.ExtractorOutput r11 = r9.output
            r11.endTracks()
        L_0x00db:
            com.google.android.exoplayer2.util.ParsableByteArray r11 = r9.psPacketBuffer
            byte[] r11 = r11.data
            r10.peekFully(r11, r1, r3)
            com.google.android.exoplayer2.util.ParsableByteArray r11 = r9.psPacketBuffer
            r11.setPosition(r1)
            com.google.android.exoplayer2.util.ParsableByteArray r11 = r9.psPacketBuffer
            int r11 = r11.readUnsignedShort()
            int r11 = r11 + r4
            if (r2 != 0) goto L_0x00f4
            r10.skipFully(r11)
            goto L_0x0113
        L_0x00f4:
            com.google.android.exoplayer2.util.ParsableByteArray r0 = r9.psPacketBuffer
            r0.reset(r11)
            com.google.android.exoplayer2.util.ParsableByteArray r0 = r9.psPacketBuffer
            byte[] r0 = r0.data
            r10.readFully(r0, r1, r11)
            com.google.android.exoplayer2.util.ParsableByteArray r10 = r9.psPacketBuffer
            r10.setPosition(r4)
            com.google.android.exoplayer2.util.ParsableByteArray r10 = r9.psPacketBuffer
            r2.consume(r10)
            com.google.android.exoplayer2.util.ParsableByteArray r10 = r9.psPacketBuffer
            int r11 = r10.capacity()
            r10.setLimit(r11)
        L_0x0113:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.extractor.ts.PsExtractor.read(com.google.android.exoplayer2.extractor.ExtractorInput, com.google.android.exoplayer2.extractor.PositionHolder):int");
    }

    public void release() {
    }

    public void seek(long j, long j2) {
        this.timestampAdjuster.reset();
        for (int i = 0; i < this.psPayloadReaders.size(); i++) {
            this.psPayloadReaders.valueAt(i).seek();
        }
    }

    public boolean sniff(ExtractorInput extractorInput) {
        byte[] bArr = new byte[14];
        extractorInput.peekFully(bArr, 0, 14);
        if (PACK_START_CODE != (((bArr[0] & 255) << 24) | ((bArr[1] & 255) << 16) | ((bArr[2] & 255) << 8) | (bArr[3] & 255)) || (bArr[4] & 196) != 68 || (bArr[6] & 4) != 4 || (bArr[8] & 4) != 4 || (bArr[9] & 1) != 1 || (bArr[12] & 3) != 3) {
            return false;
        }
        extractorInput.advancePeekPosition(bArr[13] & 7);
        extractorInput.peekFully(bArr, 0, 3);
        return 1 == ((((bArr[0] & 255) << 16) | ((bArr[1] & 255) << 8)) | (bArr[2] & 255));
    }
}
