package com.google.android.exoplayer2.extractor.ts;

import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.ts.TsPayloadReader;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.TimestampAdjuster;

public final class SectionReader implements TsPayloadReader {
    private static final int DEFAULT_SECTION_BUFFER_LENGTH = 32;
    private static final int MAX_SECTION_LENGTH = 4098;
    private static final int SECTION_HEADER_LENGTH = 3;
    private int bytesRead;
    private final SectionPayloadReader reader;
    private final ParsableByteArray sectionData = new ParsableByteArray(32);
    private boolean sectionSyntaxIndicator;
    private int totalSectionLength;
    private boolean waitingForPayloadStart;

    public SectionReader(SectionPayloadReader sectionPayloadReader) {
        this.reader = sectionPayloadReader;
    }

    /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxOverflowException: Regions count limit reached
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:47)
        	at jadx.core.utils.ErrorsCounter.methodError(ErrorsCounter.java:81)
        */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x0023  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00f3 A[SYNTHETIC] */
    public void consume(com.google.android.exoplayer2.util.ParsableByteArray r8, boolean r9) {
        /*
            r7 = this;
            r0 = -1
            if (r9 == 0) goto L_0x000d
            int r1 = r8.readUnsignedByte()
            int r2 = r8.getPosition()
            int r1 = r1 + r2
            goto L_0x000e
        L_0x000d:
            r1 = r0
        L_0x000e:
            boolean r2 = r7.waitingForPayloadStart
            r3 = 0
            if (r2 == 0) goto L_0x001d
            if (r9 != 0) goto L_0x0016
            return
        L_0x0016:
            r7.waitingForPayloadStart = r3
            r8.setPosition(r1)
        L_0x001b:
            r7.bytesRead = r3
        L_0x001d:
            int r9 = r8.bytesLeft()
            if (r9 <= 0) goto L_0x00f3
            int r9 = r7.bytesRead
            r1 = 1
            r2 = 3
            if (r9 >= r2) goto L_0x00a9
            if (r9 != 0) goto L_0x003e
            int r9 = r8.readUnsignedByte()
            int r4 = r8.getPosition()
            int r4 = r4 - r1
            r8.setPosition(r4)
            r4 = 255(0xff, float:3.57E-43)
            if (r9 != r4) goto L_0x003e
            r7.waitingForPayloadStart = r1
            return
        L_0x003e:
            int r9 = r8.bytesLeft()
            int r4 = r7.bytesRead
            int r4 = 3 - r4
            int r9 = java.lang.Math.min(r9, r4)
            com.google.android.exoplayer2.util.ParsableByteArray r4 = r7.sectionData
            byte[] r4 = r4.data
            int r5 = r7.bytesRead
            r8.readBytes(r4, r5, r9)
            int r4 = r7.bytesRead
            int r4 = r4 + r9
            r7.bytesRead = r4
            int r9 = r7.bytesRead
            if (r9 != r2) goto L_0x001d
            com.google.android.exoplayer2.util.ParsableByteArray r9 = r7.sectionData
            r9.reset(r2)
            com.google.android.exoplayer2.util.ParsableByteArray r9 = r7.sectionData
            r9.skipBytes(r1)
            com.google.android.exoplayer2.util.ParsableByteArray r9 = r7.sectionData
            int r9 = r9.readUnsignedByte()
            com.google.android.exoplayer2.util.ParsableByteArray r4 = r7.sectionData
            int r4 = r4.readUnsignedByte()
            r5 = r9 & 128(0x80, float:1.794E-43)
            if (r5 == 0) goto L_0x0077
            goto L_0x0078
        L_0x0077:
            r1 = r3
        L_0x0078:
            r7.sectionSyntaxIndicator = r1
            r9 = r9 & 15
            int r9 = r9 << 8
            r9 = r9 | r4
            int r9 = r9 + r2
            r7.totalSectionLength = r9
            com.google.android.exoplayer2.util.ParsableByteArray r9 = r7.sectionData
            int r9 = r9.capacity()
            int r1 = r7.totalSectionLength
            if (r9 >= r1) goto L_0x001d
            com.google.android.exoplayer2.util.ParsableByteArray r9 = r7.sectionData
            byte[] r4 = r9.data
            r5 = 4098(0x1002, float:5.743E-42)
            int r6 = r4.length
            int r6 = r6 * 2
            int r1 = java.lang.Math.max(r1, r6)
            int r1 = java.lang.Math.min(r5, r1)
            r9.reset(r1)
            com.google.android.exoplayer2.util.ParsableByteArray r9 = r7.sectionData
            byte[] r9 = r9.data
            java.lang.System.arraycopy(r4, r3, r9, r3, r2)
            goto L_0x001d
        L_0x00a9:
            int r9 = r8.bytesLeft()
            int r2 = r7.totalSectionLength
            int r4 = r7.bytesRead
            int r2 = r2 - r4
            int r9 = java.lang.Math.min(r9, r2)
            com.google.android.exoplayer2.util.ParsableByteArray r2 = r7.sectionData
            byte[] r2 = r2.data
            int r4 = r7.bytesRead
            r8.readBytes(r2, r4, r9)
            int r2 = r7.bytesRead
            int r2 = r2 + r9
            r7.bytesRead = r2
            int r9 = r7.bytesRead
            int r2 = r7.totalSectionLength
            if (r9 != r2) goto L_0x001d
            boolean r9 = r7.sectionSyntaxIndicator
            if (r9 == 0) goto L_0x00e5
            com.google.android.exoplayer2.util.ParsableByteArray r9 = r7.sectionData
            byte[] r9 = r9.data
            int r9 = com.google.android.exoplayer2.util.Util.crc(r9, r3, r2, r0)
            if (r9 == 0) goto L_0x00db
            r7.waitingForPayloadStart = r1
            return
        L_0x00db:
            com.google.android.exoplayer2.util.ParsableByteArray r9 = r7.sectionData
            int r1 = r7.totalSectionLength
            int r1 = r1 + -4
            r9.reset(r1)
            goto L_0x00ea
        L_0x00e5:
            com.google.android.exoplayer2.util.ParsableByteArray r9 = r7.sectionData
            r9.reset(r2)
        L_0x00ea:
            com.google.android.exoplayer2.extractor.ts.SectionPayloadReader r9 = r7.reader
            com.google.android.exoplayer2.util.ParsableByteArray r1 = r7.sectionData
            r9.consume(r1)
            goto L_0x001b
        L_0x00f3:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.extractor.ts.SectionReader.consume(com.google.android.exoplayer2.util.ParsableByteArray, boolean):void");
    }

    public void init(TimestampAdjuster timestampAdjuster, ExtractorOutput extractorOutput, TsPayloadReader.TrackIdGenerator trackIdGenerator) {
        this.reader.init(timestampAdjuster, extractorOutput, trackIdGenerator);
        this.waitingForPayloadStart = true;
    }

    public void seek() {
        this.waitingForPayloadStart = true;
    }
}
