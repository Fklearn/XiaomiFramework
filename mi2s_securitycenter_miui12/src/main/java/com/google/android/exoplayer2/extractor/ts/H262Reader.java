package com.google.android.exoplayer2.extractor.ts;

import android.util.Pair;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.extractor.ts.TsPayloadReader;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.NalUnitUtil;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.util.Arrays;
import java.util.Collections;

public final class H262Reader implements ElementaryStreamReader {
    private static final double[] FRAME_RATE_VALUES = {23.976023976023978d, 24.0d, 25.0d, 29.97002997002997d, 30.0d, 50.0d, 59.94005994005994d, 60.0d};
    private static final int START_EXTENSION = 181;
    private static final int START_GROUP = 184;
    private static final int START_PICTURE = 0;
    private static final int START_SEQUENCE_HEADER = 179;
    private final CsdBuffer csdBuffer = new CsdBuffer(128);
    private String formatId;
    private long frameDurationUs;
    private boolean hasOutputFormat;
    private TrackOutput output;
    private long pesTimeUs;
    private final boolean[] prefixFlags = new boolean[4];
    private boolean sampleHasPicture;
    private boolean sampleIsKeyframe;
    private long samplePosition;
    private long sampleTimeUs;
    private boolean startedFirstSample;
    private long totalBytesWritten;

    private static final class CsdBuffer {
        private static final byte[] START_CODE = {0, 0, 1};
        public byte[] data;
        private boolean isFilling;
        public int length;
        public int sequenceExtensionPosition;

        public CsdBuffer(int i) {
            this.data = new byte[i];
        }

        public void onData(byte[] bArr, int i, int i2) {
            if (this.isFilling) {
                int i3 = i2 - i;
                byte[] bArr2 = this.data;
                int length2 = bArr2.length;
                int i4 = this.length;
                if (length2 < i4 + i3) {
                    this.data = Arrays.copyOf(bArr2, (i4 + i3) * 2);
                }
                System.arraycopy(bArr, i, this.data, this.length, i3);
                this.length += i3;
            }
        }

        public boolean onStartCode(int i, int i2) {
            if (this.isFilling) {
                this.length -= i2;
                if (this.sequenceExtensionPosition == 0 && i == H262Reader.START_EXTENSION) {
                    this.sequenceExtensionPosition = this.length;
                } else {
                    this.isFilling = false;
                    return true;
                }
            } else if (i == H262Reader.START_SEQUENCE_HEADER) {
                this.isFilling = true;
            }
            byte[] bArr = START_CODE;
            onData(bArr, 0, bArr.length);
            return false;
        }

        public void reset() {
            this.isFilling = false;
            this.length = 0;
            this.sequenceExtensionPosition = 0;
        }
    }

    private static Pair<Format, Long> parseCsdBuffer(CsdBuffer csdBuffer2, String str) {
        int i;
        float f;
        float f2;
        CsdBuffer csdBuffer3 = csdBuffer2;
        byte[] copyOf = Arrays.copyOf(csdBuffer3.data, csdBuffer3.length);
        byte b2 = copyOf[5] & 255;
        int i2 = ((copyOf[4] & 255) << 4) | (b2 >> 4);
        byte b3 = ((b2 & 15) << 8) | (copyOf[6] & 255);
        int i3 = (copyOf[7] & 240) >> 4;
        if (i3 != 2) {
            if (i3 == 3) {
                f = (float) (b3 * 16);
                i = i2 * 9;
            } else if (i3 != 4) {
                f2 = 1.0f;
            } else {
                f = (float) (b3 * 121);
                i = i2 * 100;
            }
            f2 = f / ((float) i);
        } else {
            f = (float) (b3 * 4);
            i = i2 * 3;
            f2 = f / ((float) i);
        }
        Format createVideoSampleFormat = Format.createVideoSampleFormat(str, MimeTypes.VIDEO_MPEG2, (String) null, -1, -1, i2, b3, -1.0f, Collections.singletonList(copyOf), -1, f2, (DrmInitData) null);
        long j = 0;
        int i4 = (copyOf[7] & 15) - 1;
        if (i4 >= 0) {
            double[] dArr = FRAME_RATE_VALUES;
            if (i4 < dArr.length) {
                double d2 = dArr[i4];
                int i5 = csdBuffer3.sequenceExtensionPosition + 9;
                int i6 = (copyOf[i5] & 96) >> 5;
                byte b4 = copyOf[i5] & 31;
                if (i6 != b4) {
                    d2 *= (((double) i6) + 1.0d) / ((double) (b4 + 1));
                }
                j = (long) (1000000.0d / d2);
            }
        }
        return Pair.create(createVideoSampleFormat, Long.valueOf(j));
    }

