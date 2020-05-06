package com.google.android.exoplayer2.extractor.ts;

import android.util.Log;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.ts.TsPayloadReader;
import com.google.android.exoplayer2.util.ParsableBitArray;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.TimestampAdjuster;

public final class PesReader implements TsPayloadReader {
    private static final int HEADER_SIZE = 9;
    private static final int MAX_HEADER_EXTENSION_SIZE = 10;
    private static final int PES_SCRATCH_SIZE = 10;
    private static final int STATE_FINDING_HEADER = 0;
    private static final int STATE_READING_BODY = 3;
    private static final int STATE_READING_HEADER = 1;
    private static final int STATE_READING_HEADER_EXTENSION = 2;
    private static final String TAG = "PesReader";
    private int bytesRead;
    private boolean dataAlignmentIndicator;
    private boolean dtsFlag;
    private int extendedHeaderLength;
    private int payloadSize;
    private final ParsableBitArray pesScratch = new ParsableBitArray(new byte[10]);
    private boolean ptsFlag;
    private final ElementaryStreamReader reader;
    private boolean seenFirstDts;
    private int state = 0;
    private long timeUs;
    private TimestampAdjuster timestampAdjuster;

    public PesReader(ElementaryStreamReader elementaryStreamReader) {
        this.reader = elementaryStreamReader;
    }

    private boolean continueRead(ParsableByteArray parsableByteArray, byte[] bArr, int i) {
        int min = Math.min(parsableByteArray.bytesLeft(), i - this.bytesRead);
        if (min <= 0) {
            return true;
        }
        if (bArr == null) {
            parsableByteArray.skipBytes(min);
        } else {
            parsableByteArray.readBytes(bArr, this.bytesRead, min);
        }
        this.bytesRead += min;
        return this.bytesRead == i;
    }

    private boolean parseHeader() {
        this.pesScratch.setPosition(0);
        int readBits = this.pesScratch.readBits(24);
        if (readBits != 1) {
            Log.w(TAG, "Unexpected start code prefix: " + readBits);
            this.payloadSize = -1;
            return false;
        }
        this.pesScratch.skipBits(8);
        int readBits2 = this.pesScratch.readBits(16);
        this.pesScratch.skipBits(5);
        this.dataAlignmentIndicator = this.pesScratch.readBit();
        this.pesScratch.skipBits(2);
        this.ptsFlag = this.pesScratch.readBit();
        this.dtsFlag = this.pesScratch.readBit();
        this.pesScratch.skipBits(6);
        this.extendedHeaderLength = this.pesScratch.readBits(8);
        if (readBits2 == 0) {
            this.payloadSize = -1;
        } else {
            this.payloadSize = ((readBits2 + 6) - 9) - this.extendedHeaderLength;
        }
        return true;
    }

    private void parseHeaderExtension() {
        this.pesScratch.setPosition(0);
        this.timeUs = C.TIME_UNSET;
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

    private void setState(int i) {
        this.state = i;
        this.bytesRead = 0;
    }

    /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxOverflowException: Regions count limit reached
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:47)
        	at jadx.core.utils.ErrorsCounter.methodError(ErrorsCounter.java:81)
        */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0041  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00c4 A[SYNTHETIC] */
    public final void consume(com.google.android.exoplayer2.util.ParsableByteArray r8, boolean r9) {
        /*
            r7 = this;
            r0 = -1
            r1 = 3
            r2 = 2
            r3 = 1
            if (r9 == 0) goto L_0x003b
            int r9 = r7.state
            if (r9 == 0) goto L_0x0038
            if (r9 == r3) goto L_0x0038
            java.lang.String r4 = "PesReader"
            if (r9 == r2) goto L_0x0033
            if (r9 == r1) goto L_0x0013
            goto L_0x0038
        L_0x0013:
            int r9 = r7.payloadSize
            if (r9 == r0) goto L_0x0073
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r5 = "Unexpected start indicator: expected "
            r9.append(r5)
            int r5 = r7.payloadSize
            r9.append(r5)
            java.lang.String r5 = " more bytes"
            r9.append(r5)
            java.lang.String r9 = r9.toString()
            android.util.Log.w(r4, r9)
            goto L_0x0073
        L_0x0033:
            java.lang.String r9 = "Unexpected start indicator reading extended header"
            android.util.Log.w(r4, r9)
        L_0x0038:
            r7.setState(r3)
        L_0x003b:
            int r9 = r8.bytesLeft()
            if (r9 <= 0) goto L_0x00c4
            int r9 = r7.state
            if (r9 == 0) goto L_0x00bb
            r4 = 0
            if (r9 == r3) goto L_0x00a4
            if (r9 == r2) goto L_0x0079
            if (r9 == r1) goto L_0x004d
            goto L_0x003b
        L_0x004d:
            int r9 = r8.bytesLeft()
            int r5 = r7.payloadSize
            if (r5 != r0) goto L_0x0056
            goto L_0x0058
        L_0x0056:
            int r4 = r9 - r5
        L_0x0058:
            if (r4 <= 0) goto L_0x0063
            int r9 = r9 - r4
            int r4 = r8.getPosition()
            int r4 = r4 + r9
            r8.setLimit(r4)
        L_0x0063:
            com.google.android.exoplayer2.extractor.ts.ElementaryStreamReader r4 = r7.reader
            r4.consume(r8)
            int r4 = r7.payloadSize
            if (r4 == r0) goto L_0x003b
            int r4 = r4 - r9
            r7.payloadSize = r4
            int r9 = r7.payloadSize
            if (r9 != 0) goto L_0x003b
        L_0x0073:
            com.google.android.exoplayer2.extractor.ts.ElementaryStreamReader r9 = r7.reader
            r9.packetFinished()
            goto L_0x0038
        L_0x0079:
            r9 = 10
            int r4 = r7.extendedHeaderLength
            int r9 = java.lang.Math.min(r9, r4)
            com.google.android.exoplayer2.util.ParsableBitArray r4 = r7.pesScratch
            byte[] r4 = r4.data
            boolean r9 = r7.continueRead(r8, r4, r9)
            if (r9 == 0) goto L_0x003b
            r9 = 0
            int r4 = r7.extendedHeaderLength
            boolean r9 = r7.continueRead(r8, r9, r4)
            if (r9 == 0) goto L_0x003b
            r7.parseHeaderExtension()
            com.google.android.exoplayer2.extractor.ts.ElementaryStreamReader r9 = r7.reader
            long r4 = r7.timeUs
            boolean r6 = r7.dataAlignmentIndicator
            r9.packetStarted(r4, r6)
            r7.setState(r1)
            goto L_0x003b
        L_0x00a4:
            com.google.android.exoplayer2.util.ParsableBitArray r9 = r7.pesScratch
            byte[] r9 = r9.data
            r5 = 9
            boolean r9 = r7.continueRead(r8, r9, r5)
            if (r9 == 0) goto L_0x003b
            boolean r9 = r7.parseHeader()
            if (r9 == 0) goto L_0x00b7
            r4 = r2
        L_0x00b7:
            r7.setState(r4)
            goto L_0x003b
        L_0x00bb:
            int r9 = r8.bytesLeft()
            r8.skipBytes(r9)
            goto L_0x003b
        L_0x00c4:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.extractor.ts.PesReader.consume(com.google.android.exoplayer2.util.ParsableByteArray, boolean):void");
    }

    public void init(TimestampAdjuster timestampAdjuster2, ExtractorOutput extractorOutput, TsPayloadReader.TrackIdGenerator trackIdGenerator) {
        this.timestampAdjuster = timestampAdjuster2;
        this.reader.createTracks(extractorOutput, trackIdGenerator);
    }

    public final void seek() {
        this.state = 0;
        this.bytesRead = 0;
        this.seenFirstDts = false;
        this.reader.seek();
    }
}