    public void consume(ParsableByteArray parsableByteArray) {
        boolean z;
        boolean z2;
        ParsableByteArray parsableByteArray2 = parsableByteArray;
        int position = parsableByteArray.getPosition();
        int limit = parsableByteArray.limit();
        byte[] bArr = parsableByteArray2.data;
        this.totalBytesWritten += (long) parsableByteArray.bytesLeft();
        this.output.sampleData(parsableByteArray2, parsableByteArray.bytesLeft());
        while (true) {
            int findNalUnit = NalUnitUtil.findNalUnit(bArr, position, limit, this.prefixFlags);
            if (findNalUnit == limit) {
                break;
            }
            int i = findNalUnit + 3;
            byte b2 = parsableByteArray2.data[i] & 255;
            if (!this.hasOutputFormat) {
                int i2 = findNalUnit - position;
                if (i2 > 0) {
                    this.csdBuffer.onData(bArr, position, findNalUnit);
                }
                if (this.csdBuffer.onStartCode(b2, i2 < 0 ? -i2 : 0)) {
                    Pair<Format, Long> parseCsdBuffer = parseCsdBuffer(this.csdBuffer, this.formatId);
                    this.output.format((Format) parseCsdBuffer.first);
                    this.frameDurationUs = ((Long) parseCsdBuffer.second).longValue();
                    this.hasOutputFormat = true;
                }
            }
            if (b2 == 0 || b2 == START_SEQUENCE_HEADER) {
                int i3 = limit - findNalUnit;
                if (this.startedFirstSample && this.sampleHasPicture && this.hasOutputFormat) {
                    this.output.sampleMetadata(this.sampleTimeUs, this.sampleIsKeyframe ? 1 : 0, ((int) (this.totalBytesWritten - this.samplePosition)) - i3, i3, (TrackOutput.CryptoData) null);
                }
                if (!this.startedFirstSample || this.sampleHasPicture) {
                    this.samplePosition = this.totalBytesWritten - ((long) i3);
                    long j = this.pesTimeUs;
                    if (j == C.TIME_UNSET) {
                        j = this.startedFirstSample ? this.sampleTimeUs + this.frameDurationUs : 0;
                    }
                    this.sampleTimeUs = j;
                    z2 = false;
                    this.sampleIsKeyframe = false;
                    this.pesTimeUs = C.TIME_UNSET;
                    z = true;
                    this.startedFirstSample = true;
                } else {
                    z2 = false;
                    z = true;
                }
                if (b2 == 0) {
                    z2 = z;
                }
                this.sampleHasPicture = z2;
            } else if (b2 == START_GROUP) {
                this.sampleIsKeyframe = true;
            }
            position = i;
        }
        if (!this.hasOutputFormat) {
            this.csdBuffer.onData(bArr, position, limit);
        }
    }

    public void createTracks(ExtractorOutput extractorOutput, TsPayloadReader.TrackIdGenerator trackIdGenerator) {
        trackIdGenerator.generateNewId();
        this.formatId = trackIdGenerator.getFormatId();
        this.output = extractorOutput.track(trackIdGenerator.getTrackId(), 2);
    }

    public void packetFinished() {
    }

    public void packetStarted(long j, boolean z) {
        this.pesTimeUs = j;
    }

    public void seek() {
        NalUnitUtil.clearPrefixFlags(this.prefixFlags);
        this.csdBuffer.reset();
        this.totalBytesWritten = 0;
        this.startedFirstSample = false;
    }
}
